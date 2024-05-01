package cc.unilock.nilcord;

import cc.unilock.nilcord.util.TextUtils;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;

import java.time.Duration;

import static cc.unilock.nilcord.NilcordPremain.CONFIG;
import static cc.unilock.nilcord.NilcordPremain.LOGGER;

public class EventListener {
    public void serverStart() {
        try {
            NilcordPremain.discord.getJda().awaitReady();
            if (!CONFIG.formatting.discord.server_start_message.value().isBlank()) {
                NilcordPremain.discord.sendMessageToDiscord(CONFIG.formatting.discord.server_start_message.value());
            }
        } catch (InterruptedException e) {
            LOGGER.error(e.toString());
        }
    }

    public void serverStop() {
        try {
            if (!CONFIG.formatting.discord.server_stop_message.value().isBlank()) {
                NilcordPremain.discord.sendMessageToDiscord(CONFIG.formatting.discord.server_stop_message.value());
            }
            NilcordPremain.discord.shutdown();
            NilcordPremain.discord.getJda().awaitShutdown(Duration.ofSeconds(3));
        } catch (InterruptedException e) {
            LOGGER.error(e.toString());
        }
    }

    public void playerChatMessage(ServerPlayerEntity player, Text message) {
        if (CONFIG.discord.webhook.enabled.value() ? CONFIG.formatting.discord.webhook.chat_message.value().isBlank() : CONFIG.formatting.discord.chat_message.value().isBlank()) return;

        NilcordPremain.discord.onPlayerChatMessage(player, message);
    }

    public void playerJoin(ServerPlayerEntity player) {
        if (CONFIG.formatting.discord.join_message.value().isBlank()) return;

        String message = TextUtils.parsePlayer(
                CONFIG.formatting.discord.join_message.value(),
                player
        ).getString();
        NilcordPremain.discord.sendMessageToDiscord(message);
    }

    public void playerLeave(ServerPlayerEntity player) {
        if (CONFIG.formatting.discord.leave_message.value().isBlank()) return;

        String message = TextUtils.parsePlayer(
                CONFIG.formatting.discord.leave_message.value(),
                player
        ).getString();
        NilcordPremain.discord.sendMessageToDiscord(message);
    }

    public void playerAdvancement(ServerPlayerEntity player, Advancement advancement) {
        AdvancementDisplay display = advancement.getDisplay();

        if (player.getAdvancementTracker().getProgress(advancement).isDone()
                && display != null
                && display.shouldAnnounceToChat()
                && player.getWorld().getGameRules().getBoolean(GameRules.ANNOUNCE_ADVANCEMENTS)
        ) {
            String template = switch (display.getFrame()) {
                case CHALLENGE -> CONFIG.formatting.discord.advancement_challenge_message.value();
                case GOAL -> CONFIG.formatting.discord.advancement_goal_message.value();
                case TASK -> CONFIG.formatting.discord.advancement_task_message.value();
                default -> CONFIG.formatting.discord.advancement_fallback_message.value();
            };

            if (template.isBlank()) return;

            String message = TextUtils.parseAdvancement(
                    template,
                    player,
                    display
            ).getString();
            NilcordPremain.discord.sendMessageToDiscord(message);
        }
    }

    public void playerDeath(ServerPlayerEntity player, DamageSource source) {
        if (CONFIG.formatting.discord.death_message.value().isBlank()) return;

        String message = TextUtils.parseDeath(
                CONFIG.formatting.discord.death_message.value(),
                player,
                source
        ).getString();
        NilcordPremain.discord.sendMessageToDiscord(message);
    }
}
