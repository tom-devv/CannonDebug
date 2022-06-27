package org.originmc.cannondebug.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.material.Dispenser;
import org.bukkit.util.Vector;
import org.originmc.cannondebug.BlockSelection;
import org.originmc.cannondebug.CannonDebugPlugin;
import org.originmc.cannondebug.EntityTracker;
import org.originmc.cannondebug.User;
import org.originmc.cannondebug.utils.Configuration;

import java.util.List;

import static org.originmc.cannondebug.utils.MaterialUtils.isDispenser;
import static org.originmc.cannondebug.utils.MaterialUtils.isExplosives;
import static org.originmc.cannondebug.utils.MaterialUtils.isStacker;

public class WorldListener implements Listener {

    private final CannonDebugPlugin plugin;

    public WorldListener(CannonDebugPlugin plugin) {
        this.plugin = plugin;
    }

    public void tick() {
        if (!plugin.getConfiguration().alternativeTracking) {
            return;
        }

        // Track recently spawned
        for (World world : Bukkit.getWorlds()) {
            List<Entity> entities = world.getEntities();

            for (int i = 0, entitiesSize = entities.size(); i < entitiesSize; i++) {
                Entity entity = entities.get(i);

                if (entity.getTicksLived() != 1) {
                    continue;
                }

                track(entity);
            }
        }
    }

    private void track(Entity entity) {
        Location sourceLocation;
        if (entity instanceof TNTPrimed) {
            sourceLocation = ((TNTPrimed) entity).getSourceLoc();
        } else if (entity instanceof FallingBlock) {
            sourceLocation = ((FallingBlock) entity).getSourceLoc();
        } else {
            return;
        }

        if (sourceLocation.getX() % 1 != 0 || sourceLocation.getZ() % 1 != 0) {
            sourceLocation = sourceLocation.clone();
            sourceLocation.subtract(0.5, 0.0, 0.5);
        }

        if (plugin.getSelections().containsKey(sourceLocation)) {
            List<BlockSelection> blockSelections = plugin.getSelections().get(sourceLocation);

            EntityTracker tracker = null;
            for (BlockSelection selection : blockSelections) {
                // Build a new tracker due to it being used.
                if (tracker == null) {
                    tracker = new EntityTracker(entity.getType(), plugin.getCurrentTick());
                    tracker.setEntity(entity);
                    plugin.getActiveTrackers().add(tracker);
                }

                // Update order
                selection.setOrder(selection.getUser().getAndIncOrder());

                // Add entity tracker to user selection
                selection.setTracker(tracker);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void startProfiling(BlockDispenseEvent event) {
        if (plugin.getConfiguration().alternativeTracking) {
            return;
        }
        // Do nothing if block is not a dispenser.
        Block block = event.getBlock();
        if (!isDispenser(block.getType())) return;

        // Do nothing if not shot TNT.
        if (!isExplosives(event.getItem().getType())) return;

        // Loop through each user profile.
        BlockSelection selection;
        EntityTracker tracker = null;
        for (User user : plugin.getUsers().values()) {
            // Do nothing if user is not attempting to profile current block.
            selection = user.getSelection(block.getLocation());
            if (selection == null) {
                continue;
            }

            // Build a new tracker due to it being used.
            if (tracker == null) {
                // Cancel the event.
                event.setCancelled(true);

                // Shoot a new falling block with the exact same properties as current.
                BlockFace face = ((Dispenser) block.getState().getData()).getFacing();
                Location location = block.getLocation().clone();
                location.add(face.getModX() + 0.5, face.getModY(), face.getModZ() + 0.5);
                TNTPrimed tnt = block.getWorld().spawn(location, TNTPrimed.class);
                tracker = new EntityTracker(tnt.getType(), plugin.getCurrentTick());
                tracker.setEntity(tnt);
                plugin.getActiveTrackers().add(tracker);
            }

            // Update order
            selection.setOrder(user.getAndIncOrder());

            // Add block tracker to user.
            selection.setTracker(tracker);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void startProfiling(EntityChangeBlockEvent event) {
        if (plugin.getConfiguration().alternativeTracking) {
            return;
        }
        // Do nothing if the material is not used for stacking in cannons.
        Block block = event.getBlock();
        if (!isStacker(block.getType())) return;

        // Do nothing if block is not turning into a falling block.
        if (!(event.getEntity() instanceof FallingBlock)) return;

        // Loop through each user profile.
        BlockSelection selection;
        EntityTracker tracker = null;
        for (User user : plugin.getUsers().values()) {
            // Do nothing if user is not attempting to profile current block.
            selection = user.getSelection(block.getLocation());
            if (selection == null) {
                continue;
            }

            // Build a new tracker due to it being used.
            if (tracker == null) {
                tracker = new EntityTracker(event.getEntityType(), plugin.getCurrentTick());
                tracker.setEntity(event.getEntity());
                plugin.getActiveTrackers().add(tracker);
            }

            // Update order
            selection.setOrder(user.getAndIncOrder());

            // Add block tracker to user.
            selection.setTracker(tracker);
        }
    }

}
