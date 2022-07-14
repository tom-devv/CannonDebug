package org.originmc.cannondebug.cmd;

import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.originmc.cannondebug.BlockSelection;
import org.originmc.cannondebug.CannonDebugPlugin;
import org.originmc.cannondebug.EntityTracker;
import org.originmc.cannondebug.FancyPager;
import org.originmc.cannondebug.utils.EnumUtils;
import org.originmc.cannondebug.utils.NumberUtils;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.*;

public class CmdCompare extends CommandExecutor{

    public CmdCompare(CannonDebugPlugin plugin, CommandSender sender, String[] args, String permission) {
        super(plugin, sender, args, permission);
    }
    @Override
    public boolean perform() {

        if(args.length < 4) return false;

        BlockSelection block = null;

        BlockSelection blockAlt = null;

        for (BlockSelection selection : user.getSelections()) {
            if(selection.getId() == (NumberUtils.parseInt(args[1]))){
                block = selection;
            }
            if(selection.getId() == (NumberUtils.parseInt(args[2]))) {
                blockAlt = selection;
            }

        }
        if(block == null || blockAlt == null) return false;
        int tick = NumberUtils.parseInt(args[3]);

        List<BlockSelection> selections = new ArrayList<>();
        selections.add(block);
        selections.add(blockAlt);


        List<FancyMessage> lines = new ArrayList<>();


        EntityTracker trackers = block.getTracker();
        EntityTracker trackerAlt = blockAlt.getTracker();


        int difference = (int) trackers.getSpawnTick() - (int) trackerAlt.getSpawnTick();

        long serverTick =  trackers.getSpawnTick();
//        System.out.println(tick);
//        System.out.println(tick + difference);





        for (BlockSelection selection: selections) {
            int i = tick;
            EntityTracker tracker = selection.getTracker();
            String entityType = EnumUtils.getFriendlyName(tracker.getEntityType()).equals("Primed Tnt") ? "TNT" : "SAND";


            System.out.println(difference);

//            if(selections.get(1).getId() == selection.getId()){
//                i = tick + difference;
//            }
            Location location = tracker.getLocationHistory().get(i);
            Vector velocity = tracker.getVelocityHistory().get(i);

            FancyMessage message = new FancyMessage("ID: ")
                    .color(WHITE)
                    .then(selection.getId() + " ")
                    .color(WHITE)
                    .formattedTooltip(

                            new FancyMessage("Spawned tick: ")
                                    .color(YELLOW)
                                    .then("" + tracker.getSpawnTick())
                                    .color(AQUA),


                            new FancyMessage("Death tick: ")
                                    .color(YELLOW)
                                    .then((tracker.getDeathTick() == -1 ? "Still alive" : "" + tracker.getDeathTick()))
                                    .color(RED),

                            new FancyMessage("Server tick: ")
                                    .color(YELLOW)
                                    .then("" + serverTick)
                                    .color(GREEN)
                            )

                    .then(entityType)
                    .color(YELLOW)
                    .then(" ⎜ ")
                    .color(DARK_GRAY)

                    .then("Location + Velocity")
                    .color(GRAY)
                    .formattedTooltip(
                            new FancyMessage("Click here to teleport to this location").color(DARK_AQUA).style(BOLD),
                            new FancyMessage("LOCATION").color(YELLOW).style(BOLD),
                            new FancyMessage("X: ").color(WHITE).then("" + location.getX()).color(RED),
                            new FancyMessage("Y: ").color(WHITE).then("" + location.getY()).color(RED),
                            new FancyMessage("Z: ").color(WHITE).then("" + location.getZ()).color(RED),
                            new FancyMessage(""),
                            new FancyMessage("VELOCITY").color(YELLOW).style(BOLD),
                            new FancyMessage("X: ").color(WHITE).then("" + velocity.getX()).color(RED),
                            new FancyMessage("Y: ").color(WHITE).then("" + velocity.getY()).color(RED),
                            new FancyMessage("Z: ").color(WHITE).then("" + velocity.getZ()).color(RED)
                    )
                    .command("/c tp " + location.getX() + " " + location.getY() + " " + location.getZ())

                    .then(" ⎜ [")
                    .color(DARK_GRAY)

                    .then("P")
                    .formattedTooltip(
                            new FancyMessage("Clicking this will allow you to copy the location."),
                            new FancyMessage("It will suggest a command from there you can copy it with ")
                                    .color(WHITE)
                                    .then("CTRL + C")
                                    .color(LIGHT_PURPLE)
                    )
                    .color(AQUA)
                    .suggest(location.getX() + " " + location.getY() + " " + location.getZ())



                    .then("M")
                    .formattedTooltip(
                            new FancyMessage("Clicking this will allow you to copy the velocity."),
                            new FancyMessage("It will suggest a command from there you can copy it with ")
                                    .color(WHITE)
                                    .then("CTRL + C")
                                    .color(AQUA)
                    )
                    .color(LIGHT_PURPLE)
                    .suggest(velocity.getX() + " " + velocity.getY() + " " + velocity.getZ());



            if (NumberUtils.isInsideCube(location.getX()) && NumberUtils.isInsideCube(location.getZ()) ||
                    Math.abs(velocity.getX()) != 0.0 && NumberUtils.isInsideCube(location.getX()) ||
                    Math.abs(velocity.getZ()) != 0.0 && NumberUtils.isInsideCube(location.getZ())) {
                message.then("X")
                        .color(GREEN)
                        .formattedTooltip(new FancyMessage("This location is within a block on the x or z axis"));
            }
            if (NumberUtils.isInsideCube(location.getY() + (double) 0.49F)) {
                message.then("Y")
                        .color(RED)
                        .formattedTooltip(new FancyMessage("This location is within a block on y axis"));
            }

            if (Math.sqrt(velocity.getX() * velocity.getX() + velocity.getY() * velocity.getY() + velocity.getZ() * velocity.getZ()) >= 8.0D) {
                message.then("M")
                        .color(YELLOW)
                        .formattedTooltip(new FancyMessage("This entity is moving fast [>= 8.0, cannot swing]"));
            }
            if (velocity.getY() == -0.0) {
                message.then("O")
                        .color(BLUE)
                        .formattedTooltip(new FancyMessage("This entity is onGround"));
            }

            message.then("]")
                    .color(DARK_GRAY)

                    .then(" ⎜ [").color(DARK_GRAY)
                    .then("D")
                    .color(BLUE)
                    .formattedTooltip(
                            new FancyMessage("This displacement of this entity on each of its axes").color(AQUA),
                            new FancyMessage("X: ").color(WHITE).then("" + (location.getX() - tracker.getLocationHistory().get(0).getX())).color(RED),
                            new FancyMessage("Y: ").color(WHITE).then("" + ((location.getY() - tracker.getLocationHistory().get(0).getY()))).color(RED),
                            new FancyMessage("Z: ").color(WHITE).then("" + (location.getZ() - tracker.getLocationHistory().get(0).getZ())).color(RED)
                    )
            .then("]")
            .color(DARK_GRAY)
            .then(" ⎜ [").color(DARK_GRAY)
            .then("C")
            .color(RED)
            .formattedTooltip(
                    new FancyMessage("Click this to activate crumbs for 15 seconds").color(WHITE)
            )
            .command("/c crumbs " + selection.getId() + " 15")
            .then("]").color(DARK_GRAY);
            lines.add(message);

        }
        // Send user the pager messages.
        FancyPager pager = new FancyPager("Comparing Ticks: "  + selections.get(0).getId() + " ⎜ " + selections.get(1).getId(), lines.toArray(new FancyMessage[lines.size()]));
        send(pager, 0);
        return true;



    }
}
