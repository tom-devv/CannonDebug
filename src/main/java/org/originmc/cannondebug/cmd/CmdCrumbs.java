package org.originmc.cannondebug.cmd;

import com.sk89q.worldedit.internal.expression.runtime.For;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.originmc.cannondebug.BlockSelection;
import org.originmc.cannondebug.CannonDebugPlugin;
import org.originmc.cannondebug.EntityTracker;
import org.originmc.cannondebug.utils.FormatUtils;
import org.originmc.cannondebug.utils.NumberUtils;

import java.util.ArrayList;
import java.util.List;

public class CmdCrumbs extends CommandExecutor {

    public CmdCrumbs(CannonDebugPlugin plugin, CommandSender sender, String[] args, String permission) {
        super(plugin, sender, args, permission);
    }

    @Override
    public boolean perform() {

        List<EntityTracker> entityTrackers = new ArrayList<>();


        String selectionID = args[1].equalsIgnoreCase("all") ? "all" : args[1];
        BlockSelection blockSelection = null;
        for (BlockSelection selection : user.getSelections()) {
            if(selectionID.equalsIgnoreCase("all")){
                entityTrackers.add(selection.getTracker());
                blockSelection = selection;
            } else {
                if(NumberUtils.parseInt(selectionID) == selection.getId()){
                    blockSelection = selection;
                    entityTrackers.add(selection.getTracker());
                }
            }

        }


        if(blockSelection == null) return false; //TODO add invalid BlockSelection message !

        EntityTracker entityTracker = blockSelection.getTracker();

        if(entityTracker == null){
            FormatUtils.sendMessage(sender, plugin.getConfig().getStringList("messages.error.no-data"));
            return false;
        }

        final int time;
        if(args.length > 2){
            time = Math.min(NumberUtils.parseInt(args[2]), plugin.getConfig().getInt("settings.crumbs.max-seconds"));
        } else {
            time = plugin.getConfig().getInt("settings.crumbs.default-seconds");
        }

        double interval = plugin.getConfig().getDouble("settings.crumbs.interval");

        // Second confirmation message
        List<String> showingMessage =  FormatUtils.replaceList(plugin.getConfig().getStringList("messages.crumbs.showing"), "%selection%", selectionID);
        FormatUtils.replaceList(showingMessage, "%seconds%", String.valueOf(time));
        FormatUtils.sendMessage(sender, showingMessage);



        for (EntityTracker tracker: entityTrackers) {
            List<Location> locationList = tracker.getLocationHistory();
            List<Location> boxLocations = new ArrayList<>();
            Location explodeLocation = null;
            //Location explodeLocation = locationList.size() == 81 ? locationList.get(80) : null;
            int data = tracker.getEntityType().getName().equals("PrimedTnt") ? 1: 2;
            if(data == 2){
                explodeLocation = locationList.get(locationList.size() -2);
                boxLocations.add(explodeLocation);
            }
            //TODO fix isDead() ?
            if(tracker.getEntity() == null && data == 2){
                explodeLocation = locationList.get(locationList.size()-1);
                boxLocations.add(explodeLocation);
            }
            if(locationList.size() == 81 && data == 1){
                boxLocations.add(locationList.get(80));
            }
            int r = data == 2 ? 1/255 : 1;
            int g = 1/255;
            int b = data == 2 ? 1 :1/255;
            Location finalExplodeLocation = explodeLocation;

            Player player = (Player) sender;
            new BukkitRunnable() {


                int t = 1;
                public void run(){
                    t++;
                    for (int i = 1; i < locationList.size(); i++) {
                        Location location = player.getLocation();

                        /*
                         * Calculate the differences along the y x z axis in that order
                         */

                        double yDiff = locationList.get(i).getY() - locationList.get(i-1).getY();
                        double xDiff = locationList.get(i).getX() - locationList.get(i-1).getX();
                        double zDiff = locationList.get(i).getZ() - locationList.get(i-1).getZ();


                        double y = locationList.get(i-1).getY()+0.5;
                        double x = locationList.get(i-1).getX();
                        double z = locationList.get(i-1).getZ();

                        double y1 = locationList.get(i).getY()+0.5;
                        double x1 = locationList.get(i).getX();

                        double px;
                        double py;
                        double pz;

                        double distance;



                        /*
                         * playEffect for Y values
                         */
                        for (double j = 0; j < yDiff; j += interval) {
                            px = x - location.getX();
                            py = y+j - location.getY();
                            pz = z - location.getZ();
                            distance = Math.sqrt((px * px) + (py * py) + (pz * pz));
                            if(distance < 50){
                                ((CraftPlayer)sender).getHandle().playerConnection.sendPacket(
                                        new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, ((float) x), (float) ((float) y+j), (float) ((float) z), r, g, b, (float) 1, 0)
                                );
                            }


                        }
                        /*
                         * playEffect for X values
                         */
                        for (double j = 0; j < xDiff ; j += interval) {
                            px = x+j - location.getX();
                            py = y1 - location.getY();
                            pz = z - location.getZ();
                            distance = Math.sqrt((px * px) + (py * py) + (pz * pz));
                            if(distance < 50){
                                ((CraftPlayer)sender).getHandle().playerConnection.sendPacket(
                                        new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, (float) ((float) x+j), (float) ((float) y1), (float) ((float) z), r, g, b, (float) 1, 0)
                                );
                            }

                        }

                        for (double l = 0; l > zDiff; l -= interval) {
                            px = x1 - location.getX();
                            py = y1 - location.getY();
                            pz = z+l -location.getZ();
                            distance = Math.sqrt((px * px) + (py * py) + (pz * pz));
                            if(distance < 50){
                                ((CraftPlayer)sender).getHandle().playerConnection.sendPacket(
                                        new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, ((float) x1), (float) ((float) y1), (float) ((float) z+l), r, g, b, (float) 1, 0)
                                );
                            }


                        }


                        /*
                         * Opposite directions
                         */

                        /*
                         * playEffect for Y values
                         */
                        for (double j = 0; j > yDiff; j -= interval) {

                            px = x - location.getX();
                            py = y+j - location.getY();
                            pz = z - location.getZ();
                            distance = Math.sqrt((px * px) + (py * py) + (pz * pz));
                            if(distance < 50) {
                                ((CraftPlayer)sender).getHandle().playerConnection.sendPacket(
                                        new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, ((float) x), (float) ((float) y+j), (float) ((float) z), r, g, b, (float) 1, 0)
                                );
                            }

                        }

                        /*
                         * playEffect for X values
                         */
                        for (double j = 0; j > xDiff ; j -= interval) {

                            px = x+j - location.getX();
                            py = y1 - location.getY();
                            pz = z - location.getZ();
                            distance = Math.sqrt((px * px) + (py * py) + (pz * pz));
                            if(distance < 50) {
                                ((CraftPlayer)sender).getHandle().playerConnection.sendPacket(
                                        new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, (float) ((float) x+j), (float) ((float) y1), (float) ((float) z), r, g, b, (float) 1, 0)
                                );
                            }

                        }

