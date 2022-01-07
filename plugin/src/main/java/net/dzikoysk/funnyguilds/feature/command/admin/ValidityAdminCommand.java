package net.dzikoysk.funnyguilds.feature.command.admin;

import java.util.Date;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildExtendValidityEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class ValidityAdminCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.validity.name}",
            permission = "funnyguilds.admin",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 1, messages.generalNoTagGiven);
        when(args.length < 2, messages.adminNoValidityTimeGiven);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        when(guild.isBanned(), messages.adminGuildBanned);

        long time = TimeUtils.parseTime(args[1]);
        if (time < 1) {
            sender.sendMessage(messages.adminTimeError);
            return;
        }

        User admin = AdminUtils.getAdminUser(sender);
        if (!SimpleEventHandler.handle(new GuildExtendValidityEvent(AdminUtils.getCause(admin), admin, guild, time))) {
            return;
        }

        long validity = guild.getValidity();
        if (validity == 0) {
            validity = System.currentTimeMillis();
        }

        validity += time;
        guild.setValidity(validity);

        String date = messages.dateFormat.format(new Date(validity));
        sender.sendMessage(messages.adminNewValidity.replace("{GUILD}", guild.getName()).replace("{VALIDITY}", date));
    }

}
