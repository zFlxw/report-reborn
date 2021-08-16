package com.github.zflxw.reportreborn.listener;

import com.github.zflxw.reportreborn.ReportReborn;
import com.github.zflxw.reportreborn.utils.listener.LoadListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@LoadListener
public class PlayerConnectionListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        ReportReborn.getInstance().getPermissionManager().loadPermissions(event.getPlayer());
    }
}
