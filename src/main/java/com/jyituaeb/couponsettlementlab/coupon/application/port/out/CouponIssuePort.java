package com.jyituaeb.couponsettlementlab.coupon.application.port.out;

import com.jyituaeb.couponsettlementlab.coupon.domain.CouponIssue;
import com.jyituaeb.couponsettlementlab.coupon.domain.IssueStatus;
import java.util.Optional;

/**
 * [아웃바운드 포트 - 영속성/발급내역]
 * 목적: 쿠폰 발급 이력(CouponIssue) 저장 및 멱등성 키·사용자 기준 중복 조회, 상태별 집계 인터페이스
 * 역할: 동일 idempotencyKey 재요청 시 기존 발급 내역을 찾아 반환하거나
 *       DB 유니크 제약 위반 등 중복 상황을 판별하는 기반 제공
 */

// 쿠폰 발급 내역 및 멱등성 조회/저장 포트
// idempotencyKey나 (couponEventId, userId) 기준
// 중복 발급 여부 확인, 발급 내역 저장, 발급 건수 집계 등을 담당하는 포트
public interface CouponIssuePort {

    Optional<CouponIssue> findByIdempotencyKey(String idempotencyKey);

    Optional<CouponIssue> findByCouponEventIdAndUserId(Long couponEventId, Long userId);

    CouponIssue save(CouponIssue couponIssue);

    long countByCouponEventIdAndStatus(Long couponEventId, IssueStatus status);
}