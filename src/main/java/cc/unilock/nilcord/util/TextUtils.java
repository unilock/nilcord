package cc.unilock.nilcord.util;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.minecraft.entity.player.EntityServerPlayer;
import net.minecraft.stats.Achievement;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Translate;

public class TextUtils {
    public static String parseDiscordMessage(String template, String attachmentChunk, String replyChunk, String usernameChunk, User author, Member member, Message message) {
        return template
                .replace("<attachment_format>", attachmentChunk)
                .replace("<username_format>", usernameChunk)
                .replace("<role_color>", member == null ? ColorUtils.WHITE : ColorUtils.getHexColor(member))
                .replace("<message_url>", message.getJumpUrl())
                .replace("<reply_format>", replyChunk)
                .replace("<username>", author.getName())
                .replace("<nickname>", member == null ? author.getEffectiveName() : member.getEffectiveName())
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

    public static String parsePlayer(String template, EntityServerPlayer player) {
        return template
                .replace("<username>", player.username);
    }

	public static String parseAvatar(String template, EntityServerPlayer player) {
		return parsePlayer(template, player);
	}

    public static String parseMessage(String template, EntityServerPlayer player, String message) {
        return parsePlayer(template, player)
                .replace("<message>", message);
    }

    public static String parseAchievement(String template, EntityServerPlayer player, Achievement achievement) {
        return parsePlayer(template, player)
                .replace("<achievement_title>", Translate.format(achievement.statName))
                .replace("<achievement_description>",
                        achievement.statName.equals("achievement.openInventory")
                                ? Translate.format(achievement.achievementDescription).replace("%1$s", "E")
                                : Translate.format(achievement.achievementDescription)
                );
    }

    public static String parseDeath(String template, EntityServerPlayer player, DamageSource source) {
        return parsePlayer(template, player)
                .replace("<death_message>", Translate.format(source.getDeathMessage(player)));
    }
}
