package com.jyituaeb.couponsettlementlab.coupon.application.dto;

import com.jyituaeb.couponsettlementlab.coupon.domain.CouponIssue;
import com.jyituaeb.couponsettlementlab.coupon.domain.IssueStatus;
import java.time.LocalDateTime;
import lombok.Builder;

/*
 쿠폰 발급 처리가 끝난 뒤 서비스 계층에서 컨트롤러로 반환하는 결과 객체
 도메인 엔티티(CouponIssue)를 외부로 직접 노출하지 않고 필요한 응답 정보와 중복 요청 여부를 함께 담아 전달하는 역할

 쿠폰 발급 유스케이스(IssueCouponUseCase) 처리 결과를 반환하는 출력 DTO
 도메인 모델(CouponIssue)을 컨트롤러/외부에 직접 노출하지 않기 위해 변환하여 사용
 신규 발급인지 기존 멱등성 키에 의한 재응답인지(duplicated) 구분 가능한 목적의 디티오
 */
@Builder
public record IssueCouponResult(
        Long issueId,               // 발급 내역 고유 ID (coupon_issues.id)
        Long couponEventId,         // 발급된 쿠폰 이벤트 ID
        Long userId,                // 발급받은 사용자 ID
        String idempotencyKey,      // 처리에 사용된 멱등성 키
        String strategy,            // 실제 적용된 동시성 제어 전략 (REDIS_ATOMIC, PESSIMISTIC_LOCK 등)
        IssueStatus status,         // 발급 처리 상태 (ISSUED, PENDING, FAILED 등)
        int remainingQuantity,      // 발급 처리 직후 남은 잔여 수량 (실험 및 모니터링 확인용)
        boolean duplicated,         // true: 동일 멱등성 키로 이미 처리된 기존 결과 반환 / false: 신규 발급 처리
        LocalDateTime issuedAt      // 발급 처리 시각
) {
    /**
     * 도메인 모델(CouponIssue)로부터 결과 DTO를 생성하는 정적 팩토리 메서드.
     */
    public static IssueCouponResult from(CouponIssue issue, int remainingQuantity, boolean duplicated) {
        return IssueCouponResult.builder()
                .issueId(issue.getId())
                .couponEventId(issue.getCouponEventId())
                .userId(issue.getUserId())
                .idempotencyKey(issue.getIdempotencyKey())
                .strategy(issue.getStrategy())
                .status(issue.getStatus())
                .remainingQuantity(remainingQuantity)
                .duplicated(duplicated)
                .issuedAt(issue.getIssuedAt())
                .build();
    }
}