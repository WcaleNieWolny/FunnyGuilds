package net.dzikoysk.funnyguilds.feature.ban;

import java.util.Date;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserBan;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class BanUtils {

    private BanUtils() {}

    public static void ban(Guild guild, long time, String reason) {
        guild.setBan(time + System.currentTimeMillis());

        for (User user : guild.getMembers()) {
            Player player = user.getPlayer();
            ban(user, time, reason);

            if (player != null && player.isOnline()) {
                player.kickPlayer(getBanMessage(user));
            }
        }
    }

    public static void ban(User user, long time, String reason) {
        time += System.currentTimeMillis();
        user.setBan(new UserBan(reason, time));
    }

    public static void unban(Guild guild) {
        for (User user : guild.getMembers()) {
            unban(user);
        }
    }

    public static void unban(User user) {
        user.setBan(null);
    }

    public static void checkIfBanShouldExpire(User user) {
        if (!user.isBanned()) {
            return;
        }

        if (System.currentTimeMillis() > user.getBan().getBanTime()) {
            user.setBan(null);
        }
    }

    public static String getBanMessage(User user) {
        String message = FunnyGuilds.getInstance().getMessageConfiguration().banMessage;

        if (!user.isBanned()) {
            return StringUtils.EMPTY;
        }

        UserBan userBan = user.getBan();
        message = StringUtils.replace(message, "{NEWLINE}", ChatColor.RESET + "\n");
        message = StringUtils.replace(message, "{DATE}", FunnyGuilds.getInstance().getMessageConfiguration().dateFormat.format(new Date(userBan.getBanTime())));
        message = StringUtils.replace(message, "{REASON}", userBan.getReason());
        message = StringUtils.replace(message, "{PLAYER}", user.getName());

        return ChatUtils.colored(message);
    }

}
