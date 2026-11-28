package com.jyituaeb.couponsettlementlab.coupon.application.port.in;

import java.time.LocalDateTime;

/**
 * [인바운드 포트 - 유스케이스]
 * 목적: 실패했거나 재시도 시점이 된 후속 정산/알림 작업(SettlementTask)을 재처리하기 위한 진입점 정의
 * 역할: 스케줄러(SettlementRetryScheduler)나 관리자 재시도 API 등 서로 다른 호출자가
 *     동일한 재처리 유스케이스를 호출할 수 있도록 인터페이스를 정의하고 실제 구현체(RetrySettlementService)에 의존하지 않도록 만들기
 */

// 후속 정산/알림 작업 재처리 유스케이스
// 외부 정산/알림 연동 실패 등으로 쌓인 작업(SettlementTask)을 재시도할 때
// 스케줄러나 관리자 수동 호출에서 공통으로 쓰는 진입점 인터페이스
public interface RetrySettlementUseCase {

    /**
     * 기준 시각(now) 기준으로 재시도 대상인 후속 작업을 배치로 재처리 및
     * 처리 완료된 건수 반환 역할
     */
    int retryDueTasks(LocalDateTime now);

    /**
     * 특정 정산 작업(taskId)을 지정 시각(now) 기준으로 즉시 재시도
     */
    void retryTask(Long taskId, LocalDateTime now);
}