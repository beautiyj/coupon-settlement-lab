
/*
- 멱등성 보장(중복 발급 방지): coupon_issues에 idempotency_key 컬럼과
UNIQUE 제약을 두고 (coupon_event_id, user_id) 유니크 제약도 함께 걸어서
같은 키나 같은 사용자의 중복 발급 요청이 몰려도 DB가 마지막 방어선으로 중복 저장 막아주기

- 동시성 전략 기록 및 비교: 요청이 어떤 전략(strategy,
예: REDIS_ATOMIC vs PESSIMISTIC_LOCK)으로 처리되었는지 남겨
두 전략의 동작과 결과를 비교·설명하기 쉽게 하려고

- 잔여 수량 정합성 검증: total_quantity, remaining_quantity와 음수/초과 방지 CHECK 제약을 둬서
동시성 제어 실험 중 재고가 0 밑으로 떨어지거나 꼬이지 않는지 확인 용도

- 실패 복구·재시도 추적: settlement_tasks에 retry_count, next_retry_at, last_error,
상태 인덱스 등을 둬서 외부 연동 실패 시 재시도와 DEAD_LETTER 처리 흐름을 다룰 수 있도록 처리
*/


CREATE DATABASE IF NOT EXISTS coupon_lab
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE coupon_lab;

CREATE TABLE IF NOT EXISTS coupon_events (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(120) NOT NULL,
    status VARCHAR(20) NOT NULL,
    total_quantity INT NOT NULL,
    remaining_quantity INT NOT NULL,
    starts_at DATETIME NOT NULL,
    ends_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT chk_coupon_events_total_qty CHECK (total_quantity >= 0),
    CONSTRAINT chk_coupon_events_remaining_qty CHECK (remaining_quantity >= 0),
    CONSTRAINT chk_coupon_events_stock_bound CHECK (remaining_quantity <= total_quantity),
    CONSTRAINT chk_coupon_events_period CHECK (ends_at >= starts_at),
    INDEX idx_coupon_events_status (status),
    INDEX idx_coupon_events_period (starts_at, ends_at)
);

CREATE TABLE IF NOT EXISTS coupon_issues (
    id BIGINT NOT NULL AUTO_INCREMENT,
    coupon_event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    idempotency_key VARCHAR(100) NOT NULL,
    strategy VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL,
    issued_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_coupon_issues_idempotency_key UNIQUE (idempotency_key),
    CONSTRAINT uk_coupon_issues_event_user UNIQUE (coupon_event_id, user_id),
    CONSTRAINT fk_coupon_issues_event FOREIGN KEY (coupon_event_id)
        REFERENCES coupon_events (id),
    INDEX idx_coupon_issues_status (status),
    INDEX idx_coupon_issues_user (user_id),
    INDEX idx_coupon_issues_strategy (strategy)
);

CREATE TABLE IF NOT EXISTS settlement_tasks (
    id BIGINT NOT NULL AUTO_INCREMENT,
    coupon_issue_id BIGINT NOT NULL,
    task_type VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL,
    retry_count INT NOT NULL DEFAULT 0,
    next_retry_at DATETIME NULL,
    last_error VARCHAR(500) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_settlement_tasks_issue_type UNIQUE (coupon_issue_id, task_type),
    CONSTRAINT fk_settlement_tasks_issue FOREIGN KEY (coupon_issue_id)
        REFERENCES coupon_issues (id),
    CONSTRAINT chk_settlement_tasks_retry_count CHECK (retry_count >= 0),
    INDEX idx_settlement_tasks_status_retry (status, next_retry_at)
);