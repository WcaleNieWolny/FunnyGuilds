package net.dzikoysk.funnyguilds.data.flat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;

import net.dzikoysk.funnyguilds.Entity;
import net.dzikoysk.funnyguilds.Entity.EntityType;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.requests.database.DatabaseFixAlliesRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalUpdateRequest;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.data.util.YamlWrapper;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.RegionUtils;
import net.dzikoysk.funnyguilds.shared.IOUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserUtils;
import org.apache.commons.lang3.StringUtils;
import panda.std.reactive.Completable;

public class FlatDataModel implements DataModel {

    private final FunnyGuilds plugin;
    private final File guildsFolderFile;
    private final File regionsFolderFile;
    private final File usersFolderFile;

    public FlatDataModel(FunnyGuilds plugin) {
        this.plugin = plugin;
        this.guildsFolderFile = new File(plugin.getPluginDataFolder(), "guilds");
        this.regionsFolderFile = new File(plugin.getPluginDataFolder(), "regions");
        this.usersFolderFile = new File(plugin.getPluginDataFolder(), "users");

        FlatPatcher flatPatcher = new FlatPatcher();
        flatPatcher.patch(this);
    }

    public File getGuildsFolder() {
        return this.guildsFolderFile;
    }

    public File getRegionsFolder() {
        return this.regionsFolderFile;
    }

    public File getUsersFolder() {
        return this.usersFolderFile;
    }

    File loadCustomFile(EntityType type, String name) {
        switch (type) {
            case GUILD: {
                File file = new File(this.guildsFolderFile, name + ".yml");
                IOUtils.initialize(file, true);
                return file;
            }
            case REGION: {
                File file = new File(this.regionsFolderFile, name + ".yml");
                IOUtils.initialize(file, true);
                return file;
            }
            case USER: {
                File file = new File(this.usersFolderFile, name + ".yml");
                IOUtils.initialize(file, true);
                return file;
            }
            default: return null;
        }
    }

    public File getUserFile(User user) {
        return new File(this.usersFolderFile, user.getUUID() + ".yml");
    }

    public File getRegionFile(Region region) {
        return new File(this.regionsFolderFile, region.getName() + ".yml");
    }

    public File getGuildFile(Guild guild) {
        return new File(this.guildsFolderFile, guild.getName() + ".yml");
    }

    @Override
    public void load() {
        this.loadUsers();
        this.loadRegions();

        GuildManager guildManager = plugin.getGuildManager();

        this.getAllGuilds().subscribe(guilds -> guilds.forEach(guild -> guildManager.addGuild(guild)));

        this.validateLoadedData();
    }

    @Override
    public void save(boolean ignoreNotChanged) {
        this.saveUsers(ignoreNotChanged);
        this.saveRegions(ignoreNotChanged);
        this.saveGuilds(ignoreNotChanged);
    }

    private void saveUsers(boolean ignoreNotChanged) {
        if (UserUtils.getUsers().isEmpty()) {
            return;
        }

        int errors = 0;

        for (User user : UserUtils.getUsers()) {
            if (user.getUUID() == null || user.getName() == null) {
                errors++;
                continue;
            }

            if (ignoreNotChanged && !user.wasChanged()) {
                continue;
            }

            new FlatUser(user).serialize(this);
        }

        if (errors > 0) {
            FunnyGuilds.getPluginLogger().error("Users save errors " + errors);
        }
    }

    private void loadUsers() {
        File[] path = usersFolderFile.listFiles();
        int errors = 0;

        if (path == null) {
            FunnyGuilds.getPluginLogger().warning("Users directory is empty");
            return;
        }

        for (File file : path) {
            if (file.length() == 0) {
                continue;
            }

            String filenameWithoutExtension = StringUtils.removeEnd(file.getName(), ".yml");

            if (!UserUtils.validateUUID(filenameWithoutExtension)) {
                if (UserUtils.validateUsername(filenameWithoutExtension)) {
                    file = migrateUser(file);

                    if (file == null) {
                        continue;
                    }
                }
                else {
                    FunnyGuilds.getPluginLogger().warning("Skipping loading of user file '" + file.getName() + "'. Name is invalid.");
                    continue;
                }
            }

            User user = FlatUser.deserialize(file);

            if (user == null) {
                errors++;
                continue;
            }

            user.wasChanged();
        }

        if (errors > 0) {
            FunnyGuilds.getPluginLogger().error("Users load errors " + errors);
        }

        FunnyGuilds.getPluginLogger().info("Loaded users: " + UserUtils.getUsers().size());
    }

    private File migrateUser(File file) {
        YamlWrapper wrapper = new YamlWrapper(file);

        String id = wrapper.getString("uuid");

        if (id == null || !UserUtils.validateUUID(id)) {
            FunnyGuilds.getPluginLogger().warning("Skipping loading of user file '" + file.getName() + "'. UUID is invalid.");
            return null;
        }

        Path source = file.toPath();
        Path target = source.resolveSibling(String.format("%s.yml", id));

        if (Files.exists(target)) {
            return target.toFile();
        }

        try {
            return Files.move(source, target, StandardCopyOption.REPLACE_EXISTING).toFile();
        }
        catch (IOException e) {
            throw new RuntimeException("Could not move file '" + source + "' to '" + target + "'.", e.getCause());
        }
    }

