package com.jyituaeb.couponsettlementlab.coupon.application.port.in;

import com.jyituaeb.couponsettlementlab.coupon.application.dto.IssueCouponCommand;
import com.jyituaeb.couponsettlementlab.coupon.application.dto.IssueCouponResult;

/**
 * [인바운드 포트 - 유스케이스]
 * 목적: 사용자의 선착순 쿠폰 발급 요청을 처리하기 위한 비즈니스 진입점 정의
 * 역할: 웹 컨트롤러 등 외부 입력 계층이 구체 서비스 구현체(IssueCouponService)에 직접 의존하지 않고
 *       정의된 인터페이스(Command -> Result)를 통해 발급 기능을 호출하도록 만들기
 */

// 선착순 쿠폰 발급 유스케이스
// 쿠폰 발급 요청(IssueCouponCommand)을 받아 멱등성·동시성 제어 전략·잔여 수량 검증을 거친 뒤
// 발급 결과(IssueCouponResult)를 반환하는 계약
public interface IssueCouponUseCase {

    /**
     * 쿠폰 발급 실행
     * - idempotencyKey 기준 중복 요청 방어
     * - strategy(REDIS_ATOMIC, PESSIMISTIC_LOCK 등)에 따른 동시성 제어 및 잔여 수량 검증
     * - 발급 내역 및 후속 정산/알림 작업 생성 흐름과 연결
     */
    IssueCouponResult issue(IssueCouponCommand command);
}