package com.github.zflxw.reportreborn.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Report {
    @NotNull
    private final String reportId;

    @NotNull
    private final ReportStatus reportStatus;

    @NotNull
    private final String reporter;

    @Nullable
    private final String assignee;

    @NotNull
    private final String reason;

    private final long timestamp;

    public Report(@NotNull String reportId, @NotNull ReportStatus reportStatus, @NotNull String reporter, @Nullable String assignee, @NotNull String reason, long timestamp) {
        this.reportId = reportId;
        this.reportStatus = reportStatus;
        this.reporter = reporter;
        this.assignee = assignee;
        this.reason = reason;
        this.timestamp = timestamp;
    }

    /**
     * create a new Report object
     * @param reporter the user who created the report
     * @param reason the reason of the report
     * @return the report
     */
    public static Report of(String reporter, String reason) {
        return new Report(UUID.randomUUID().toString(), ReportStatus.OPEN, reporter, null, reason, System.currentTimeMillis());
    }

    /**
     * get the report id
     * @return the report id
     */
    @NotNull
    public String getReportId() {
        return this.reportId;
    }

    /**
     * get the status of the report
     * @return the report status
     */
    @NotNull
    public ReportStatus getReportStatus() {
        return this.reportStatus;
    }

    /**
     * get the user who created the report
     * @return the report creator
     */
    @NotNull
    public String getReporter() {
        return this.reporter;
    }

    /**
     * get the assigned staff member
     * @return the assignee
     */
    @Nullable
    public String getAssignee() {
        return this.assignee;
    }

    /**
     * get the reason of the report
     * @return the reason
     */
    @NotNull
    public String getReason() {
        return this.reason;
    }

    /**
     * get the timestamp (time when the report was created)
     * @return the timestamp
     */
    public long getTimestamp() {
        return this.timestamp;
    }
}
