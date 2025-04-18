package cc.unilock.nilcord.util;

import cc.unilock.nilcord.mixin.early.minecraft.AchievementAccessor;
import cc.unilock.nilcord.mixin.early.minecraft.StatBaseAccessor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.Achievement;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;

import java.util.regex.Pattern;

public class TextUtils {
    private static final Pattern FORMATTING_CODE_PATTERN = Pattern.compile('§' + "[0-9A-FK-OR]", Pattern.CASE_INSENSITIVE);

    public static String parseDiscordMessage(String template, String attachmentChunk, String replyChunk, String usernameChunk, User author, Member member, Message message) {
        return template
                .replace("<attachment_format>", attachmentChunk)
                .replace("<username_format>", usernameChunk)
                .replace("<role_color>", ColorUtils.getHexColor(member))
                .replace("<message_url>", message.getJumpUrl())
                .replace("<reply_format>", replyChunk)
                .replace("<username>", author.getName())
                .replace("<nickname>", member.getEffectiveName())
                .replace("<message>", message.getContentDisplay());
    }

    public static String parseDiscordReply(String template, Message refMessage) {
        User refAuthor = refMessage.getAuthor();
        Member refMember = refMessage.getMember();

        return template
                .replace("<reply_role_color>", refMember == null ? ColorUtils.WHITE : ColorUtils.getHexColor(refMember))
                .replace("<reply_url>", refMessage.getJumpUrl())
                .replace("<reply_username>", refAuthor.getName())
                .replace("<reply_nickname>", refMember == null ? refAuthor.getEffectiveName() : refMember.getEffectiveName())
                .replace("<reply_message>", refMessage.getContentDisplay());
    }

    public static String parsePlayer(String template, EntityPlayerMP player) {
        return template
                .replace("<displayname>", stripControlCodes(player.getDisplayName().getUnformattedText()))
                .replace("<username>", player.getGameProfile().getName())
                .replace("<uuid>", player.getGameProfile().getId().toString());
    }

    public static String parseMessage(String template, EntityPlayerMP player, String message) {
        return parsePlayer(template, player)
                .replace("<message>", message);
    }

    public static String parseAdvancement(String template, EntityPlayerMP player, DisplayInfo display) {
        return parsePlayer(template, player)
                .replace("<advancement_title>", display.getTitle().getUnformattedText())
                .replace("<advancement_description>", display.getDescription().getUnformattedText());
    }

    public static String parseDeath(String template, EntityPlayerMP player, DamageSource source) {
        return parsePlayer(template, player)
                .replace("<death_message>", source.getDeathMessage(player).getUnformattedText());
    }

    public static String stripControlCodes(String text) {
        return FORMATTING_CODE_PATTERN.matcher(text).replaceAll("");
    }
}
