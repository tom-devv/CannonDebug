/*
 * This file is part of CannonProfiler, licensed under the MIT License (MIT).
 *
 * Copyright (c) Origin <http://www.originmc.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.originmc.cannondebug.cmd;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.originmc.cannondebug.BlockSelection;
import org.originmc.cannondebug.CannonDebugPlugin;
import org.originmc.cannondebug.utils.FormatUtils;

import java.util.Iterator;
import java.util.List;

public final class CmdClear extends CommandExecutor {

    public CmdClear(CannonDebugPlugin plugin, CommandSender sender, String[] args, String permission) {
        super(plugin, sender, args, permission);
    }

    @Override
    public boolean perform() {
        // Do nothing if not enough arguments.
        if (args.length == 1) return false;

        // Check if user wishes to delete history.
        if (args[1].toLowerCase().startsWith("h")) {
            // Delete users history.
            for (BlockSelection selection : user.getSelections()) {
                selection.setTracker(null);
            }

            // Send confirmation message.
            FormatUtils.sendMessage(sender, plugin.getConfig().getStringList("messages.clear.history"));
            return true;
        }

        // Check if user wishes to delete selections.
        if (args[1].toLowerCase().startsWith("s")) {
            // Update the users preview if they have preview mode toggled.
            if (user.isPreviewing()) {
                for (BlockSelection selection : user.getSelections()) {
                    Block block = selection.getLocation().getBlock();
                    ((Player) sender).sendBlockChange(block.getLocation(), block.getType(), block.getData());
                }
            }

            // Delete users selections.
            for (BlockSelection blockSelection : user.getSelections()) {
                for (Iterator<List<BlockSelection>> iterator = plugin.getSelections().values().iterator(); iterator.hasNext(); ) {
                    List<BlockSelection> list = iterator.next();
                    list.remove(blockSelection);

                    // If it was the last element remove it
                    if (list.size() == 0) {
                        iterator.remove();
                    }
                }
            }
            user.getSelections().clear();

            // Send confirmation message.
            FormatUtils.sendMessage(sender, plugin.getConfig().getStringList("messages.clear.selections"));
            return true;
        }
        return false;
    }

}
