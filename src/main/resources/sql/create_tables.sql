CREATE TABLE IF NOT EXISTS reports (
    REPORT_ID VARCHAR(36) PRIMARY KEY,
    STATUS VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    REPORTER VARCHAR(36) NOT NULL,
    ASSIGNEE VARCHAR(36),
    `TIMESTAMP` BIGINT NOT NULL,
    REASON VARCHAR(100) NOT NULL
);