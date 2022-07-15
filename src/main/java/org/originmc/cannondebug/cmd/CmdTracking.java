package org.originmc.cannondebug.cmd;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.originmc.cannondebug.BlockSelection;
import org.originmc.cannondebug.CannonDebugPlugin;
import org.originmc.cannondebug.EntityTracker;
import org.originmc.cannondebug.utils.FormatUtils;
import org.originmc.cannondebug.utils.NumberUtils;
import org.originmc.cannondebug.utils.PlotSquared;

public class CmdTracking extends CommandExecutor {

    public CmdTracking(CannonDebugPlugin plugin, CommandSender sender, String[] args, String permission) {
        super(plugin, sender, args, permission);
    }

    @Override
    public boolean perform() {
        // In case a user gives everyone cannondebug.*
        if (!user.getBase().isOp()) {
            return false;
        }

        boolean tracking = !plugin.getConfiguration().alternativeTracking;
        plugin.getConfiguration().setBoolean("settings.alternative-tracking.enabled", tracking);
        plugin.getConfiguration().alternativeTracking = tracking;

        if (tracking) {
            FormatUtils.sendMessage(sender, plugin.getConfig().getStringList("messages.alternative-tracking.enabled"));
        } else {
            FormatUtils.sendMessage(sender, plugin.getConfig().getStringList("messages.alternative-tracking.disabled"));
        }

        return true;
    }


}
