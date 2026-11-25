package com.jyituaeb.couponsettlementlab.coupon.adapter.in.scheduler;

import org.springframework.scheduling.annotation.Scheduled;

public class SettlementRetryScheduler {
    @Scheduled(fixedDelay = 60000)
    public void retry() {
    }
}
