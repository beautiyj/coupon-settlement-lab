package com.jyituaeb.couponsettlementlab.coupon.application.port.out;

import com.jyituaeb.couponsettlementlab.coupon.domain.CouponEvent;
import java.util.Optional;

/**
 * [아웃바운드 포트 - 영속성/이벤트]
 * 목적: 쿠폰 이벤트(CouponEvent)를 조회·저장하고 DB 락 기반 동시성 제어 시 이벤트를 잠금 조회하기 위한 인터페이스
 * 역할: 애플리케이션 서비스가 MySQL/JPA 기술에 직접 의존하지 않고
 *       이벤트 도메인 모델 단위로 데이터를 주고받을 수 있도록 처리
 */

// 쿠폰 이벤트 저장·조회·잠금 포트
// 이벤트 조회, 저장, 그리고 DB 비관적 락(PESSIMISTIC_LOCK) 전략을 쓸 때
// 필요한 조회/차감 등을 추상화한 포트
public interface CouponEventPort {

    Optional<CouponEvent> findById(Long couponEventId);

    /**
     * DB 비관적 락(예: SELECT ... FOR UPDATE)을 걸어 쿠폰 이벤트 조회
     * PESSIMISTIC_LOCK 전략에서 잔여 수량 경합을 제어할 때 사용하는 로직
     */
    Optional<CouponEvent> findByIdWithLock(Long couponEventId);

    CouponEvent save(CouponEvent couponEvent);
}