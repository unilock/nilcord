package cc.unilock.nilcord.compat.chromaticraft;

import Reika.ChromatiCraft.API.CrystalElementAccessor;
import Reika.ChromatiCraft.API.Event.ProgressionEvent;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Magic.Progression.ResearchLevel;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import cc.unilock.nilcord.NilcordPremain;
import cc.unilock.nilcord.util.TextUtils;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import static cc.unilock.nilcord.NilcordPremain.CONFIG;
import static cc.unilock.nilcord.NilcordPremain.LOGGER;

public class ChromatiCraftCompat {
    @SubscribeEvent
    public void onProgressionEvent(ProgressionEvent ev) {
        if (ev.entityPlayer instanceof EntityPlayerMP spe) {
            String title = null;
            String desc = null; // NOTE: often somewhat spoilery, and isn't normally displayed in chat
            String template = null;

            if (ev.type == ProgressionEvent.ResearchType.COLOR) {
                CrystalElementAccessor.CrystalElementProxy color = CrystalElementAccessor.getByEnum(ev.researchKey);

                title = color.displayName();
                desc = "A new form of crystal energy";
                template = CONFIG.formatting.discord.compat.chromaticraft.color.value();
            } else if (ev.type == ProgressionEvent.ResearchType.DIMSTRUCT) {
                CrystalElementAccessor.CrystalElementProxy color = CrystalElementAccessor.getByEnum(ev.researchKey);

                title = color.displayName() + " Core";
                desc = "Another piece of the puzzle";
                template = CONFIG.formatting.discord.compat.chromaticraft.dimstruct.value();
            } else if (ev.type == ProgressionEvent.ResearchType.FRAGMENT) {
                ChromaResearch research = ChromaResearch.getByName(ev.researchKey);

                title = research.getTitle();
                desc = "Something new to investigate";
                template = CONFIG.formatting.discord.compat.chromaticraft.fragment.value();
            } else if (ev.type == ProgressionEvent.ResearchType.PROGRESS) {
                ProgressStage stage;

                try {
                    stage = ProgressStage.valueOf(ev.researchKey);

                    if (stage.getShareability() == ProgressStage.Shareability.ALWAYS || stage.getShareability() == ProgressStage.Shareability.PROXIMITY) {
                        title = stage.getTitle();
                        desc = stage.getShortDesc();
                        template = CONFIG.formatting.discord.compat.chromaticraft.progress.value();
                    } else {
                        LOGGER.trace("CC: Ignoring non-shareable ProgressStage");
                    }
                } catch (IllegalArgumentException e) {
                    LOGGER.error("CC: Not a ProgressStage? : " + ev.researchKey);
                }
            }

            if (title != null && desc != null && template != null) {
                sendMessage(spe, template, title, desc);
            }
        }
    }

    public static void onProgressionLevel(EntityPlayer player, ResearchLevel level) {
        if (player instanceof EntityPlayerMP spe) {
            String title = level.getTitle();
            String desc = level.getShortDesc();
            String template = CONFIG.formatting.discord.compat.chromaticraft.level.value();

            if (title != null && desc != null && template != null) {
                sendMessage(spe, template, title, desc);
            }
        }
    }

    private static void sendMessage(EntityPlayerMP player, String template, String title, String desc) {
        if (!template.isEmpty()) {
            String msg = TextUtils.parsePlayer(template, player)
                .replace("<progress_title>", title)
                .replace("<progress_description>", desc);

            NilcordPremain.discord.sendMessageToDiscord(msg);
        }
    }
}
