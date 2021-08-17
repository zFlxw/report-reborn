package com.github.zflxw.reportreborn.data;

import com.github.zflxw.reportreborn.ReportReborn;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportPlayer {
    @NotNull
    private final Player player;

    private int totalReports;
    private int openReports;
    private int acceptedReports;
    private int deniedReports;

    private ReportPlayer(@NotNull Player player, int totalReports, int openReports, int acceptedReports, int deniedReports) {
        this.player = player;

        this.totalReports = totalReports;
        this.openReports = openReports;
        this.acceptedReports = acceptedReports;
        this.deniedReports = deniedReports;
    }

    /**
     * instantiate a new object of a player. If there are any entries in the database yet, they will be loaded into the object
     * @param player the player to load
     * @return the ReportPlayer object for the given player
     */
    public static ReportPlayer of(Player player) {
        try {
            ResultSet resultSet = ReportReborn.getInstance().getDatabase().query("SELECT * FROM reports WHERE UUID = ?", player.getUniqueId().toString());
            int openCount = 0, acceptedCount = 0, deniedCount = 0;

            while (resultSet.next()) {
                switch (ReportStatus.valueOf(resultSet.getString("STATUS"))) {
                    case OPEN, REVIEWING -> openCount++;
                    case ACCEPTED -> acceptedCount++;
                    case DENIED -> deniedCount++;
                }
            }

            resultSet.close();
            return new ReportPlayer(
                player,
                openCount + acceptedCount + deniedCount,
                openCount,
                acceptedCount,
                deniedCount
            );
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    /**
     * get the player of the object
     * @return the player
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * get the total reports of the player
     * @return the total reports
     */
    public int getTotalReports() {
        return totalReports;
    }

    /**
     * set the total reports of the player
     * @param totalReports the total reports
     */
    public void setTotalReports(int totalReports) {
        this.totalReports = totalReports;
    }

    /**
     * get the open reports of the player
     * @return the open reports
     */
    public int getOpenReports() {
        return openReports;
    }

    /**
     * set the open reports of the player
     * @param openReports the open reports
     */
    public void setOpenReports(int openReports) {
        this.openReports = openReports;
    }

    /**
     * get the accepted reports of the player
     * @return the accepted reports
     */
    public int getAcceptedReports() {
        return acceptedReports;
    }

    /**
     * set the accepted reports of the player
     * @param acceptedReports the accepted reports
     */
    public void setAcceptedReports(int acceptedReports) {
        this.acceptedReports = acceptedReports;
    }

    /**
     * get the denied reports of the player
     * @return the denied reports
     */
    public int getDeniedReports() {
        return deniedReports;
    }

    /**
     * set the denied reports of the player
     * @param deniedReports the denied reports
     */
    public void setDeniedReports(int deniedReports) {
        this.deniedReports = deniedReports;
    }

}