                        for (double l = 0; l < zDiff; l += interval) {

                            px = x1 - location.getX();
                            py = y1 - location.getY();
                            pz = z+l -location.getZ();
                            distance = Math.sqrt((px * px) + (py * py) + (pz * pz));
                            if(distance < 50) {
                                ((CraftPlayer)sender).getHandle().playerConnection.sendPacket(
                                        new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, ((float) x1), (float) ((float) y1), (float) ((float) z+l), r, g, b, (float) 1, 0)
                                );
                            }

                        }
                    }
                    if(t > time *2){
                        this.cancel();
                    }
                    /*
                     * Draw Boxes
                     */
                    for(Location finalExplodeLocation: boxLocations) {
                        if (finalExplodeLocation != null) {
                            double x = finalExplodeLocation.getX() + 0.5;
                            double y = finalExplodeLocation.getY();
                            double z = finalExplodeLocation.getZ() + 0.5;

                            double x1 = finalExplodeLocation.getX() - 0.5;
                            double z1 = finalExplodeLocation.getZ() - 0.5;

                            for (double i = 0; i < 1; i += interval) {

                                ((CraftPlayer) sender).getHandle().playerConnection.sendPacket(
                                        new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, (float) ((float) x - i), (float) y + 1, (float) z, r, g, b, (float) 1, 0)
                                );
                                ((CraftPlayer) sender).getHandle().playerConnection.sendPacket(
                                        new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, ((float) x), (float) y + 1, (float) ((float) z - i), r, g, b, (float) 1, 0)
                                );
                                ((CraftPlayer) sender).getHandle().playerConnection.sendPacket(
                                        new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, (float) ((float) x1 + i), (float) y + 1, (float) ((float) z1), r, g, b, (float) 1, 0)
                                );
                                ((CraftPlayer) sender).getHandle().playerConnection.sendPacket(
                                        new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, ((float) x1), (float) y + 1, (float) ((float) z1 + i), r, g, b, (float) 1, 0)
                                );

                                ((CraftPlayer) sender).getHandle().playerConnection.sendPacket(
                                        new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, (float) ((float) x - i), (float) y, (float) z, r, g, b, (float) 1, 0)
                                );
                                ((CraftPlayer) sender).getHandle().playerConnection.sendPacket(
                                        new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, ((float) x), (float) y, (float) ((float) z - i), r, g, b, (float) 1, 0)
                                );
                                ((CraftPlayer) sender).getHandle().playerConnection.sendPacket(
                                        new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, (float) ((float) x1 + i), (float) y, (float) ((float) z1), r, g, b, (float) 1, 0)
                                );
                                ((CraftPlayer) sender).getHandle().playerConnection.sendPacket(
                                        new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, ((float) x1), (float) y, (float) ((float) z1 + i), r, g, b, (float) 1, 0)
                                );

                                // Edges

                                ((CraftPlayer) sender).getHandle().playerConnection.sendPacket(
                                        new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, ((float) x), (float) ((float) y + i), (float) ((float) z), r, g, b, (float) 1, 0)
                                );
                                ((CraftPlayer) sender).getHandle().playerConnection.sendPacket(
                                        new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, ((float) x1), (float) ((float) y + i), (float) ((float) z), r, g, b, (float) 1, 0)
                                );
                                ((CraftPlayer) sender).getHandle().playerConnection.sendPacket(
                                        new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, ((float) x), (float) ((float) y + i), (float) ((float) z1), r, g, b, (float) 1, 0)
                                );
                                ((CraftPlayer) sender).getHandle().playerConnection.sendPacket(
                                        new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, ((float) x1), (float) ((float) y + i), (float) ((float) z1), r, g, b, (float) 1, 0)
                                );
                            }
                        }
                    }
                }
            }.runTaskTimer(plugin, 0, 10L);

        }





        return true;
    }

    public double calculateDistanceBetweenPoints(
            double x1,
            double y1,
            double z1,
            double x2,
            double y2,
            double z2
    ) {
        return Math.sqrt((y2 - y1) * (y2 - y1)  + (x2 - x1) * (x2 - x1) + (z2 - z1) * (z2 - z1));
    }

}
