package com.jyituaeb.couponsettlementlab.coupon.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/*
 컨트롤러(adapter/in/web)에서 받은 요청을
 애플리케이션 서비스(IssueCouponUseCase)로 넘길 때 사용하는 입력 객체

 쿠폰 발급 유스케이스(IssueCouponUseCase) 실행을 위한 입력 커맨드 DTO
 웹 요청(IssueCouponRequest)과 애플리케이션 계층의 결합도를 끊고
 멱등성 키와 동시성 제어 전략을 서비스 계층으로 명확히 전달하기 위해 사용하는 디티오
 */

// @NotNull, @NotBlank, @Positive, @Size를 선언해 두면 스프링의 @Valid와 연동해 잘못된 입력값이 유스케이스 내부 로직으로 들어오기 전에 거르기 가능
@Builder
public record IssueCouponCommand(
        @NotNull(message = "쿠폰 이벤트 ID는 필수입니다.")
        @Positive(message = "쿠폰 이벤트 ID는 양수여야 합니다.")
        Long couponEventId,     // 발급 대상 쿠폰 이벤트 ID

        @NotNull(message = "사용자 ID는 필수입니다.")
        @Positive(message = "사용자 ID는 양수여야 합니다.")
        Long userId,            // 쿠폰을 요청한 사용자 ID

        @NotBlank(message = "멱등성 키는 필수입니다.")
        @Size(max = 100, message = "멱등성 키는 100자를 초과할 수 없습니다.")
        String idempotencyKey,  // 중복 요청 판별 및 재시도 방어용 멱등성 키

        @NotBlank(message = "동시성 제어 전략은 필수입니다.")
        @Size(max = 30, message = "전략명은 30자를 초과할 수 없습니다.")
        String strategy         // 적용할 동시성 제어 전략 (예: REDIS_ATOMIC, PESSIMISTIC_LOCK)
) {
}