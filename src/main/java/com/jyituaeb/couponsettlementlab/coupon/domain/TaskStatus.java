package com.jyituaeb.couponsettlementlab.coupon.domain;

// 쿠폰 발급 후 이어지는 정산·알림 등 후속 작업의 상태를 나타내는 enum
// 외부 연동 실패 시 재시도 및 최종 실패(DEAD_LETTER)를 구분하기 위해 사용

public enum TaskStatus {
    PENDING,      // 처리 대기 중 상태
    PROCESSING,   // 현재 처리 중인 상태
    COMPLETED,    // 후속 작업 완료 상태
    FAILED,       // 실패했으나 재시도 대상인 상태
    DEAD_LETTER   // 최대 재시도 횟수를 넘겨 별도 확인/격리가 필요한 상태
}