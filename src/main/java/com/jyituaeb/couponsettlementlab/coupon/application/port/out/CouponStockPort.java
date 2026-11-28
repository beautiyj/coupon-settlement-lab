package com.jyituaeb.couponsettlementlab.coupon.application.port.out;

import java.time.Duration;
import java.util.Optional;

/**
 * [아웃바운드 포트 - Redis 재고/멱등성]
 * 목적: Redis 기반의 원자적 재고 차감·복구 및 요청 키 선점/조회 기능을 추상화하는 인터페이스
 * 역할: 서비스 계층이 Spring Data Redis나 Lua 스크립트 세부 구현을 몰라도
 *       동시성 제어 전략(REDIS_ATOMIC)을 일관된 메서드로 호출할 수 있도록 처리하는 로직
 */
// Redis 재고 차감·복구·멱등성 키 제어 포트
// REDIS_ATOMIC 전략을 쓸 때 Redis 원자적 연산으로
// 잔여 수량을 차감/복구하거나멱등성 키를 선점/조회하는 포트
public interface CouponStockPort {

    /**
     * Redis에서 지정 수량만큼 원자적으로 잔여 수량 차감
     * 성공 시 차감 후 남은 수량을 반환하고 재고가 부족하거나 키가 없으면 empty를 반환하는 역할
     */
    Optional<Integer> tryDecrementStock(Long couponEventId, int quantity);

    /**
     * 차감된 Redis 재고를 되돌리는 롤백 로직(발급 실패/롤백 시 보상 처리용)
     */
    void restoreStock(Long couponEventId, int quantity);

    /**
     * 멱등성 키를 TTL과 함께 원자적으로 등록
     * 처음 등록되면 true, 이미 존재하면 false 반환
     */
    boolean tryAcquireIdempotencyKey(String idempotencyKey, String value, Duration ttl);

    Optional<String> getIdempotencyValue(String idempotencyKey);
}