package net.dzikoysk.funnyguilds.feature.hooks;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.hooks.holographicdisplays.EmptyHologramManagerImpl;
import net.dzikoysk.funnyguilds.feature.hooks.holographicdisplays.FunnyHologramManager;
import net.dzikoysk.funnyguilds.feature.hooks.holographicdisplays.HolographicDisplaysHook;
import net.dzikoysk.funnyguilds.feature.hooks.worldedit.WorldEdit6Hook;
import net.dzikoysk.funnyguilds.feature.hooks.worldedit.WorldEdit7Hook;
import net.dzikoysk.funnyguilds.feature.hooks.worldedit.WorldEditHook;
import net.dzikoysk.funnyguilds.feature.hooks.worldguard.WorldGuard6Hook;
import net.dzikoysk.funnyguilds.feature.hooks.worldguard.WorldGuard7Hook;
import net.dzikoysk.funnyguilds.feature.hooks.worldguard.WorldGuardHook;
import org.bukkit.Bukkit;
import panda.std.stream.PandaStream;

public class HookManager {

    public static WorldGuardHook WORLD_GUARD;
    public static WorldEditHook WORLD_EDIT;
    public static FunnyTabHook FUNNY_TAB;
    public static VaultHook VAULT;
    public static BungeeTabListPlusHook BUNGEE_TAB_LIST_PLUS;
    public static MVdWPlaceholderAPIHook MVDW_PLACEHOLDER_API;
    public static PlaceholderAPIHook PLACEHOLDER_API;
    public static LeaderHeadsHook LEADER_HEADS;
    public static FunnyHologramManager HOLOGRAPHIC_DISPLAYS;

    private final FunnyGuilds plugin;

    private final Map<String, PluginHook> pluginHooks = new HashMap<>();

    public HookManager(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    public void setupHooks() {
        WORLD_GUARD = setupHook("WorldGuard", pluginName -> {
            try {
                Class.forName("com.sk89q.worldguard.protection.flags.registry.FlagRegistry");
                Class.forName("com.sk89q.worldguard.protection.flags.Flag");

                String worldGuardVersion = Bukkit.getPluginManager().getPlugin(pluginName).getDescription().getVersion();
                return worldGuardVersion.startsWith("7") ? new WorldGuard7Hook(pluginName) : new WorldGuard6Hook(pluginName);
            }
            catch (ClassNotFoundException exception) {
                FunnyGuilds.getPluginLogger().warning("FunnyGuilds supports only WorldGuard v6.2 or newer");
                return null;
            }
        });
        WORLD_EDIT = setupHook("WorldEdit", pluginName -> {
            try {
                Class.forName("com.sk89q.worldedit.Vector");
                return new WorldEdit6Hook(pluginName);
            }
            catch (ClassNotFoundException ignored) {
                return new WorldEdit7Hook(pluginName);
            }
        });
        VAULT = setupHook("Vault", VaultHook::new);
        BUNGEE_TAB_LIST_PLUS = setupHook("BungeeTabListPlus", pluginName -> {
            try {
                Class.forName("codecrafter47.bungeetablistplus.api.bukkit.Variable");
                return new BungeeTabListPlusHook(pluginName, plugin);
            }
            catch (ClassNotFoundException exception) {
                return null;
            }
        });
        MVDW_PLACEHOLDER_API = setupHook("MVdWPlaceholderAPI", pluginName -> new MVdWPlaceholderAPIHook(pluginName, plugin));
        PLACEHOLDER_API = setupHook("PlaceholderAPI", pluginName -> new PlaceholderAPIHook(pluginName, plugin));
        LEADER_HEADS = setupHook("LeaderHeads", pluginName -> new LeaderHeadsHook(pluginName, plugin));
        HOLOGRAPHIC_DISPLAYS = setupHook("HolographicDisplays", pluginName -> Bukkit.getPluginManager().getPlugin("HolographicDisplays") != null
                ? new HolographicDisplaysHook(pluginName, plugin)
                : new EmptyHologramManagerImpl());
        FUNNY_TAB = setupHook("FunnyTab", pluginName -> new FunnyTabHook(pluginName, plugin), false);
    }

    public <T> T setupHook(String pluginName, Function<String, T> hookSupplier, boolean notifyIfMissing) {
        if (hookSupplier == null) {
            return null;
        }

        if (Bukkit.getPluginManager().getPlugin(pluginName) == null) {
            if (notifyIfMissing) {
                FunnyGuilds.getPluginLogger().info(pluginName + " plugin could not be found, some features may not be available");
            }
            return hookSupplier.apply(pluginName);
        }

        T hook = hookSupplier.apply(pluginName);
        if (hook == null) {
            return null;
        }

        if (!(hook instanceof PluginHook)) {
            return hook;
        }

        if (PandaStream.of(plugin.getPluginConfiguration().disabledHooks)
                .find(disabledHook -> disabledHook.equalsIgnoreCase(pluginName))
                .isPresent()) {
            if (!pluginName.equalsIgnoreCase("FunnyTab")) {
                FunnyGuilds.getPluginLogger().warning(pluginName + " plugin hook is disabled in configuration, some features may not be available");
                return hook;
            }

            FunnyGuilds.getPluginLogger().warning("You can't disable FunnyTab plugin hook lol");
        }

        this.pluginHooks.put(pluginName, (PluginHook) hook);
        return hook;
    }

    public <T> T setupHook(String pluginName, Function<String, T> hookSupplier) {
        return this.setupHook(pluginName, hookSupplier, true);
    }

    public void earlyInit() {
        pluginHooks.forEach((pluginName, hook) -> {
            try {
                hook.callEarlyInit();
            }
            catch (Exception exception) {
                FunnyGuilds.getPluginLogger().error("Failed to early initialize " + pluginName + " plugin hook");
                exception.printStackTrace();
            }
        });
    }

    public void init() {
        pluginHooks.forEach((pluginName, hook) -> {
            try {
                FunnyGuilds.getPluginLogger().info(pluginName + " plugin hook has been enabled!");
                hook.callInit();
            }
            catch (Exception exception) {
                FunnyGuilds.getPluginLogger().error("Failed to initialize " + pluginName + " plugin hook");
                exception.printStackTrace();
            }
        });
    }

    public PluginHook getHook(String pluginName) {
        return pluginHooks.get(pluginName);
    }

    public boolean isHookPresent(String pluginName) {
        return pluginHooks.containsKey(pluginName);
    }

}
