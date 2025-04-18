package cc.unilock.nilcord;

import cc.unilock.nilcord.util.TextUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;

import java.time.Duration;

import static cc.unilock.nilcord.NilcordPremain.CONFIG;
import static cc.unilock.nilcord.NilcordPremain.LOGGER;

public class EventListener {
    private boolean crash = false;

    public void serverStart() {
        try {
            NilcordPremain.discord.getJda().awaitReady();
            if (!CONFIG.formatting.discord.server_start_message.value().isEmpty()) {
                NilcordPremain.discord.sendMessageToDiscord(CONFIG.formatting.discord.server_start_message.value());
            }
        } catch (InterruptedException e) {
            LOGGER.error(e.toString());
        }
    }

    public void serverStop() {
        try {
            if (!CONFIG.formatting.discord.server_stop_message.value().isEmpty() && !this.crash) {
                NilcordPremain.discord.sendMessageToDiscord(CONFIG.formatting.discord.server_stop_message.value());
            }
            NilcordPremain.discord.shutdown();
            NilcordPremain.discord.getJda().awaitShutdown(Duration.ofSeconds(3));
        } catch (InterruptedException e) {
            LOGGER.error(e.toString());
        }
    }

    // TODO: upload report.asString() to mclogs or something (configurable)
    public void serverCrash(CrashReport report) {
        if (CONFIG.formatting.discord.server_crash_message.value().isEmpty()) return;

        this.crash = true;
        NilcordPremain.discord.sendMessageToDiscord(CONFIG.formatting.discord.server_crash_message.value());
    }

    public void playerChatMessage(EntityPlayerMP player, String message) {
        if (CONFIG.discord.webhook.enabled.value() ? CONFIG.formatting.discord.webhook.chat_message.value().isEmpty() : CONFIG.formatting.discord.chat_message.value().isEmpty()) return;

        NilcordPremain.discord.onPlayerChatMessage(player, message);
    }

    public void playerJoin(EntityPlayerMP player) {
        if (CONFIG.formatting.discord.join_message.value().isEmpty()) return;

        String message = TextUtils.parsePlayer(
                CONFIG.formatting.discord.join_message.value(),
                player
        );
        NilcordPremain.discord.sendMessageToDiscord(message);
    }

    public void playerLeave(EntityPlayerMP player) {
        if (CONFIG.formatting.discord.leave_message.value().isEmpty()) return;

        String message = TextUtils.parsePlayer(
                CONFIG.formatting.discord.leave_message.value(),
                player
        );
        NilcordPremain.discord.sendMessageToDiscord(message);
    }

    public void playerAdvancement(EntityPlayerMP player, Advancement advancement) {
        DisplayInfo display = advancement.getDisplay();

        if (player.getAdvancements().getProgress(advancement).isDone()
                && display != null
                && display.shouldAnnounceToChat()
                && player.world.getGameRules().getBoolean("announceAdvancements")
        ) {
            String template = switch (display.getFrame()) {
                case CHALLENGE -> CONFIG.formatting.discord.advancement_challenge_message.value();
                case GOAL -> CONFIG.formatting.discord.advancement_goal_message.value();
                case TASK -> CONFIG.formatting.discord.advancement_task_message.value();
                default -> CONFIG.formatting.discord.advancement_fallback_message.value();
            };

            if (template.isEmpty()) return;

            String message = TextUtils.parseAdvancement(
                    template,
                    player,
                    display
            );
            NilcordPremain.discord.sendMessageToDiscord(message);
        }
    }

    public void playerDeath(EntityPlayerMP player, DamageSource source) {
        if (CONFIG.formatting.discord.death_message.value().isEmpty()) return;

        String message = TextUtils.parseDeath(
                CONFIG.formatting.discord.death_message.value(),
                player,
                source
        );
        NilcordPremain.discord.sendMessageToDiscord(message);
    }
}
