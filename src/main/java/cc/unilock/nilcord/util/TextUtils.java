package cc.unilock.nilcord.util;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.TextParserUtils;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.regex.Pattern;

public class TextUtils {
    private static final Pattern ANGLE_BRACKETS = Pattern.compile("(?<!((?<!(\\\\))\\\\))<(?<id>[^<>]+)>");

    private static Text parse(String str) {
        return TextParserUtils.formatText(str);
    }

    public static Text parsePlayer(String template, ServerPlayerEntity player) {
        Map<String, Text> placeholders = Map.of(
                "displayname", player.getDisplayName(),
                "username", Text.literal(player.getGameProfile().getName()),
                "uuid", Text.literal(player.getGameProfile().getId().toString())
        );

        return Placeholders.parseText(Placeholders.parseText(parse(template), ANGLE_BRACKETS, placeholders), PlaceholderContext.of(player));
    }

    public static Text parseMessage(String template, ServerPlayerEntity player, Text message) {
        Map<String, Text> placeholders = Map.of(
                "message", message
        );

        return Placeholders.parseText(parsePlayer(template, player), ANGLE_BRACKETS, placeholders);
    }

    public static Text parseAdvancement(String template, ServerPlayerEntity player, AdvancementDisplay display) {
        Map<String, Text> placeholders = Map.of(
                "advancement_title", display.getTitle(),
                "advancement_description", display.getDescription()
        );

        return Placeholders.parseText(parsePlayer(template, player), ANGLE_BRACKETS, placeholders);
    }

    public static Text parseDeath(String template, ServerPlayerEntity player, DamageSource source) {
        Map<String, Text> placeholders = Map.of(
                "death_message", source.getDeathMessage(player)
        );

        return Placeholders.parseText(parsePlayer(template, player), ANGLE_BRACKETS, placeholders);
    }
}
