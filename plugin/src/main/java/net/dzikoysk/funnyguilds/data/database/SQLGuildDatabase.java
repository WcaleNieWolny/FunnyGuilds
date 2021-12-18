package net.dzikoysk.funnyguilds.data.database;

import net.dzikoysk.funnyguilds.data.database.element.SQLBasicUtils;
import net.dzikoysk.funnyguilds.data.database.element.SQLNamedStatement;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildDatabase;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.guild.RegionUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import net.dzikoysk.funnyguilds.user.UserUtils;
import panda.std.Option;
import panda.std.stream.PandaStream;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SQLGuildDatabase implements GuildDatabase {

    @Override
    public void saveGuild(Guild guild) {
        String members = ChatUtils.toString(UserUtils.getNames(guild.getMembers()), false);
        String deputies = ChatUtils.toString(UserUtils.getNames(guild.getDeputies()), false);
        String allies = ChatUtils.toString(GuildUtils.getNames(guild.getAllies()), false);
        String enemies = ChatUtils.toString(GuildUtils.getNames(guild.getEnemies()), false);
        SQLNamedStatement statement = SQLBasicUtils.getInsert(SQLDataModel.tabGuilds);

        statement.set("uuid", guild.getUUID().toString());
        statement.set("name", guild.getName());
        statement.set("tag", guild.getTag());
        statement.set("owner", guild.getOwner().getName());
        statement.set("home", LocationUtils.toString(guild.getHome()));
        statement.set("region", RegionUtils.toString(guild.getRegion()));
        statement.set("regions", "#abandoned");
        statement.set("members", members);
        statement.set("deputy", deputies);
        statement.set("allies", allies);
        statement.set("enemies", enemies);
        statement.set("points", guild.getRank().getAveragePoints());
        statement.set("lives", guild.getLives());
        statement.set("born", guild.getBorn());
        statement.set("validity", guild.getValidity());
        statement.set("attacked", guild.getProtection()); //TODO: [FG 5.0] attacked -> protection
        statement.set("ban", guild.getBan());
        statement.set("pvp", guild.getPvP());
        statement.set("info", "");

        statement.executeUpdate();
    }

    @Override
    public void deleteGuild(Guild guild) {
        SQLNamedStatement statement = SQLBasicUtils.getDelete(SQLDataModel.tabGuilds);

        statement.set("uuid", guild.getUUID().toString());
        statement.executeUpdate();
    }

    @Override
    public Set<Guild> getAllGuilds() {
        Set<Guild> guilds = new HashSet<>();

        SQLBasicUtils.getSelectAll(SQLDataModel.tabGuilds).executeQuery(resultAll -> {
            while (resultAll.next()) {
                Guild guild = DatabaseGuild.deserialize(resultAll);

                if (guild == null) {
                    continue;
                }

                guild.wasChanged();
                guilds.add(guild);
            }
        });

        SQLBasicUtils.getSelect(SQLDataModel.tabGuilds, "tag", "allies", "enemies").executeQuery(result -> {
            while (result.next()) {
                String tag = result.getString("tag");
                Option<Guild> guildOption = PandaStream.of(guilds)
                        .find(guild -> guild.getTag().equals(tag));

                if (guildOption.isEmpty()) {
                    continue;
                }

                Guild guild = guildOption.get();

                String alliesList = result.getString("allies");
                String enemiesList = result.getString("enemies");

                if (alliesList != null && !alliesList.isEmpty()) {
                    Set<Guild> allies = PandaStream.of(ChatUtils.fromString(alliesList))
                            .flatMap(name -> PandaStream.of(guilds).find(ally -> ally.getName().equals(name)))
                            .collect(Collectors.toSet());

                    guild.setAllies(allies);
                }

                if (enemiesList != null && !enemiesList.isEmpty()) {
                    Set<Guild> enemies = PandaStream.of(ChatUtils.fromString(enemiesList))
                            .flatMap(name -> PandaStream.of(guilds).find(enemy -> enemy.getName().equals(name)))
                            .collect(Collectors.toSet());

                    guild.setEnemies(enemies);
                }
            }
        });

        return guilds;
    }

}
