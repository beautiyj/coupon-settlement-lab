package com.jyituaeb.couponsettlementlab.coupon.domain;

// 개별 쿠폰 발급 요청/내역의 처리 상태를 나타내는 enum

public enum IssueStatus {
    PENDING,    // 발급 처리 중(대기/진행 중) 상태
    ISSUED,     // 쿠폰 발급 완료 상태
    FAILED,     // 발급 처리 실패 상태
    CANCELLED   // 발급 취소 상태
}