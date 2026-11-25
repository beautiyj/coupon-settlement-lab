CREATE DATABASE IF NOT EXISTS coupon_lab
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE coupon_lab;

CREATE TABLE IF NOT EXISTS coupon_events (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    total_stock INT NOT NULL,
    issued_stock INT NOT NULL DEFAULT 0,
    starts_at DATETIME NOT NULL,
    ends_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT chk_coupon_events_stock CHECK (total_stock >= 0),
    CONSTRAINT chk_coupon_events_issued_stock CHECK (issued_stock >= 0),
    CONSTRAINT chk_coupon_events_period CHECK (ends_at >= starts_at),
    INDEX idx_coupon_events_status (status),
    INDEX idx_coupon_events_period (starts_at, ends_at)
);

CREATE TABLE IF NOT EXISTS coupon_issues (
    id BIGINT NOT NULL AUTO_INCREMENT,
    event_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    issued_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_coupon_issues_event_member UNIQUE (event_id, member_id),
    CONSTRAINT fk_coupon_issues_event FOREIGN KEY (event_id)
        REFERENCES coupon_events (id),
    INDEX idx_coupon_issues_status (status),
    INDEX idx_coupon_issues_member (member_id)
);

CREATE TABLE IF NOT EXISTS settlement_tasks (
    id BIGINT NOT NULL AUTO_INCREMENT,
    issue_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    attempt_count INT NOT NULL DEFAULT 0,
    next_retry_at DATETIME NULL,
    last_error VARCHAR(500) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_settlement_tasks_issue UNIQUE (issue_id),
    CONSTRAINT fk_settlement_tasks_issue FOREIGN KEY (issue_id)
        REFERENCES coupon_issues (id),
    CONSTRAINT chk_settlement_tasks_attempt CHECK (attempt_count >= 0),
    INDEX idx_settlement_tasks_status_retry (status, next_retry_at)
);
