package org.originmc.cannondebug.cmd;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.originmc.cannondebug.BlockSelection;
import org.originmc.cannondebug.CannonDebugPlugin;
import org.originmc.cannondebug.EntityTracker;
import org.originmc.cannondebug.utils.FormatUtils;
import org.originmc.cannondebug.utils.NumberUtils;

import java.util.List;

public class CmdExposure extends CommandExecutor {

    public CmdExposure(CannonDebugPlugin plugin, CommandSender sender, String[] args, String permission) {
        super(plugin, sender, args, permission);
    }

    @Override
    public boolean perform() {

        if(args.length < 5){
            FormatUtils.sendMessage(sender, plugin.getConfig().getStringList("messages.exposure.arguments"));
            return false;
        }

        int powerID = NumberUtils.parseInt(args[1]);
        int powerTick = NumberUtils.parseInt(args[2]);
        int projectileID = NumberUtils.parseInt(args[3]);
        int projectileTick = NumberUtils.parseInt(args[4]);

        BlockSelection power = null;
        BlockSelection projectile = null;

        for (BlockSelection selection : user.getSelections()) {

            if(powerID == selection.getId()){
                power = selection;
            }
            if(projectileID == selection.getId()){
                projectile = selection;
            }
        }

        if(power == null || projectile == null) return false;

        Player player = (Player) sender;
        EntityTracker powerTracker = power.getTracker();
        EntityTracker projectileTracker = projectile.getTracker();

        List<Location> powerLocations = powerTracker.getLocationHistory();
        List<Location> projectileLocations = projectileTracker.getLocationHistory();

        double powerX = powerLocations.get(powerTick).getX();
        double powerY = powerLocations.get(powerTick).getY();
        double powerZ = powerLocations.get(powerTick).getZ();

        double projectileX = projectileLocations.get(projectileTick).getX();
        double projectileY = projectileLocations.get(projectileTick).getY();
        double projectileZ = projectileLocations.get(projectileTick).getZ();


        TextComponent exposure = new TextComponent(plugin.getConfig().getString("messages.exposure.copy-message"));
        exposure.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"X: " + String.valueOf(powerX - projectileX) + " Y: " + String.valueOf(powerY - projectileY) + " Z:" + String.valueOf(powerZ - projectileZ)));

        List<String> values =  FormatUtils.replaceList(plugin.getConfig().getStringList("messages.exposure.values"), "%x%", String.valueOf(powerX - projectileX));
        FormatUtils.replaceList(values, "%y%", String.valueOf(powerY - projectileY));
        FormatUtils.replaceList(values, "%z%", String.valueOf(powerZ - projectileZ));

        FormatUtils.sendMessage(sender, values);
        player.spigot().sendMessage((BaseComponent) exposure);


        return true;
    }
}
