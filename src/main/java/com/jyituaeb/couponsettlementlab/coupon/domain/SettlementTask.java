package com.jyituaeb.couponsettlementlab.coupon.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 쿠폰 발급 뒤 이어지는 외부 정산/알림 등의 후속 작업과 재시도 이력을 관리하는 도메인 모델
// 외부 연동 실패 시 재시도 횟수·다음 재시도 시각·마지막 에러·DEAD_LETTER 전환 규칙을 보호하는 목적

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SettlementTask {

    private Long id;                    // 후속 작업 고유 번호 (DB PK 대응)
    private Long couponIssueId;         // 어떤 쿠폰 발급 내역에 대한 작업인지 연결하는 ID (settlement_tasks.coupon_issue_id 대응)
    private String taskType;            // 후속 작업 유형 (settlement_tasks.task_type 대응)
    private TaskStatus status;          // 작업 진행 상태 (settlement_tasks.status 대응)
    private int retryCount;             // 재시도 횟수 (settlement_tasks.retry_count 대응)
    private LocalDateTime nextRetryAt;  // 다음 재시도 예정 시각 (settlement_tasks.next_retry_at 대응)
    private String lastError;           // 마지막 실패 원인/메시지 (settlement_tasks.last_error 대응)
    private LocalDateTime createdAt;    // 생성 시각 (settlement_tasks.created_at 대응)
    private LocalDateTime updatedAt;    // 수정 시각 (settlement_tasks.updated_at 대응)

    // 작업 상태와 재시도 횟수 등 기본 무결성을 검증하는 생성자
    public SettlementTask(
            Long id,
            Long couponIssueId,
            String taskType,
            TaskStatus status,
            int retryCount,
            LocalDateTime nextRetryAt,
            String lastError,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        if (retryCount < 0) {
            throw new IllegalArgumentException("retryCount must be >= 0");
        }
        if (taskType == null || taskType.isBlank()) {
            throw new IllegalArgumentException("taskType must not be blank");
        }
        this.id = id;
        this.couponIssueId = Objects.requireNonNull(couponIssueId, "couponIssueId must not be null");
        this.taskType = taskType;
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.retryCount = retryCount;
        this.nextRetryAt = nextRetryAt;
        this.lastError = lastError;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // 후속 작업을 처음 등록할 때 PENDING(재시도 0회) 상태로 만드는 팩토리 메서드
    public static SettlementTask createPending(
            Long couponIssueId,
            String taskType,
            LocalDateTime now
    ) {
        return new SettlementTask(
                null,
                couponIssueId,
                taskType,
                TaskStatus.PENDING,
                0,
                now,
                null,
                now,
                now
        );
    }

    // 작업 실행 시작 상태로 변경
    public void markProcessing(LocalDateTime now) {
        this.status = TaskStatus.PROCESSING;
        this.updatedAt = now;
    }

    // 작업 완료 상태로 변경하고 다음 재시도 시각과 에러 메시지를 초기화
    public void markCompleted(LocalDateTime now) {
        this.status = TaskStatus.COMPLETED;
        this.nextRetryAt = null;
        this.lastError = null;
        this.updatedAt = now;
    }

    // 실패 기록: 재시도 횟수를 늘리고, 최대 횟수에 도달하면 DEAD_LETTER로 전환
    public void recordFailure(String errorMessage, LocalDateTime nextRetryAt, int maxRetryCount, LocalDateTime now) {
        this.retryCount += 1;
        this.lastError = errorMessage;
        this.updatedAt = now;
        if (this.retryCount >= maxRetryCount) {
            this.status = TaskStatus.DEAD_LETTER;
            this.nextRetryAt = null;
        } else {
            this.status = TaskStatus.FAILED;
            this.nextRetryAt = nextRetryAt;
        }
    }
}