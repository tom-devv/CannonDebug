package org.originmc.cannondebug.cmd;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.originmc.cannondebug.BlockSelection;
import org.originmc.cannondebug.CannonDebugPlugin;
import org.originmc.cannondebug.EntityTracker;
import org.originmc.cannondebug.utils.NumberUtils;
import org.originmc.cannondebug.utils.PlotSquared;

import java.util.Arrays;
import java.util.List;

public class CmdCrumbs extends CommandExecutor {

    public CmdCrumbs(CannonDebugPlugin plugin, CommandSender sender, String[] args, String permission) {
        super(plugin, sender, args, permission);
    }

    @Override
    public boolean perform() {

        int selectionID = NumberUtils.parseInt(args[1]);
        BlockSelection blockSelection = null;
        for (BlockSelection selection : user.getSelections()) {
            if(selection.getId() == selectionID){
                blockSelection = selection;
            }
        }

        if(blockSelection == null) return false; //TODO add invalid BlockSelection message !

        World world = blockSelection.getLocation().getWorld();

        EntityTracker entityTracker = blockSelection.getTracker();

        if(entityTracker == null){
            sender.sendMessage(ChatColor.RED + "That ID has not dispensed any TNT");
            return false;
        }

        List<Location> locationList = entityTracker.getLocationHistory();

        final int time;
        if(args.length > 2){
            time = Math.min(NumberUtils.parseInt(args[2]), 15);
        } else {
            time = 5;
        }

        new BukkitRunnable() {
            int t = 1;
            public void run(){
                t++;
                for (int i = 1; i < locationList.size(); i++) {

                    /*
                     * Calculate the differences along the y x z axis in that order
                     */

                    double yDiff = locationList.get(i).getY() - locationList.get(i-1).getY();
                    double xDiff = locationList.get(i).getX() - locationList.get(i-1).getX();
                    double zDiff = locationList.get(i).getZ() - locationList.get(i-1).getZ();


                    double y = locationList.get(i-1).getY();
                    double x = locationList.get(i-1).getX();
                    double z = locationList.get(i-1).getZ();

                    double y1 = locationList.get(i).getY();
                    double x1 = locationList.get(i).getX();


                    /*
                     * playEffect for Y values
                     */
                    //System.out.println(yDiff);
                    for (int j = 0; j < yDiff; j++) {
                        world.spigot().playEffect(new Location(world, x,y+j,z), Effect.COLOURED_DUST, 0, 1,0,0,0,0, 30 ,30);
                    }
                    /*
                     * playEffect for X values
                     */
                    for (int j = 0; j < xDiff ; j++) {
                        world.spigot().playEffect(new Location(world, x+j,y1,z), Effect.COLOURED_DUST, 0, 1,0,0,0,0, 30 ,30);
                    }


                    for (int j = 0; j < xDiff ; j++) {
                        world.spigot().playEffect(new Location(world, x+j,y1,z), Effect.COLOURED_DUST, 0, 1,0,0,0,0, 30 ,30);
                    }

                    for (int l = 0; l > zDiff; l--) {
                        world.spigot().playEffect(new Location(world, x1,y1,z+l), Effect.COLOURED_DUST, 0, 1,0,0,0,0, 30 ,30);
                    }


                    /*
                     * Opposite directions
                     */

                    /*
                     * playEffect for Y values
                     */
                    for (int j = 0; j > yDiff; j--) {
                        world.spigot().playEffect(new Location(world, x,y+j,z), Effect.COLOURED_DUST, 0, 1,0,0,0,0, 30 ,30);
                    }

                    /*
                     * playEffect for X values
                     */
                    for (int j = 0; j > xDiff ; j--) {
                        world.spigot().playEffect(new Location(world, x+j,y1,z), Effect.COLOURED_DUST, 0, 1,0,0,0,0, 30 ,30);
                    }

                    for (int l = 0; l < zDiff; l++) {
                        world.spigot().playEffect(new Location(world, x1,y1,z+l), Effect.COLOURED_DUST, 0, 1,0,0,0,0, 30 ,30);
                    }




                }
                if(t > time){
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20L);






        return true;
    }

}
