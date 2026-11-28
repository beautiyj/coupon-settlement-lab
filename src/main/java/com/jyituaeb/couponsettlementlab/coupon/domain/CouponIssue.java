package com.jyituaeb.couponsettlementlab.coupon.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 어떤 이벤트에서/누가/어떤 멱등성 키와 동시성 전략으로/어떤 상태로 쿠폰을 발급받았는지를 담는 도메인 모델
// 중복 요청 판별(=멱등성)과 전략별 발급 결과 이력을 관리하기 위해 사용

// 멱등성(Idempotency): "같은 요청이 여러 번 와도 결과가 한 번 처리한 것과 같게 보장하는 것"
// 예: 사용자가 쿠폰 발급 버튼을 따닥 두 번 눌렀거나, 네트워크가 끊겨 재시도했을 때 같은 idempotencyKey나 (couponEventId, userId)로 이미 발급된 건인지 판별해 쿠폰이 두 번 발급되지 않게 막는 것이에요.

// 동시성(Concurrency): "여러 요청이 같은 시점에 동시에 몰릴 때 데이터가 꼬이지 않고 안전하게 처리하는 것"
// 예: 선착순 100장 쿠폰에 1,000명이 동시에 덤벼들 때, 잔여 수량이 음수가 되거나(-1, -2) 100장 넘게 초과 발급되지 않도록 순서나 잠금(락/원자적 연산)으로 제어하는 것이에요.

/*  <동시성 제어와 락>
    서로 다른 사용자/요청끼리는 병렬로 빠르게 처리하되, 공유 자원(잔여 수량, 같은 사용자/키)을 건드릴 때만 충돌이 안 나게 제어하는 걸 실습하는 용도!
    지금 프로젝트에서 REDIS_ATOMIC이나 PESSIMISTIC_LOCK 같은 전략을 비교하려는 이유가 멱등성+동시성 제어.

    - 락/제어를 너무 넓게 잡으면: 안전하긴 하지만 대기 줄이 길어져서 처리 속도가 느려질 수 있고
    - 제어를 너무 느슨하게 잡으면: 빠르긴 하지만 동시에 들어온 요청끼리 잔여 수량을 덮어써서 초과 발급 같은 동시성 문제 발생
*/

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponIssue {

    private Long id;                    // 발급 내역 고유 번호 (DB PK 대응)
    private Long couponEventId;         // 어떤 쿠폰 이벤트인지 식별하는 ID (coupon_issues.coupon_event_id 대응)
    private Long userId;                // 쿠폰을 요청/발급받은 사용자 ID (coupon_issues.user_id 대응)
    private String idempotencyKey;      // 중복 요청 판별용 멱등성 키 (coupon_issues.idempotency_key 대응)
    private String strategy;            // 적용된 동시성 제어 전략명 (coupon_issues.strategy 대응, 예: REDIS_ATOMIC, PESSIMISTIC_LOCK)
    private IssueStatus status;         // 발급 처리 상태 (coupon_issues.status 대응)
    private LocalDateTime issuedAt;     // 발급 요청/처리 시각 (coupon_issues.issued_at 대응)

    // 필수 값(이벤트 ID, 사용자 ID, 멱등성 키, 전략, 상태, 시각)을 검증하며 생성하는 생성자
    public CouponIssue(
            Long id,
            Long couponEventId,
            Long userId,
            String idempotencyKey,
            String strategy,
            IssueStatus status,
            LocalDateTime issuedAt
    ) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("idempotencyKey must not be blank");
        }
        if (strategy == null || strategy.isBlank()) {
            throw new IllegalArgumentException("strategy must not be blank");
        }
        this.id = id;
        this.couponEventId = Objects.requireNonNull(couponEventId, "couponEventId must not be null");
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.idempotencyKey = idempotencyKey;
        this.strategy = strategy;
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.issuedAt = Objects.requireNonNull(issuedAt, "issuedAt must not be null");
    }

    // 발급 처리를 시작할 때 PENDING 상태로 만드는 팩토리 메서드
    public static CouponIssue createPending(
            Long couponEventId,
            Long userId,
            String idempotencyKey,
            String strategy,
            LocalDateTime issuedAt
    ) {
        return new CouponIssue(
                null,
                couponEventId,
                userId,
                idempotencyKey,
                strategy,
                IssueStatus.PENDING,
                issuedAt
        );
    }

    // 쿠폰 발급 완료 상태로 변경
    public void markIssued() {
        this.status = IssueStatus.ISSUED;
    }

    // 쿠폰 발급 실패 상태로 변경
    public void markFailed() {
        this.status = IssueStatus.FAILED;
    }

    // 쿠폰 발급 취소 상태로 변경
    public void cancel() {
        this.status = IssueStatus.CANCELLED;
    }
}