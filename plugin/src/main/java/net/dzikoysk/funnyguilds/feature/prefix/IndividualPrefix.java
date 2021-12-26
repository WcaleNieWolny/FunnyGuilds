package net.dzikoysk.funnyguilds.feature.prefix;

import java.util.Set;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.placeholders.Placeholders;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import panda.utilities.text.Formatter;

public class IndividualPrefix {

    private final User user;
    private final FunnyGuilds plugin;

    public IndividualPrefix(User user) {
        this.user = user;
        this.plugin = FunnyGuilds.getInstance();
    }

    protected void addPlayer(String player) {
        plugin.getUserManager().findByName(player)
                .filter(User::hasGuild)
                .peek(byName -> {
                    Scoreboard scoreboard = getScoreboard();
                    Team team = scoreboard.getEntryTeam(player);

                    if (team != null) {
                        team.removeEntry(player);
                    }

                    team = scoreboard.getTeam(byName.getGuild().getTag());
                    if (team == null) {
                        addGuild(byName.getGuild());
                        team = scoreboard.getTeam(byName.getGuild().getTag());
                    }

                    if (team == null) {
                        FunnyGuilds.getPluginLogger().debug("We're trying to add Prefix for player, but guild team is null");
                        return;
                    }

                    if (this.user.hasGuild()) {
                        if (this.user.equals(byName) || this.user.getGuild().getMembers().contains(byName)) {
                            team.setPrefix(preparePrefix(plugin.getPluginConfiguration().prefixOur, byName.getGuild()));
                        }
                    }

                    team.addEntry(player);
                });
    }

    public void addGuild(Guild to) {
        if (to == null) {
            return;
        }

        Scoreboard scoreboard = getScoreboard();
        Guild guild = user.getGuild();

        if (guild != null) {
            if (guild.equals(to)) {
                initialize();
                return;
            }

            Team team = scoreboard.getTeam(to.getTag());

            if (team == null) {
                team = scoreboard.registerNewTeam(to.getTag());
            }

            for (User u : to.getMembers()) {
                if (!team.hasEntry(u.getName())) {
                    team.addEntry(u.getName());
                }
            }

            String prefix = plugin.getPluginConfiguration().prefixOther;

            if (guild.getAllies().contains(to)) {
                prefix = plugin.getPluginConfiguration().prefixAllies;
            }

            if (guild.getEnemies().contains(to) || to.getEnemies().contains(guild)) {
                prefix = plugin.getPluginConfiguration().prefixEnemies;
            }

            team.setPrefix(preparePrefix(prefix, to));
        }
        else {
            Team team = scoreboard.getTeam(to.getTag());

            if (team == null) {
                team = scoreboard.registerNewTeam(to.getTag());
            }

            for (User u : to.getMembers()) {
                if (!team.hasEntry(u.getName())) {
                    team.addEntry(u.getName());
                }
            }

            team.setPrefix(preparePrefix(plugin.getPluginConfiguration().prefixOther, to));
        }
    }

    protected void removePlayer(String playerName) {
        Team team = getScoreboard().getEntryTeam(playerName);
        if (team != null) {
            team.removeEntry(playerName);
        }

        plugin.getUserManager().findByName(playerName)
                .peek(this::registerSoloTeam);
    }

    protected void removeGuild(Guild guild) {
        if (guild == null) {
            return;
        }

        String tag = guild.getTag();

        if (tag.isEmpty()) {
            throw new IllegalStateException("Guild tag can't be empty!");
        }

        this.user.getCache().getScoreboard()
                .map(scoreboard -> scoreboard.getTeam(tag))
                .peek(Team::unregister);

        for (User member : guild.getMembers()) {
            registerSoloTeam(member);
        }
    }

    public void initialize() {
        Set<Guild> guilds = plugin.getGuildManager().getGuilds();
        Scoreboard scoreboard = getScoreboard();
        Guild guild = user.getGuild();

        if (guild != null) {
            guilds.remove(guild);

            PluginConfiguration config = plugin.getPluginConfiguration();
            String our = config.prefixOur;
            String ally = config.prefixAllies;
            String enemy = config.prefixEnemies;
            String other = config.prefixOther;
            Team team = scoreboard.getTeam(guild.getTag());

            if (team == null) {
                team = scoreboard.registerNewTeam(guild.getTag());
            }

            for (User member : guild.getMembers()) {
                if (!team.hasEntry(member.getName())) {
                    team.addEntry(member.getName());
                }
            }

            team.setPrefix(preparePrefix(our, guild));

            for (Guild one : guilds) {
                if (one == null || one.getTag() == null) {
                    continue;
                }

                team = scoreboard.getTeam(one.getTag());

                if (team == null) {
                    team = scoreboard.registerNewTeam(one.getTag());
                }

                for (User member : one.getMembers()) {
                    if (!team.hasEntry(member.getName())) {
                        team.addEntry(member.getName());
                    }
                }

                if (guild.getAllies().contains(one)) {
                    team.setPrefix(preparePrefix(ally, one));
                }
                else if (guild.getEnemies().contains(one) || one.getEnemies().contains(guild)) {
                    team.setPrefix(preparePrefix(enemy, one));
                }
                else {
                    team.setPrefix(preparePrefix(other, one));
                }
            }
        }
        else {
            String other = plugin.getPluginConfiguration().prefixOther;
            registerSoloTeam(this.user);

            for (Guild one : guilds) {
                if (one == null || one.getTag() == null) {
                    continue;
                }

                Team team = scoreboard.getTeam(one.getTag());

                if (team == null) {
                    team = scoreboard.registerNewTeam(one.getTag());
                }

                for (User member : one.getMembers()) {
                    if (!team.hasEntry(member.getName())) {
                        team.addEntry(member.getName());
                    }
                }

                team.setPrefix(preparePrefix(other, one));
            }
        }
    }

    private void registerSoloTeam(User soloUser) {
        String teamName = soloUser.getName() + "_solo";
        Set<Guild> guilds = plugin.getGuildManager().getGuilds();

        if (teamName.length() > 16) {
            teamName = soloUser.getName();
        }

        for (Guild guild : guilds) {
            if (guild.getTag().equalsIgnoreCase(teamName)) {
                return;
            }
        }

        Team team = getScoreboard().getTeam(teamName);

        if (team == null) {
            team = getScoreboard().registerNewTeam(teamName);
        }

        if (!team.hasEntry(soloUser.getName())) {
            team.addEntry(soloUser.getName());
        }
    }

    public static String preparePrefix(String text, Guild guild) {
        Formatter formatter = Placeholders.GUILD.toFormatter(guild);
        String formatted = formatter.format(text);

        if (formatted.length() > 16) {
            formatted = formatted.substring(0, 16);
        }

        return formatted;
    }

    @NotNull
    public Scoreboard getScoreboard() {
        return this.user.getCache().getScoreboard()
                .orThrow(() -> new NullPointerException("scoreboard is null"));
    }

}
