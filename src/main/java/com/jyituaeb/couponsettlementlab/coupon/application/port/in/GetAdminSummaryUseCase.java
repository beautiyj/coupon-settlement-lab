package com.jyituaeb.couponsettlementlab.coupon.application.port.in;

import com.jyituaeb.couponsettlementlab.coupon.application.dto.AdminSummaryResult;

/**
 * [인바운드 포트 - 유스케이스]
 * 목적: 관리자 모니터링 화면(/admin) 및 운영 조회를 위한 요약 지표 조회 진입점 정의
 * 역할: 쿠폰 이벤트의 수량/상태, 발급 건수, 후속 정산 태스크(PENDING/COMPLETED/FAILED/DEAD_LETTER)
 *       집계 결과를 AdminSummaryResult 형태로 조립해 반환하는 기능 명세
 */

// 관리자 운영 요약 조회 유스케이스
// /admin 화면이나 운영 점검에서 특정 쿠폰 이벤트의 잔여 수량, 누적 발급 건수,
// 후속 정산 작업 상태별 건수(AdminSummaryResult)를 조회하는 계약
public interface GetAdminSummaryUseCase {

    /**
     * 지정한 쿠폰 이벤트의 운영 요약 지표 조회
     */
    AdminSummaryResult getSummary(Long couponEventId);
}