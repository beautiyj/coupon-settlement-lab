package com.jyituaeb.couponsettlementlab.coupon.application.port.out;

import com.jyituaeb.couponsettlementlab.coupon.domain.SettlementTask;

/**
 * [아웃바운드 포트 - 외부 정산/알림 연동]
 * 목적: 외부 정산 시스템 또는 알림 발송 클라이언트를 호출하기 위한 추상화 인터페이스
 * 역할: 외부 시스템 장애나 지연이 발생하더라도 애플리케이션 핵심 로직을 보호하고
 *       어댑터(ExternalSettlementAdapter)에서 장애 격리·재시도·폴백을 다룰 수 있도록 추상화
 */

// 외부 정산/알림 연동 클라이언트 포트
// 쿠폰 발급 후 외부 정산 API나 알림 발송 시스템을 호출할 때 쓰는 포트
// 장애 격리(Resilience4j 서킷브레이커/타임아웃/재시도 등)를 적용하기 쉽게 외부 호출 경계를 분리하는 용도임
public interface SettlementClientPort {

    /**
     * 후속 정산/알림 작업을 외부 시스템으로 전송하는 역할
     * 실패 시 예외를 던져 호출 측(서비스/스케줄러)에서 재시도 및 실패 이력 기록. 로그같은 역할?
     */
    void dispatch(SettlementTask task);
}