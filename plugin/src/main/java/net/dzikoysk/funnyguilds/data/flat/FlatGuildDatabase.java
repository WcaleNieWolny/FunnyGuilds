package net.dzikoysk.funnyguilds.data.flat;

import net.dzikoysk.funnyguilds.Entity;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.requests.database.DatabaseFixAlliesRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalUpdateRequest;
import net.dzikoysk.funnyguilds.data.util.YamlWrapper;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildDatabase;
import net.dzikoysk.funnyguilds.guild.RegionUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class FlatGuildDatabase implements GuildDatabase {

    private final FlatDataModel flatDataModel;

    public FlatGuildDatabase(FlatDataModel flatDataModel) {
        this.flatDataModel = flatDataModel;
    }

    @Override
    public void saveGuild(Guild guild) {
        if (guild.getName() == null) {
            FunnyGuilds.getPluginLogger().error("[Serialize] Cannot serialize guild! Caused by: name is null");
            return;
        }

        if (guild.getTag() == null) {
            FunnyGuilds.getPluginLogger().error("[Serialize] Cannot serialize guild: " + guild.getName() + "! Caused by: tag is null");
            return;
        }

        if (guild.getOwner() == null) {
            FunnyGuilds.getPluginLogger().error("[Serialize] Cannot serialize guild: " + guild.getName() + "! Caused by: owner is null");
            return;
        }

        if (guild.getRegion() == null && FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
            FunnyGuilds.getPluginLogger().error("[Serialize] Cannot serialize guild: " + guild.getName() + "! Caused by: region is null");
            return;
        }

        File file = flatDataModel.loadCustomFile(Entity.EntityType.GUILD, guild.getName());
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
    }

    @Override
    public void deleteGuild(Guild guild) {
        flatDataModel.getGuildFile(guild).delete();
    }

    @Override
    public Set<Guild> getAllGuilds() {
        Set<Guild> guilds = new HashSet<>();
        File[] path = flatDataModel.getGuildsFolder().listFiles();
        int errors = 0;

        if (path == null) {
            FunnyGuilds.getPluginLogger().warning("Guilds directory is empty");
            return new HashSet<>();
        }

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
        return guilds;
    }

}