    private void saveRegions(boolean ignoreNotChanged) {
        if (!FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
            return;
        }

        int errors = 0;

        for (Region region : RegionUtils.getRegions()) {
            if (ignoreNotChanged && !region.wasChanged()) {
                continue;
            }

            if (!new FlatRegion(region).serialize(this)) {
                errors++;
            }
        }

        if (errors > 0) {
            FunnyGuilds.getPluginLogger().error("Regions save errors " + errors);
        }
    }

    private void loadRegions() {
        if (!FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
            FunnyGuilds.getPluginLogger().info("Regions are disabled and thus - not loaded");
            return;
        }

        File[] path = regionsFolderFile.listFiles();
        int errors = 0;

        if (path == null) {
            FunnyGuilds.getPluginLogger().warning("Regions directory is empty");
            return;
        }

        for (File file : path) {
            Region region = FlatRegion.deserialize(file);

            if (region == null) {
                errors++;
                continue;
            }

            region.wasChanged();
            FunnyGuilds.getInstance().getRegionManager().addRegion(region);
        }

        if (errors > 0) {
            FunnyGuilds.getPluginLogger().error("Guild load errors " + errors);
        }

        FunnyGuilds.getPluginLogger().info("Loaded regions: " + RegionUtils.getRegions().size());
    }

    private void saveGuilds(boolean ignoreNotChanged) {
        for (Guild guild : GuildUtils.getGuilds()) {
            if (ignoreNotChanged && !guild.wasChanged()) {
                continue;
            }

            this.saveGuild(guild);
        }
    }

    @Override
    public Completable<Boolean> saveGuild(Guild guild) {
        if (guild.getName() == null) {
            FunnyGuilds.getPluginLogger().error("[Serialize] Cannot serialize guild! Caused by: name is null");
            return Completable.completed(false);
        }

        if (guild.getTag() == null) {
            FunnyGuilds.getPluginLogger().error("[Serialize] Cannot serialize guild: " + guild.getName() + "! Caused by: tag is null");
            return Completable.completed(false);
        }

        if (guild.getOwner() == null) {
            FunnyGuilds.getPluginLogger().error("[Serialize] Cannot serialize guild: " + guild.getName() + "! Caused by: owner is null");
            return Completable.completed(false);
        }

        if (guild.getRegion() == null && FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
            FunnyGuilds.getPluginLogger().error("[Serialize] Cannot serialize guild: " + guild.getName() + "! Caused by: region is null");
            return Completable.completed(false);
        }

        File file = this.loadCustomFile(Entity.EntityType.GUILD, guild.getName());
        YamlWrapper wrapper = new YamlWrapper(file);

        wrapper.set("uuid", guild.getUUID().toString());
        wrapper.set("name", guild.getName());
        wrapper.set("tag", guild.getTag());
        wrapper.set("owner", guild.getOwner().getName());
        wrapper.set("home", LocationUtils.toString(guild.getHome()));
        wrapper.set("members", Entity.names(guild.getMembers()));
        wrapper.set("region", RegionUtils.toString(guild.getRegion()));
        wrapper.set("regions", null);
        wrapper.set("allies", Entity.names(guild.getAllies()));
        wrapper.set("enemies", Entity.names(guild.getEnemies()));
        wrapper.set("born", guild.getBorn());
        wrapper.set("validity", guild.getValidity());
        wrapper.set("attacked", guild.getProtection()); //TODO: [FG 5.0] attacked -> protection
        wrapper.set("lives", guild.getLives());
        wrapper.set("ban", guild.getBan());
        wrapper.set("pvp", guild.getPvP());
        wrapper.set("deputy", ChatUtils.toString(Entity.names(guild.getDeputies()), false));

        wrapper.save();
        return Completable.completed(true);
    }

    @Override
    public Completable<Boolean> deleteGuild(Guild guild) {
        boolean deleted = this.getGuildFile(guild).delete();

        return Completable.completed(deleted);
    }

    @Override
    public Completable<Set<Guild>> getAllGuilds() {
        File[] path = guildsFolderFile.listFiles();
        int errors = 0;

        if (path == null) {
            FunnyGuilds.getPluginLogger().warning("Guilds directory is empty");
            return Completable.completed(new HashSet<>());
        }

        Set<Guild> guilds = new HashSet<>();

        for (File file : path) {
            Guild guild = FlatGuild.deserialize(file);

            if (guild == null) {
                errors++;
                continue;
            }

            guild.wasChanged();
            guilds.add(guild);
        }

        for (Guild guild : guilds) {
            if (guild.getOwner() != null) {
                continue;
            }

            errors++;
            FunnyGuilds.getPluginLogger().error("In guild " + guild.getTag() + " owner not exist!");
        }

        if (errors > 0) {
            FunnyGuilds.getPluginLogger().error("Guild load errors " + errors);
        }

        ConcurrencyManager concurrencyManager = FunnyGuilds.getInstance().getConcurrencyManager();
        concurrencyManager.postRequests(new DatabaseFixAlliesRequest(), new PrefixGlobalUpdateRequest());

        FunnyGuilds.getPluginLogger().info("Loaded guilds: " + guilds.size());
        return Completable.completed(guilds);
    }

}
