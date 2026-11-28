package com.jyituaeb.couponsettlementlab.coupon.application.port.out;

import com.jyituaeb.couponsettlementlab.coupon.domain.SettlementTask;
import com.jyituaeb.couponsettlementlab.coupon.domain.TaskStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * [아웃바운드 포트 - 영속성/후속정산작업]
 * 목적: 후속 정산/알림 작업(SettlementTask)의 저장, 재시도 대상 조회, 상태별 건수 집계 인터페이스
 * 역할: 발급 트랜잭션과 분리된 후속 작업 상태를 관리하고
 *       재시도 스케줄러(SettlementRetryScheduler)가 처리할 대상을 가져올 수 있도록 처리하는 역할
 */

// 후속 정산/알림 작업 저장·조회·집계 포트
// 발급 이후 생성되는 SettlementTask를 저장하고
// 재시도 대상 조회 및 관리자 요약용 상태별 건수 집계를 수행하는 포트
public interface SettlementTaskPort {

    Optional<SettlementTask> findById(Long taskId);

    SettlementTask save(SettlementTask task);

    /**
     * 지정한 상태(예: PENDING, FAILED)이면서 nextRetryAt <= now 인 재시도 대상 목록을 조회하는 로직
     */
    List<SettlementTask> findDueTasks(List<TaskStatus> statuses, LocalDateTime now, int limit);

    long countByCouponEventIdAndStatus(Long couponEventId, TaskStatus status);
}