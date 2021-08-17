package com.github.zflxw.reportreborn.data;

import com.github.zflxw.reportreborn.ReportReborn;

import java.util.List;

public class ReportReason {
    private final List<String> reportReasons;

    /**
     * load all reasons from config into cache
     */
    public ReportReason() {
        this.reportReasons = (List<String>) ReportReborn.getInstance().getConfiguration().getList("reasons");
    }

    /**
     * add a reason to cache and config
     * @param reason the reason
     */
    public void addReason(String reason) {
        this.reportReasons.add(reason);
        ReportReborn.getInstance().getConfiguration().getYamlConfiguration().set("reasons", reportReasons);
    }

    /**
     * check if the cache contains a specific reason
     * @param reason the reason to search for
     * @return true, if the reason is in cache. False otherwise
     */
    public boolean contains(String reason) {
        return this.reportReasons.contains(reason);
    }

    /**
     * get all cached reasons
     * @return the cached reasons
     */
    public List<String> getReasons() {
        return this.reportReasons;
    }
}
