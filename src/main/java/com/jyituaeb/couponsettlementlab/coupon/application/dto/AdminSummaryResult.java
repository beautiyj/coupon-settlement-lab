package com.jyituaeb.couponsettlementlab.coupon.application.dto;

import com.jyituaeb.couponsettlementlab.coupon.domain.EventStatus;
import lombok.Builder;

/*
 /admin 운영 모니터링 화면 및 GetAdminSummaryUseCase에서
 이벤트 재고 현황, 발급 건수, 정산/알림 후속 작업의 상태별 집계를 한 번에 내려줄 때 사용하는 DTO

 관리자 모니터링 유스케이스(GetAdminSummaryUseCase)의 집계 결과를 담는 출력 DTO
 React 운영 화면(/admin)에서 이벤트 잔여 수량, 누적 발급 수,
 후속 정산 작업의 상태별 건수(대기/완료/실패/DEAD_LETTER)를 한눈에 조회하기 위한 디티오
 */
@Builder
public record AdminSummaryResult(
        Long couponEventId,             // 조회 대상 쿠폰 이벤트 ID
        String eventTitle,              // 쿠폰 이벤트 제목
        EventStatus eventStatus,        // 현재 이벤트 상태 (OPEN, SOLD_OUT, CLOSED 등)
        int totalQuantity,              // 전체 발행 한도 수량
        int remainingQuantity,          // 현재 남은 잔여 수량
        long issuedCount,               // 정상 발급 완료(ISSUED) 건수
        long pendingTaskCount,          // 후속 작업 대기/진행 중(PENDING, PROCESSING) 건수
        long completedTaskCount,        // 후속 작업 완료(COMPLETED) 건수
        long failedTaskCount,           // 일시 실패 후 재시도 대기(FAILED) 건수
        long deadLetterTaskCount        // 최대 재시도 초과로 격리된(DEAD_LETTER) 건수
) {
}