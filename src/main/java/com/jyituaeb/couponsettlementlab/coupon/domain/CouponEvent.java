package com.jyituaeb.couponsettlementlab.coupon.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 쿠폰 이벤트(행사 정보 + 총수량/잔여수량 + 진행 기간/상태)를 표현하는 도메인 모델(=엔티티)
// JPA/Spring 의존성을 넣지 않은 순수 자바 클래스. 도메인의 클래스들은 순수 자바 객체
// 이벤트 수량·상태 규칙 보호 역할

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponEvent {

    private Long id;                    // 이벤트 고유 번호 (DB PK 대응)
    private String title;               // 이벤트 제목 (coupon_events.title 대응)
    private EventStatus status;         // 현재 이벤트 상태 (coupon_events.status 대응)
    private int totalQuantity;          // 전체 발행 수량 (coupon_events.total_quantity 대응)
    private int remainingQuantity;      // 현재 남은 수량 / 잔여 수량 (coupon_events.remaining_quantity 대응)
    private LocalDateTime startsAt;     // 이벤트 시작 시각 (coupon_events.starts_at 대응)
    private LocalDateTime endsAt;       // 이벤트 종료 시각 (coupon_events.ends_at 대응)
    private LocalDateTime createdAt;    // 생성 시각 (coupon_events.created_at 대응)
    private LocalDateTime updatedAt;    // 수정 시각 (coupon_events.updated_at 대응)

    // 전체 필드를 받아 도메인 객체를 생성하면서 수량·기간 유효성을 검증하는 생성자
    public CouponEvent(
            Long id,
            String title,
            EventStatus status,
            int totalQuantity,
            int remainingQuantity,
            LocalDateTime startsAt,
            LocalDateTime endsAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        validateQuantities(totalQuantity, remainingQuantity);
        validatePeriod(startsAt, endsAt);
        this.id = id;
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.totalQuantity = totalQuantity;
        this.remainingQuantity = remainingQuantity;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // 새 쿠폰 이벤트를 처음 만들 때 쓰는 팩토리 메서드 (잔여 수량은 총수량과 같게 시작)
    public static CouponEvent create(
            String title,
            int totalQuantity,
            LocalDateTime startsAt,
            LocalDateTime endsAt,
            LocalDateTime now
    ) {
        return new CouponEvent(
                null,
                title,
                EventStatus.SCHEDULED,
                totalQuantity,
                totalQuantity,
                startsAt,
                endsAt,
                now,
                now
        );
    }

    // 현재 시각 기준으로 이벤트가 열려 있고 + 남은 수량 존재 + 기간 내인지 확인
    public boolean isIssuableAt(LocalDateTime now) {
        return status == EventStatus.OPEN
                && remainingQuantity > 0
                && !now.isBefore(startsAt)
                && !now.isAfter(endsAt);
    }

    // 쿠폰 발급 시 잔여 수량을 차감하는 역할. 0이 되면 SOLD_OUT 상태로 전환
    public void decreaseRemainingQuantity(int quantity, LocalDateTime now) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        if (status != EventStatus.OPEN) {
            throw new IllegalStateException("coupon event is not open: " + status);
        }
        if (remainingQuantity < quantity) {
            throw new IllegalStateException("insufficient remaining quantity");
        }
        this.remainingQuantity -= quantity;
        if (this.remainingQuantity == 0) {
            this.status = EventStatus.SOLD_OUT;
        }
        this.updatedAt = now;
    }

    // 발급 취소/롤백 등으로 잔여 수량을 되돌리고 매진 상태였다면 다시 OPEN으로 복구
    public void restoreRemainingQuantity(int quantity, LocalDateTime now) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        int nextRemaining = this.remainingQuantity + quantity;
        if (nextRemaining > this.totalQuantity) {
            throw new IllegalStateException("remaining quantity exceeds total quantity");
        }
        this.remainingQuantity = nextRemaining;
        if (this.status == EventStatus.SOLD_OUT && this.remainingQuantity > 0) {
            this.status = EventStatus.OPEN;
        }
        this.updatedAt = now;
    }

    // 총수량과 잔여수량이 음수가 아니고 잔여수량이 총수량을 넘지 않는지 검증
    private void validateQuantities(int total, int remaining) {
        if (total < 0) {
            throw new IllegalArgumentException("totalQuantity must be >= 0");
        }
        if (remaining < 0 || remaining > total) {
            throw new IllegalArgumentException("remainingQuantity must be between 0 and totalQuantity");
        }
    }

    // 종료 시각이 시작 시각보다 앞서지 않는지 검증
    private void validatePeriod(LocalDateTime start, LocalDateTime end) {
        Objects.requireNonNull(start, "startsAt must not be null");
        Objects.requireNonNull(end, "endsAt must not be null");
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("endsAt must not be before startsAt");
        }
    }
}