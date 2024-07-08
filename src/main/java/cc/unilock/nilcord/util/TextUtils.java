package cc.unilock.nilcord.util;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.TextParserUtils;
import eu.pb4.placeholders.api.parsers.MarkdownLiteParserV1;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class TextUtils {
    private static final Pattern ANGLE_BRACKETS = Pattern.compile("(?<!((?<!(\\\\))\\\\))<(?<id>[^<>]+)>");

    private static Component parse(String str) {
        return TextParserUtils.formatText(str);
    }

    public static Component parseDiscordMessage(String template, String attachmentChunk, Component replyChunk, String usernameChunk, User author, Member member, Message message) {
        template = template
                .replace("<attachment_format>", attachmentChunk)
                .replace("<username_format>", usernameChunk)
                .replace("<role_color>", ColorUtils.getHexColor(member))
                .replace("<message_url>", message.getJumpUrl());

        Map<String, Component> placeholders = Map.of(
                "reply_format", replyChunk,
                "username", Component.literal(author.getName()),
                "nickname", Component.literal(member.getEffectiveName()),
                "message", MarkdownLiteParserV1.ALL.parseText(message.getContentDisplay(), ParserContext.of())
        );

        return Placeholders.parseText(parse(template), ANGLE_BRACKETS, placeholders);
    }

    public static Component parseDiscordReply(String template, Message refMessage) {
        User refAuthor = refMessage.getAuthor();
        Member refMember = refMessage.getMember();

        template = template
                .replace("<reply_role_color>", refMember == null ? ColorUtils.WHITE : ColorUtils.getHexColor(refMember))
                .replace("<reply_url>", refMessage.getJumpUrl());

        Map<String, Component> placeholders = Map.of(
                "reply_username", Component.literal(refAuthor.getName()),
                "reply_nickname", Component.literal(refMember == null ? refAuthor.getEffectiveName() : refMember.getEffectiveName()),
                "reply_message", MarkdownLiteParserV1.ALL.parseText(refMessage.getContentDisplay(), ParserContext.of())
        );

        return Placeholders.parseText(parse(template), ANGLE_BRACKETS, placeholders);
    }

    public static Component parsePlayer(String template, ServerPlayer player) {
        Map<String, Component> placeholders = Map.of(
                "displayname", Objects.requireNonNullElse(player.getDisplayName(), Component.literal(player.getGameProfile().getName())),
                "username", Component.literal(player.getGameProfile().getName()),
                "uuid", Component.literal(player.getGameProfile().getId().toString())
        );

        return Placeholders.parseText(Placeholders.parseText(parse(template), ANGLE_BRACKETS, placeholders), PlaceholderContext.of(player));
    }

    public static Component parseMessage(String template, ServerPlayer player, Component message) {
        Map<String, Component> placeholders = Map.of(
                "message", message
        );

        return Placeholders.parseText(parsePlayer(template, player), ANGLE_BRACKETS, placeholders);
    }

    public static Component parseAdvancement(String template, ServerPlayer player, DisplayInfo display) {
        Map<String, Component> placeholders = Map.of(
                "advancement_title", display.getTitle(),
                "advancement_description", display.getDescription()
        );

        return Placeholders.parseText(parsePlayer(template, player), ANGLE_BRACKETS, placeholders);
    }

    public static Component parseDeath(String template, ServerPlayer player, DamageSource source) {
        Map<String, Component> placeholders = Map.of(
                "death_message", source.getLocalizedDeathMessage(player)
        );

        return Placeholders.parseText(parsePlayer(template, player), ANGLE_BRACKETS, placeholders);
    }
}
