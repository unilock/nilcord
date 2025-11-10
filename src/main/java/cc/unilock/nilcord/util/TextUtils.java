package cc.unilock.nilcord.util;

import com.google.common.base.CharMatcher;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.TextParserUtils;
import eu.pb4.placeholders.api.parsers.MarkdownLiteParserV1;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class TextUtils {
    private static final Pattern ANGLE_BRACKETS = Pattern.compile("(?<!((?<!(\\\\))\\\\))<(?<id>[^<>]+)>");

    private static Text parse(String str) {
        return TextParserUtils.formatText(str);
    }

    public static Text parseDiscordMessage(String template, String attachmentChunk, Text replyChunk, String usernameChunk, User author, Member member, Message message) {
        template = template
                .replace("<attachment_format>", attachmentChunk)
                .replace("<username_format>", usernameChunk)
                .replace("<role_color>", member == null ? ColorUtils.WHITE : ColorUtils.getHexColor(member))
                .replace("<message_url>", message.getJumpUrl());

        Map<String, Text> placeholders = Map.of(
                "reply_format", replyChunk,
                "username", Text.literal(author.getName()),
                "nickname", Text.literal(member == null ? author.getEffectiveName() : member.getEffectiveName()),
                "message", MarkdownLiteParserV1.ALL.parseText(message.getContentDisplay(), ParserContext.of())
        );

        return Placeholders.parseText(parse(template), ANGLE_BRACKETS, placeholders);
    }

    public static Text parseDiscordReply(String template, Message refMessage) {
        User refAuthor = refMessage.getAuthor();
        Member refMember = refMessage.getMember();

        template = template
                .replace("<reply_role_color>", refMember == null ? ColorUtils.WHITE : ColorUtils.getHexColor(refMember))
                .replace("<reply_url>", refMessage.getJumpUrl());

        Map<String, Text> placeholders = Map.of(
                "reply_username", Text.literal(refAuthor.getName()),
                "reply_nickname", Text.literal(refMember == null ? refAuthor.getEffectiveName() : refMember.getEffectiveName()),
                "reply_message", MarkdownLiteParserV1.ALL.parseText(refMessage.getContentDisplay(), ParserContext.of())
        );

        return Placeholders.parseText(parse(template), ANGLE_BRACKETS, placeholders);
    }

    public static Text parsePlayer(String template, ServerPlayerEntity player) {
        Map<String, Text> placeholders = Map.of(
                "displayname", player.getDisplayName(),
                "username", Text.literal(player.getGameProfile().getName())
        );

        return Placeholders.parseText(Placeholders.parseText(parse(template), ANGLE_BRACKETS, placeholders), PlaceholderContext.of(player));
    }

    public static Text parseAvatar(String template, ServerPlayerEntity player) {
        SkinUtils.Skin skin = SkinUtils.getSkin(player.getGameProfile());

        Map<String, Text> placeholders = Map.of(
                "skin_id", Text.literal(skin.id()),
                "skin_model", Text.literal(skin.model()),
                "uuid", Text.literal(player.getGameProfile().getId().toString())
        );

        return Placeholders.parseText(parsePlayer(template, player), ANGLE_BRACKETS, placeholders);
    }

    public static Text parseMessage(String template, ServerPlayerEntity player, Text message) {
        MutableText unspoiled = MutableText.of(TextContent.EMPTY);

        message.visit((style, literal) -> {
            if (CharMatcher.is(literal.charAt(0)).matchesAllOf(literal.substring(1))) {
                HoverEvent hover = style.getHoverEvent();
                if (hover != null && HoverEvent.Action.SHOW_TEXT.equals(hover.getAction())) {
                    Text hoverText = hover.getValue(HoverEvent.Action.SHOW_TEXT);
                    if (hoverText != null) {
                        unspoiled.append("||"+hoverText.getString()+"||");
                        return Optional.empty();
                    }
                }
            }

            unspoiled.append(literal);
            return Optional.empty();
        }, Style.EMPTY);

        Map<String, Text> placeholders = Map.of(
                "message", unspoiled
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
