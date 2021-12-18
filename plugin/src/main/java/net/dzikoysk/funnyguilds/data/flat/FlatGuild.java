package net.dzikoysk.funnyguilds.data.flat;

import java.io.File;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.dzikoysk.funnyguilds.Entity.EntityType;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.data.util.YamlWrapper;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.RegionUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserUtils;
import org.bukkit.Location;
import panda.std.Option;

@Deprecated
public class FlatGuild {

    private final Guild guild;

    public FlatGuild(Guild guild) {
        this.guild = guild;
    }

    public static Guild deserialize(File file) {
        PluginConfiguration configuration = FunnyGuilds.getInstance().getPluginConfiguration();
        YamlWrapper wrapper = new YamlWrapper(file);

        String id = wrapper.getString("uuid");
        String name = wrapper.getString("name");
        String tag = wrapper.getString("tag");
        String ownerName = wrapper.getString("owner");
        String deputyName = wrapper.getString("deputy");
        String hs = wrapper.getString("home");
        String regionName = wrapper.getString("region");
        boolean pvp = wrapper.getBoolean("pvp");
        long born = wrapper.getLong("born");
        long validity = wrapper.getLong("validity");
        long attacked = wrapper.getLong("attacked");
        long ban = wrapper.getLong("ban");
        int lives = wrapper.getInt("lives");

        if (name == null) {
            FunnyGuilds.getPluginLogger().error("[Deserialize] Cannot deserialize guild! Caused by: name is null");
            return null;
        }

        if (tag == null) {
            FunnyGuilds.getPluginLogger().error("[Deserialize] Cannot deserialize guild: " + name + "! Caused by: tag is null");
            return null;
        }

        if (ownerName == null) {
            FunnyGuilds.getPluginLogger().error("[Deserialize] Cannot deserialize guild: " + name + "! Caused by: owner is null");
            return null;
        }

        if (regionName == null && configuration.regionsEnabled) {
            FunnyGuilds.getPluginLogger().error("[Deserialize] Cannot deserialize guild: " + name + "! Caused by: region is null");
            return null;
        }

        Set<String> memberNames = loadSet(wrapper, "members");
        Set<String> allyNames = loadSet(wrapper, "allies");
        Set<String> enemyNames = loadSet(wrapper, "enemies");

        Option<Region> regionOption = FunnyGuilds.getInstance().getRegionManager().findByName(regionName);
        if (regionOption.isEmpty() && configuration.regionsEnabled) {
            FunnyGuilds.getPluginLogger().error("[Deserialize] Cannot deserialize guild: " + name + "! Caused by: region (object) is null");
            return null;
        }
        Region region = regionOption.get();

        UUID uuid = UUID.randomUUID();
        if (id != null && !id.isEmpty()) {
            uuid = UUID.fromString(id);
        }

        final User owner = UserUtils.get(ownerName);

        Set<User> deputies = ConcurrentHashMap.newKeySet(1);
        if (deputyName != null && !deputyName.isEmpty()) {
            deputies = UserUtils.getUsersFromString(ChatUtils.fromString(deputyName));
        }

        Location home = null;

        if (region != null) {
            home = region.getCenter();

            if (hs != null) {
                home = LocationUtils.parseLocation(hs);
            }
        }

        if (memberNames == null || memberNames.isEmpty()) {
            memberNames = new HashSet<>();
            memberNames.add(ownerName);
        }

        GuildManager guildManager = FunnyGuilds.getInstance().getGuildManager();
        Set<User> members = FunnyGuilds.getInstance().getUserManager().findByNames(memberNames);
        Set<Guild> allies = guildManager.findByNames(allyNames);
        Set<Guild> enemies = guildManager.findByNames(enemyNames);

        if (born == 0) {
            born = System.currentTimeMillis();
        }

        if (validity == 0) {
            validity = Instant.now().plus(configuration.validityStart).toEpochMilli();
        }

        if (lives == 0) {
            lives = configuration.warLives;
        }

        final Object[] values = new Object[17];

        values[0] = uuid;
        values[1] = name;
        values[2] = tag;
        values[3] = owner;
        values[4] = home;
        values[5] = region;
        values[6] = members;
        values[7] = allies;
        values[8] = enemies;
        values[9] = born;
        values[10] = validity;
        values[11] = attacked;
        values[12] = lives;
        values[13] = ban;
        values[14] = deputies;
        values[15] = pvp;

        return DeserializationUtils.deserializeGuild(configuration, values);
    }

    @SuppressWarnings("unchecked")
    private static Set<String> loadSet(YamlWrapper data, String key) {
        Object collection = data.get(key);

        if (collection instanceof List) {
            return new HashSet<>((List<String>) collection);
        }
        else if (collection instanceof Set) {
            return (Set<String>) collection;
        }

        return null;
    }

}
