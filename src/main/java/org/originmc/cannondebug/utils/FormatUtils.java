package org.originmc.cannondebug.utils;

import com.sk89q.worldedit.internal.expression.runtime.For;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.originmc.cannondebug.CannonDebugPlugin;

import java.util.List;

public final class FormatUtils {

    public static CannonDebugPlugin plugin = (CannonDebugPlugin) CannonDebugPlugin.getPlugin(CannonDebugPlugin.class);

    public static String format(String s){
        return ChatColor.translateAlternateColorCodes((char)'&', (String)s);
    }

    public static String prefix = FormatUtils.format(plugin.getConfig().getString("settings.prefix"));

    //Credits: SYS
    public static void sendMessage(CommandSender sender, List<String> messageList) {
        for (String message : messageList)
            if (!message.isEmpty())
                sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', message));
    }
    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', message));
    }
    //Credits: SYS
    public static List<String> replaceList(List<String> ls, String initial, String change){
        for (int i = 0; i < ls.size(); i++)
            ls.set(i, ls.get(i).replace(initial, change));
        return ls;
    }

}
