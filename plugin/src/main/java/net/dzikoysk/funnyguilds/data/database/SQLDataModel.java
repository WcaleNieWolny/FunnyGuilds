package net.dzikoysk.funnyguilds.data.database;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import net.dzikoysk.funnyguilds.Entity;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalUpdateRequest;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.data.database.element.SQLBasicUtils;
import net.dzikoysk.funnyguilds.data.database.element.SQLElement;
import net.dzikoysk.funnyguilds.data.database.element.SQLNamedStatement;
import net.dzikoysk.funnyguilds.data.database.element.SQLTable;
import net.dzikoysk.funnyguilds.data.database.element.SQLType;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildDatabase;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.RegionUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserUtils;
import panda.std.Option;
import panda.std.reactive.Completable;
import panda.std.stream.PandaStream;

public class SQLDataModel implements DataModel {

    private static SQLDataModel instance;

    public static SQLTable tabUsers;
    public static SQLTable tabRegions;
    public static SQLTable tabGuilds;

    public SQLDataModel() {
        instance = this;

        PluginConfiguration pluginConfiguration = FunnyGuilds.getInstance().getPluginConfiguration();

        tabUsers = new SQLTable(pluginConfiguration.mysql.usersTableName);
        tabRegions = new SQLTable(pluginConfiguration.mysql.regionsTableName);
        tabGuilds = new SQLTable(pluginConfiguration.mysql.guildsTableName);

        tabUsers.add("uuid", SQLType.VARCHAR, 36, true);
        tabUsers.add("name", SQLType.TEXT, true);
        tabUsers.add("points", SQLType.INT, true);
        tabUsers.add("kills", SQLType.INT, true);
        tabUsers.add("deaths", SQLType.INT, true);
        tabUsers.add("assists", SQLType.INT, true);
        tabUsers.add("logouts", SQLType.INT, true);
        tabUsers.add("ban", SQLType.BIGINT);
        tabUsers.add("reason", SQLType.TEXT);
        tabUsers.setPrimaryKey("uuid");

        tabRegions.add("name", SQLType.VARCHAR, 100, true);
        tabRegions.add("center", SQLType.TEXT, true);
        tabRegions.add("size", SQLType.INT, true);
        tabRegions.add("enlarge", SQLType.INT, true);
        tabRegions.setPrimaryKey("name");

        tabGuilds.add("uuid", SQLType.VARCHAR, 100, true);
        tabGuilds.add("name", SQLType.TEXT, true);
        tabGuilds.add("tag", SQLType.TEXT, true);
        tabGuilds.add("owner", SQLType.TEXT, true);
        tabGuilds.add("home", SQLType.TEXT, true);
        tabGuilds.add("region", SQLType.TEXT, true);
        tabGuilds.add("regions", SQLType.TEXT, true);
        tabGuilds.add("members", SQLType.TEXT, true);
        tabGuilds.add("points", SQLType.INT, true);
        tabGuilds.add("lives", SQLType.INT, true);
        tabGuilds.add("ban", SQLType.BIGINT, true);
        tabGuilds.add("born", SQLType.BIGINT, true);
        tabGuilds.add("validity", SQLType.BIGINT, true);
        tabGuilds.add("pvp", SQLType.BOOLEAN, true);
        tabGuilds.add("attacked", SQLType.BIGINT); //TODO: [FG 5.0] attacked -> protection
        tabGuilds.add("allies", SQLType.TEXT);
        tabGuilds.add("enemies", SQLType.TEXT);
        tabGuilds.add("info", SQLType.TEXT);
        tabGuilds.add("deputy", SQLType.TEXT);
        tabGuilds.setPrimaryKey("uuid");
    }

    public static SQLDataModel getInstance() {
        if (instance != null) {
            return instance;
        }

        return new SQLDataModel();
    }

    public void load() throws SQLException {
        createTableIfNotExists(tabUsers);
        createTableIfNotExists(tabRegions);
        createTableIfNotExists(tabGuilds);

        loadUsers();
        loadRegions();

        FunnyGuilds plugin = FunnyGuilds.getInstance();
        GuildManager guildManager = plugin.getGuildManager();

        this.getAllGuilds().subscribe(guilds -> guilds.forEach(guildManager::addGuild));

        ConcurrencyManager concurrencyManager = plugin.getConcurrencyManager();
        concurrencyManager.postRequests(new PrefixGlobalUpdateRequest());
    }

    public void loadUsers() {
        SQLBasicUtils.getSelectAll(SQLDataModel.tabUsers).executeQuery(result -> {
            while (result.next()) {
                String userName = result.getString("name");

                if (!UserUtils.validateUsername(userName)) {
                    FunnyGuilds.getPluginLogger().warning("Skipping loading of user '" + userName + "'. Name is invalid.");
                    continue;
                }

                User user = DatabaseUser.deserialize(result);

                if (user != null) {
                    user.wasChanged();
                }
            }
        });

        FunnyGuilds.getPluginLogger().info("Loaded users: " + UserUtils.getUsers().size());
    }

    public void loadRegions() throws SQLException {
        if (!FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
            FunnyGuilds.getPluginLogger().info("Regions are disabled and thus - not loaded");
            return;
        }

        SQLBasicUtils.getSelectAll(SQLDataModel.tabRegions).executeQuery(result -> {
            while (result.next()) {
                Region region = DatabaseRegion.deserialize(result);

                if (region != null) {
                    region.wasChanged();
                    FunnyGuilds.getInstance().getRegionManager().addRegion(region);
                }
            }
        });

        FunnyGuilds.getPluginLogger().info("Loaded regions: " + RegionUtils.getRegions().size());
    }

    @Override
    public void save(boolean ignoreNotChanged) {
        for (User user : FunnyGuilds.getInstance().getUserManager().getUsers()) {
            if (ignoreNotChanged && !user.wasChanged()) {
                continue;
            }

            DatabaseUser.save(user);
        }

        for (Guild guild : FunnyGuilds.getInstance().getGuildManager().getGuilds()) {
            if (ignoreNotChanged && !guild.wasChanged()) {
                continue;
            }

            DatabaseGuild.save(guild);
        }

        if (!FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
            return;
        }

        for (Region region : RegionUtils.getRegions()) {
            if (ignoreNotChanged && !region.wasChanged()) {
                continue;
            }

            DatabaseRegion.save(region);
        }
    }

    public void createTableIfNotExists(SQLTable table) {
        SQLBasicUtils.getCreate(table).executeUpdate();

        for (SQLElement sqlElement : table.getSqlElements()) {
            SQLBasicUtils.getAlter(table, sqlElement).executeUpdate(true);
        }
    }

    @Override
    public Completable<Boolean> saveGuild(Guild guild) {
        String members = ChatUtils.toString(Entity.names(guild.getMembers()), false);
        String deputies = ChatUtils.toString(Entity.names(guild.getDeputies()), false);
        String allies = ChatUtils.toString(Entity.names(guild.getAllies()), false);
        String enemies = ChatUtils.toString(Entity.names(guild.getEnemies()), false);
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

        return Completable.completed(true);
    }

    @Override
    public Completable<Boolean> deleteGuild(Guild guild) {
        SQLNamedStatement statement = SQLBasicUtils.getDelete(SQLDataModel.tabGuilds);

        statement.set("uuid", guild.getUUID().toString());
        statement.executeUpdate();
        return Completable.completed(true);
    }

    @Override
    public Completable<Set<Guild>> getAllGuilds() {
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

        return Completable.completed(guilds);
    }

}
