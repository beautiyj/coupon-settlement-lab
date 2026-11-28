package com.jyituaeb.couponsettlementlab.coupon.domain;

// 쿠폰 이벤트(행사)가 가질 수 있는 상태들을 정해 둔 enum
// 0, 1이나 오타 나기 쉬운 문자열 대신 의미 있는 이름으로 상태 관리하는 용도의 enum 파일

public enum EventStatus {
    SCHEDULED,  // 오픈 전 대기 상태
    OPEN,       // 현재 발급 진행 중인 상태
    CLOSED,     // 이벤트 기간 종료 등으로 마감된 상태
    SOLD_OUT,   // 준비된 수량이 모두 소진된 상태
    CANCELLED   // 이벤트가 취소된 상태
}