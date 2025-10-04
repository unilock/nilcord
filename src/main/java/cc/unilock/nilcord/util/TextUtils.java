package cc.unilock.nilcord.util;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.MarkdownLiteParserV1;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.ParserBuilder;
import eu.pb4.placeholders.api.parsers.TagLikeParser;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Objects;
import java.util.function.Function;

public class TextUtils {
    private static final NodeParser PARSER = ParserBuilder.of()
            .globalPlaceholders()
            .markdown()
            .quickText()
            .simplifiedTextFormat()
            .build();

    public static Text parseDiscordMessage(String template, String attachmentChunk, Text replyChunk, String usernameChunk, User author, Member member, Message message) {
        template = template
                .replace("<attachment_format>", attachmentChunk)
                .replace("<username_format>", usernameChunk)
                .replace("<role_color>", ColorUtils.getHexColor(member))
                .replace("<message_url>", message.getJumpUrl());

        Function<String, Text> placeholders = str -> switch (str) {
            case "reply_format" -> replyChunk;
            case "username" -> Text.literal(author.getName());
            case "nickname" -> Text.literal(member.getEffectiveName());
            case "message" -> MarkdownLiteParserV1.ALL.parseText(message.getContentDisplay(), ParserContext.of());
            default -> null;
        };

        return TagLikeParser.placeholderText(TagLikeParser.TAGS, placeholders).parseText(PARSER.parseNode(template), ParserContext.of());
    }

    public static Text parseDiscordReply(String template, Message refMessage) {
        User refAuthor = refMessage.getAuthor();
        Member refMember = refMessage.getMember();

        template = template
                .replace("<reply_role_color>", refMember == null ? ColorUtils.WHITE : ColorUtils.getHexColor(refMember))
                .replace("<reply_url>", refMessage.getJumpUrl());

        Function<String, Text> placeholders = str -> switch (str) {
            case "reply_username" -> Text.literal(refAuthor.getName());
            case "reply_nickname" -> Text.literal(refMember == null ? refAuthor.getEffectiveName() : refMember.getEffectiveName());
            case "reply_message" -> MarkdownLiteParserV1.ALL.parseText(refMessage.getContentDisplay(), ParserContext.of());
            default -> null;
        };

        return TagLikeParser.placeholderText(TagLikeParser.TAGS, placeholders).parseText(PARSER.parseNode(template), ParserContext.of());
    }

    public static Text parsePlayer(String template, ServerPlayerEntity player) {
        Function<String, Text> placeholders = str -> switch (str) {
            case "displayname" -> Objects.requireNonNullElse(player.getDisplayName(), Text.literal(player.getGameProfile().name()));
            case "username" -> Text.literal(player.getGameProfile().name());
            case "uuid" -> Text.literal(player.getGameProfile().id().toString());
            default -> null;
        };

        return TagLikeParser.placeholderText(TagLikeParser.TAGS, placeholders).parseText(PARSER.parseNode(template), PlaceholderContext.of(player).asParserContext());
    }

    public static Text parseMessage(String template, ServerPlayerEntity player, Text message) {
        Function<String, Text> placeholders = str -> switch (str) {
            case "message" -> message;
            default -> null;
        };

        return TagLikeParser.placeholderText(TagLikeParser.TAGS, placeholders).parseText(TextNode.convert(parsePlayer(template, player)), ParserContext.of());
    }

    public static Text parseAdvancement(String template, ServerPlayerEntity player, AdvancementDisplay display) {
        Function<String, Text> placeholders = str -> switch (str) {
            case "advancement_title" -> display.getTitle();
            case "advancement_description" -> display.getDescription();
            default -> null;
        };

        return TagLikeParser.placeholderText(TagLikeParser.TAGS, placeholders).parseText(TextNode.convert(parsePlayer(template, player)), ParserContext.of());
    }

    public static Text parseDeath(String template, ServerPlayerEntity player, DamageSource source) {
        Function<String, Text> placeholders = str -> switch (str) {
            case "death_message" -> source.getDeathMessage(player);
            default -> null;
        };

        return TagLikeParser.placeholderText(TagLikeParser.TAGS, placeholders).parseText(TextNode.convert(parsePlayer(template, player)), ParserContext.of());
    }
}
