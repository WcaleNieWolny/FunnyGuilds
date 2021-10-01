package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTask;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTaskBuilder;
import net.dzikoysk.funnyguilds.concurrency.requests.database.DatabaseUpdateGuildPointsRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.database.DatabaseUpdateUserPointsRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.dummy.DummyGlobalUpdateUserRequest;
import net.dzikoysk.funnyguilds.config.IntegerRange;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration.DataModel;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.rank.PointsChangeEvent;
import net.dzikoysk.funnyguilds.event.rank.RankChangeEvent;
import net.dzikoysk.funnyguilds.feature.hooks.PluginHook;
import net.dzikoysk.funnyguilds.nms.api.message.TitleMessage;
import net.dzikoysk.funnyguilds.rank.RankManager;
import net.dzikoysk.funnyguilds.rank.RankSystem;
import net.dzikoysk.funnyguilds.shared.MapUtil;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.MaterialUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.dzikoysk.funnyguilds.user.UserRank;
import net.dzikoysk.funnyguilds.user.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import panda.std.Option;
import panda.utilities.text.Formatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class PlayerDeath implements Listener {

    private final FunnyGuilds plugin;
    private final RankManager rankManager;
    private final UserManager userManager;
    private final RankSystem rankSystem;

    private final Map<EnderCrystal, Player> lastInteract = new HashMap<>();

    public PlayerDeath(FunnyGuilds plugin) {
        this.plugin = plugin;

        this.rankManager = plugin.getRankManager();
        this.userManager = plugin.getUserManager();

        this.rankSystem = RankSystem.create(plugin.getPluginConfiguration());
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity rightClicked = event.getRightClicked();

        if (!(rightClicked instanceof EnderCrystal)) {
            return;
        }

        lastInteract.put((EnderCrystal) rightClicked, player);
        Bukkit.getScheduler().runTaskLater(plugin, () -> lastInteract.remove(rightClicked), 10L);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        PluginConfiguration config = plugin.getPluginConfiguration();
        Player playerVictim = event.getEntity();
        Option<Player> attackerOption = Option.of(event.getEntity().getKiller());

        User victim = UserUtils.get(playerVictim.getUniqueId());
        UserCache victimCache = victim.getCache();

        UserRank victimRank = victim.getRank();
        victimRank.updateDeaths(currentValue -> currentValue + 1);

        EntityDamageEvent lastEvent = playerVictim.getLastDamageCause();

        if (attackerOption.isEmpty() && lastEvent instanceof EntityDamageByEntityEvent) {
            attackerOption = getDamager((EntityDamageByEntityEvent) lastEvent);
        }

        if (attackerOption.isEmpty()) {
            if (!config.considerLastAttackerAsKiller) {
                victimCache.clearDamage();
                return;
            }

            User lastAttacker = victimCache.getLastKiller();

            if (lastAttacker == null || !lastAttacker.isOnline()) {
                victimCache.clearDamage();
                return;
            }

            Long attackTime = victimCache.wasVictimOf(lastAttacker);

            if (attackTime == null || attackTime + config.lastAttackerAsKillerConsiderationTimeout_ < System.currentTimeMillis()) {
                victimCache.clearDamage();
                return;
            }

            attackerOption = Option.of(lastAttacker.getPlayer());
        }

        Player playerAttacker = attackerOption.get();
        User attacker = UserUtils.get(playerAttacker.getUniqueId());
        UserCache attackerCache = attacker.getCache();

        if (victim.equals(attacker)) {
            victimCache.clearDamage();
            return;
        }

        if (PluginHook.isPresent(PluginHook.PLUGIN_WORLDGUARD)) {
            if (PluginHook.WORLD_GUARD.isInNonPointsRegion(playerVictim.getLocation()) || PluginHook.WORLD_GUARD.isInNonPointsRegion(playerAttacker.getLocation())) {
                victimCache.clearDamage();
                return;
            }
        }

        MessageConfiguration messages = plugin.getMessageConfiguration();

        if (config.rankFarmingProtect) {
            Long attackTimestamp = attackerCache.wasAttackerOf(victim);
            Long victimTimestamp = attackerCache.wasVictimOf(attacker);

            if (attackTimestamp != null) {
                if (attackTimestamp + (config.rankFarmingCooldown * 1000L) >= System.currentTimeMillis()) {
                    playerVictim.sendMessage(messages.rankLastVictimV);
                    playerAttacker.sendMessage(messages.rankLastVictimA);

                    victimCache.clearDamage();
                    event.setDeathMessage(null);

                    return;
                }
            } else if (victimTimestamp != null) {
                if (victimTimestamp + (config.rankFarmingCooldown * 1000L) >= System.currentTimeMillis()) {
                    playerVictim.sendMessage(messages.rankLastAttackerV);
                    playerAttacker.sendMessage(messages.rankLastAttackerA);

                    victimCache.clearDamage();
                    event.setDeathMessage(null);

                    return;
                }
            }
        }

        if (config.rankIPProtect) {
            String attackerIP = playerAttacker.getAddress().getHostString();

            if (attackerIP != null && attackerIP.equalsIgnoreCase(playerVictim.getAddress().getHostString())) {
                playerVictim.sendMessage(messages.rankIPVictim);
                playerAttacker.sendMessage(messages.rankIPAttacker);

                victimCache.clearDamage();
                event.setDeathMessage(null);

                return;
            }
        }

        UserRank attackerRank = attacker.getRank();

        RankSystem.RankResult result = rankSystem.calculate(config.rankSystem, attackerRank.getPoints(), victimRank.getPoints());

        RankChangeEvent attackerEvent = new PointsChangeEvent(EventCause.USER, attackerRank, attacker, result.getAttackerPoints());
        RankChangeEvent victimEvent = new PointsChangeEvent(EventCause.USER, victimRank, attacker, result.getVictimPoints());

        List<String> assistEntries = new ArrayList<>();
        List<User> messageReceivers = new ArrayList<>();

        int victimPointsBeforeChange = victimRank.getPoints();

        if (SimpleEventHandler.handle(attackerEvent) && SimpleEventHandler.handle(victimEvent)) {
            double attackerDamage = victimCache.killedBy(attacker);

            if (config.assistEnable && victimCache.isAssisted()) {
                double toShare = attackerEvent.getChange() * (1 - config.assistKillerShare);
                double totalDamage = victimCache.getTotalDamage() + attackerDamage;
                int givenPoints = 0;

                Map<User, Double> damage = MapUtil.sortByValue(victimCache.getDamage());
                int assists = 0;

                for (Entry<User, Double> assist : damage.entrySet()) {
                    User assistUser = assist.getKey();
                    double assistFraction = assist.getValue() / totalDamage;
                    int addedPoints = (int) Math.round(assistFraction * toShare);

                    if (addedPoints <= 0) {
                        continue;
                    }

                    if (config.assistsLimit > 0) {
                        if (assists >= config.assistsLimit) {
                            continue;
                        }

                        assists++;
                    }

                    if (!config.broadcastDeathMessage) {
                        messageReceivers.add(assistUser);
                    }

                    givenPoints += addedPoints;

                    String assistEntry = StringUtils.replace(messages.rankAssistEntry, "{PLAYER}", assistUser.getName());
                    assistEntry = StringUtils.replace(assistEntry, "{+}", Integer.toString(addedPoints));
                    assistEntry = StringUtils.replace(assistEntry, "{SHARE}", ChatUtils.getPercent(assistFraction));
                    assistEntries.add(assistEntry);

                    assistUser.getRank().updatePoints(currentValue -> currentValue + addedPoints);
                    assistUser.getRank().updateAssists(currentValue -> currentValue + 1);
                }

                double updatedAttackerPoints = attackerEvent.getChange() - toShare + (givenPoints < toShare ? toShare - givenPoints : 0);
                attackerEvent.setChange((int) Math.round(updatedAttackerPoints));
            }

            attackerRank.updateKills(currentValue -> currentValue + 1);
            attackerRank.updatePoints(currentValue -> currentValue + attackerEvent.getChange());
            attackerCache.registerVictim(victim);

            victimPointsBeforeChange = victimRank.getPoints();

            victimRank.updatePoints(currentValue -> currentValue - victimEvent.getChange());
            victimCache.registerKiller(attacker);
            victimCache.clearDamage();

            if (!config.broadcastDeathMessage) {
                messageReceivers.add(attacker);
                messageReceivers.add(victim);
            }
        }

        ConcurrencyManager concurrencyManager = plugin.getConcurrencyManager();
        ConcurrencyTaskBuilder taskBuilder = ConcurrencyTask.builder();

        if (config.dataModel == DataModel.MYSQL) {
            if (victim.hasGuild()) {
                taskBuilder.delegate(new DatabaseUpdateGuildPointsRequest(victim.getGuild()));
            }

            if (attacker.hasGuild()) {
                taskBuilder.delegate(new DatabaseUpdateGuildPointsRequest(attacker.getGuild()));
            }

            taskBuilder.delegate(new DatabaseUpdateUserPointsRequest(victim));
            taskBuilder.delegate(new DatabaseUpdateUserPointsRequest(attacker));
        }

        concurrencyManager.postTask(taskBuilder
                .delegate(new DummyGlobalUpdateUserRequest(victim))
                .delegate(new DummyGlobalUpdateUserRequest(attacker))
                .build());

        Formatter killFormatter = new Formatter()
                .register("{ATTACKER}", attacker.getName())
                .register("{VICTIM}", victim.getName())
                .register("{+}", Integer.toString(attackerEvent.getChange()))
                .register("{-}", Math.min(victimPointsBeforeChange, victimEvent.getChange()))
                .register("{POINTS-FORMAT}", IntegerRange.inRangeToString(victimRank.getPoints(), config.pointsFormat))
                .register("{POINTS}", Integer.toString(victimRank.getPoints()))
                .register("{WEAPON}", MaterialUtils.getMaterialName(playerAttacker.getItemInHand().getType()))
                .register("{WEAPON-NAME}", MaterialUtils.getItemCustomName(playerAttacker.getItemInHand()))
                .register("{REMAINING-HEALTH}", String.format(Locale.US, "%.2f", playerAttacker.getHealth()))
                .register("{REMAINING-HEARTS}", Integer.toString((int) (playerAttacker.getHealth() / 2)))
                .register("{VTAG}", victim.hasGuild()
                        ? StringUtils.replace(config.chatGuild, "{TAG}", victim.getGuild().getTag())
                        : "")
                .register("{ATAG}", attacker.hasGuild()
                        ? StringUtils.replace(config.chatGuild, "{TAG}", attacker.getGuild().getTag())
                        : "")
                .register("{ASSISTS}", config.assistEnable && !assistEntries.isEmpty()
                        ? StringUtils.replace(messages.rankAssistMessage, "{ASSISTS}", String.join(messages.rankAssistDelimiter, assistEntries))
                        : "");

        if (config.displayTitleNotificationForKiller) {
            TitleMessage titleMessage = TitleMessage.builder()
                    .text(killFormatter.format(messages.rankKillTitle))
                    .subText(killFormatter.format(messages.rankKillSubtitle))
                    .fadeInDuration(config.notificationTitleFadeIn)
                    .stayDuration(config.notificationTitleStay)
                    .fadeOutDuration(config.notificationTitleFadeOut)
                    .build();

            plugin.getNmsAccessor().getMessageAccessor().sendTitleMessage(titleMessage, playerAttacker);
        }

        String deathMessage = killFormatter.format(messages.rankDeathMessage);

        if (config.broadcastDeathMessage) {
            if (config.ignoreDisabledDeathMessages) {
                for (Player player : event.getEntity().getWorld().getPlayers()) {
                    event.setDeathMessage(null);
                    player.sendMessage(deathMessage);
                }
            } else {
                event.setDeathMessage(deathMessage);
            }
        } else {
            event.setDeathMessage(null);

            for (User fighter : messageReceivers) {
                if (fighter.isOnline()) {
                    fighter.getPlayer().sendMessage(deathMessage);
                }
            }
        }

    }

    private Option<Player> getDamager(EntityDamageByEntityEvent event) {
        Option<Entity> damager = Option.of(event.getDamager());

        Option<Player> playerOption = Option.of(damager)
                .is(TNTPrimed.class)
                .map(TNTPrimed::getSource)
                .is(Player.class);

        return Option.of(damager)
                .is(EnderCrystal.class)
                .filter(lastInteract::containsKey)
                .map(lastInteract::get)
                .orElse(playerOption);
    }

}
