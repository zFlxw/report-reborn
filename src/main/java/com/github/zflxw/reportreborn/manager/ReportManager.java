package com.github.zflxw.reportreborn.manager;

import com.github.zflxw.reportreborn.ReportReborn;
import com.github.zflxw.reportreborn.data.Report;
import com.github.zflxw.reportreborn.data.ReportStatus;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class ReportManager {
    @NotNull
    private static final ConcurrentHashMap<String, Report> reports = new ConcurrentHashMap<>();

    /**
     * adds a report to cache and database
     * @param report the report to add
     */
    public static void addReport(@NotNull Report report) {
        PreparedStatement statement = ReportReborn.getInstance().getDatabase().statement(
            "INSERT INTO reports (REPORT_ID, STATUS, REPORTER, ASSIGNEE, TIMESTAMP, REASON) VALUES (?, ?, ?, ?, ?, ?)",
            report.getReportId(),
            report.getReportStatus().toString(),
            report.getReporter(),
            report.getAssignee(),
            report.getTimestamp(),
            report.getReason()
        );

        ReportReborn.getInstance().getDatabase().transaction(statement);
        reports.put(report.getReportId(), report);
    }

    /**
     * updated a report in cache and database
     * @param report the report to update
     */
    public static void updateReport(@NotNull Report report) {
        PreparedStatement statement = ReportReborn.getInstance().getDatabase().statement(
            "UPDATE reports SET STATUS = ?, REPORTER = ?, ASSIGNEE = ?, TIMESTAMP = ?, REASON = ? WHERE REPORT_ID = ?",
            report.getReportStatus().toString(),
            report.getReporter(),
            report.getAssignee(),
            report.getTimestamp(),
            report.getReason(),
            report.getReportId()
        );

        ReportReborn.getInstance().getDatabase().transaction(statement);
        reports.put(report.getReportId(), report);
    }

    /**
     * deletes a report with the given id
     * @param reportId the id of the report to remove
     */
    public static void deleteReport(String reportId) {
        reports.remove(reportId);
        PreparedStatement statement = ReportReborn.getInstance().getDatabase().statement(
            "DELETE FROM reports WHERE REPORT_ID = ?",
            reportId
        );

        ReportReborn.getInstance().getDatabase().transaction(statement);
    }

    /**
     * load all reports from the database and cache them local
     */
    public static void cacheAllReports() {
        try {
            long start = System.currentTimeMillis();
            int cacheLimit = ReportReborn.getInstance().getConfiguration().getInt("cache-limit");
            ResultSet resultSet = ReportReborn.getInstance().getDatabase()
                    .query("SELECT * FROM reports ORDER BY `TIMESTAMP` ASC " + ((cacheLimit != 0) ? "LIMIT " + cacheLimit : ""));
            ExecutorService executor = Executors.newFixedThreadPool(10);

            executor.execute(() -> {
                try {
                    while (resultSet.next()) {
                        try {
                            Report report = new Report(
                                    resultSet.getString("REPORT_ID"),
                                    ReportStatus.valueOf(resultSet.getString("STATUS")),
                                    resultSet.getString("REPORTER"),
                                    resultSet.getString("ASSIGNEE"),
                                    resultSet.getString("REASON"),
                                    resultSet.getLong("TIMESTAMP")
                            );
                            reports.put(resultSet.getString("REPORT_ID"), report);
                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }
                    }
                    resultSet.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            });

            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.SECONDS);

            ReportReborn.getInstance().log(Level.INFO, "Cached " + reports.size() + " reports in " + (System.currentTimeMillis() - start) + "ms");
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }
}
