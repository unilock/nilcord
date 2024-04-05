package cc.unilock.nilcord;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;

import java.time.Duration;

import static cc.unilock.nilcord.NilcordPremain.CONFIG;
import static cc.unilock.nilcord.NilcordPremain.LOGGER;

public class EventListener {
    public void serverStart() {
        try {
            NilcordPremain.discord.getJda().awaitReady();
            NilcordPremain.discord.sendMessageToDiscord(CONFIG.formatting.discord.server_start_message.value());
        } catch (InterruptedException e) {
            LOGGER.error(e.toString());
        }
    }

    public void serverStop() {
        try {
            NilcordPremain.discord.sendMessageToDiscord(CONFIG.formatting.discord.server_stop_message.value());
            NilcordPremain.discord.shutdown();
            NilcordPremain.discord.getJda().awaitShutdown(Duration.ofSeconds(3));
        } catch (InterruptedException e) {
            LOGGER.error(e.toString());
        }
    }

    public void playerChatMessage(ServerPlayerEntity player, String message) {
        NilcordPremain.discord.onPlayerChatMessage(player, message);
    }

    public void playerJoin(ServerPlayerEntity player) {
        String message = CONFIG.formatting.discord.join_message.value()
                .replace("<username>", player.getGameProfile().getName())
                .replace("<displayname>", player.getDisplayName().getString());
        NilcordPremain.discord.sendMessageToDiscord(message);
    }

    public void playerLeave(ServerPlayerEntity player) {
        String message = CONFIG.formatting.discord.leave_message.value()
                .replace("<username>", player.getGameProfile().getName())
                .replace("<displayname>", player.getDisplayName().getString());
        NilcordPremain.discord.sendMessageToDiscord(message);
    }

    public void playerAdvancement(ServerPlayerEntity player, Advancement advancement) {
        AdvancementDisplay display = advancement.getDisplay();

        if (player.getAdvancementTracker().getProgress(advancement).isDone()
                && display != null
                && display.shouldAnnounceToChat()
                && player.getWorld().getGameRules().getBoolean(GameRules.ANNOUNCE_ADVANCEMENTS)
        ) {
            String username = player.getGameProfile().getName();
            String title = display.getTitle().getString();
            String description = display.getDescription().getString();

//            String advType = switch (display.getFrame()) {
//                case CHALLENGE -> YEP_ADV_CHALLENGE;
//                case GOAL -> YEP_ADV_GOAL;
//                case TASK -> YEP_ADV_TASK;
//                default -> YEP_ADV_DEFAULT;
//            };

            String message = CONFIG.formatting.discord.advancement_message.value()
                    .replace("<username>", username)
                    .replace("<displayname>", player.getDisplayName().getString())
                    .replace("<advancement_title>", title)
                    .replace("<advancement_description>", description);
            NilcordPremain.discord.sendMessageToDiscord(message);
        }
    }

    public void playerDeath(ServerPlayerEntity player, DamageSource source) {
        String message = CONFIG.formatting.discord.death_message.value()
                .replace("<username>", player.getGameProfile().getName())
                .replace("<displayname>", player.getDisplayName().getString())
                .replace("<death_message>", source.getDeathMessage(player).getString());
        NilcordPremain.discord.sendMessageToDiscord(message);
    }
}
