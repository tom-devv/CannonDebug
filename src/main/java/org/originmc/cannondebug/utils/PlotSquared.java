package org.originmc.cannondebug.utils;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlotSquared {

    private static PlotAPI plotAPI = null;

    static {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("PlotSquared");

        if (plugin != null && plugin.isEnabled()) {
            plotAPI = new PlotAPI();
        }
    }

    public static boolean isEnabled() {
        return plotAPI != null;
    }

    public static boolean isPlotTrusted(Player player, Location location) {
        Plot plot = plotAPI.getPlot(location);

        if (plot == null) {
            // If this isn't a plot world just return true
            return !plotAPI.isPlotWorld(location.getWorld());
        }

        return player != null && (plot.getOwners().contains(player.getUniqueId()) || plot.getTrusted().contains(player.getUniqueId()) || player.isOp() || player.hasPermission("cannondebug.*"));
    }

}
