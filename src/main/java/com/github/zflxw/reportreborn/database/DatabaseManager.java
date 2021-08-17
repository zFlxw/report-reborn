package com.github.zflxw.reportreborn.database;

import com.github.zflxw.reportreborn.ReportReborn;

public class DatabaseManager {
    public static void createTables() {
        ReportReborn.getInstance().getDatabase().transaction(
            ReportReborn.getInstance().getDatabase().statement(
                ReportReborn.getInstance().getFileUtils().readResource("sql/create_tables.sql")
            )
        );
    }
}
