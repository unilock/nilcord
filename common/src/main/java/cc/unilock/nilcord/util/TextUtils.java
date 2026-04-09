package cc.unilock.nilcord.util;

import com.google.common.base.CharMatcher;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.MarkdownLiteParserV1;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.ParserBuilder;
import eu.pb4.placeholders.api.parsers.TagLikeParser;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class TextUtils {
    private static final NodeParser PARSER = ParserBuilder.of()
            .serverPlaceholders()
            .markdown()
            .quickText()
            .simplifiedTextFormat()
            .build();

    public static Component parseDiscordMessage(String template, String attachmentChunk, Component replyChunk, String usernameChunk, User author, Member member, Message message) {
        template = template
                .replace("<attachment_format>", attachmentChunk)
                .replace("<username_format>", usernameChunk)
                .replace("<role_color>", member == null ? ColorUtils.WHITE : ColorUtils.getHexColor(member))
                .replace("<message_url>", message.getJumpUrl());

        Function<String, Component> placeholders = str -> switch (str) {
            case "reply_format" -> replyChunk;
            case "username" -> Component.literal(author.getName());
            case "nickname" -> Component.literal(member == null ? author.getEffectiveName() : member.getEffectiveName());
            case "message" -> MarkdownLiteParserV1.ALL.parseComponent(message.getContentDisplay(), ParserContext.of());
            default -> null;
        };

        return TagLikeParser.placeholderText(TagLikeParser.TAGS, placeholders).parseComponent(PARSER.parseNode(template), ParserContext.of());
    }

    public static Component parseDiscordReply(String template, Message refMessage) {
        User refAuthor = refMessage.getAuthor();
        Member refMember = refMessage.getMember();

        template = template
                .replace("<reply_role_color>", refMember == null ? ColorUtils.WHITE : ColorUtils.getHexColor(refMember))
                .replace("<reply_url>", refMessage.getJumpUrl());

        Function<String, Component> placeholders = str -> switch (str) {
            case "reply_username" -> Component.literal(refAuthor.getName());
            case "reply_nickname" -> Component.literal(refMember == null ? refAuthor.getEffectiveName() : refMember.getEffectiveName());
            case "reply_message" -> MarkdownLiteParserV1.ALL.parseComponent(refMessage.getContentDisplay(), ParserContext.of());
            default -> null;
        };

        return TagLikeParser.placeholderText(TagLikeParser.TAGS, placeholders).parseComponent(PARSER.parseNode(template), ParserContext.of());
    }

    public static Component parsePlayer(String template, ServerPlayer player) {
        Function<String, Component> placeholders = str -> switch (str) {
            case "displayname" -> Objects.requireNonNullElse(player.getDisplayName(), Component.literal(player.getGameProfile().name()));
            case "username" -> Component.literal(player.getGameProfile().name());
            case "uuid" -> Component.literal(player.getGameProfile().id().toString());
            default -> null;
        };

        return TagLikeParser.placeholderText(TagLikeParser.TAGS, placeholders).parseComponent(PARSER.parseNode(template), ParserContext.of());
    }

    public static Component parseAvatar(String template, ServerPlayer player) {
        SkinUtils.Skin skin = SkinUtils.getSkin(player.getGameProfile());

        Function<String, Component> placeholders = str -> switch (str) {
                case "skin_id" -> Component.literal(skin.id());
                case "skin_model" -> Component.literal(skin.model());
                case "uuid" -> Component.literal(player.getGameProfile().id().toString());
                default -> null;
        };

        return TagLikeParser.placeholderText(TagLikeParser.TAGS, placeholders).parseComponent(TextNode.convert(parsePlayer(template, player)), ParserContext.of());
    }

    public static Component parseMessage(String template, ServerPlayer player, Component message) {
        MutableComponent unspoiled = MutableComponent.create(PlainTextContents.EMPTY);

        message.visit((style, literal) -> {
            if (CharMatcher.is(literal.charAt(0)).matchesAllOf(literal.substring(1))) {
                if (style.getHoverEvent() instanceof HoverEvent.ShowText(Component hoverComponent)) {
                    unspoiled.append("||" + hoverComponent.getString() + "||");
                    return Optional.empty();
                }
            }

            unspoiled.append(literal);
            return Optional.empty();
        }, Style.EMPTY);

        Function<String, Component> placeholders = str -> switch (str) {
            case "message" -> unspoiled;
            default -> null;
        };

        return TagLikeParser.placeholderText(TagLikeParser.TAGS, placeholders).parseComponent(TextNode.convert(parsePlayer(template, player)), ParserContext.of());
    }

    public static Component parseAdvancement(String template, ServerPlayer player, DisplayInfo display) {
        Function<String, Component> placeholders = str -> switch (str) {
            case "advancement_title" -> display.getTitle();
            case "advancement_description" -> display.getDescription();
            default -> null;
        };

        return TagLikeParser.placeholderText(TagLikeParser.TAGS, placeholders).parseComponent(TextNode.convert(parsePlayer(template, player)), ParserContext.of());
    }

    public static Component parseDeath(String template, ServerPlayer player, DamageSource source) {
        Function<String, Component> placeholders = str -> switch (str) {
            case "death_message" -> source.getLocalizedDeathMessage(player);
            default -> null;
        };

        return TagLikeParser.placeholderText(TagLikeParser.TAGS, placeholders).parseComponent(TextNode.convert(parsePlayer(template, player)), ParserContext.of());
    }
}
