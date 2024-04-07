package cc.unilock.nilcord.compat.chromaticraft;

import Reika.ChromatiCraft.API.CrystalElementAccessor;
import Reika.ChromatiCraft.API.Event.ProgressionEvent;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Magic.Progression.ResearchLevel;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

import static cc.unilock.nilcord.NilcordPremain.LOGGER;

public class ChromatiCraftCompat {
    // id : username : displayname : type : title : description
    private static final String CC_PROGRESS_FORMAT = "%s␞%s␟%s␟%s␟%s␟%s";

    private static final ResourceLocation CC_PROGRESS = new ResourceLocation("cc", "progress");

    private static final String CC_TYPE_COLOR = "COLOR";
    private static final String CC_TYPE_DIMSTRUCT = "DIMSTRUCT";
    private static final String CC_TYPE_FRAGMENT = "FRAGMENT";
    private static final String CC_TYPE_PROGRESS = "PROGRESS";
    private static final String CC_TYPE_LEVEL = "LEVEL";

    @SubscribeEvent
    public void onProgressionEvent(ProgressionEvent ev) {
        if (ev.entityPlayer instanceof EntityPlayerMP spe) {
            String title = null;
            String desc = null; // NOTE: often somewhat spoilery, and isn't normally displayed in chat
            String type = null;

            if (ev.type == ProgressionEvent.ResearchType.COLOR) {
                CrystalElementAccessor.CrystalElementProxy color = CrystalElementAccessor.getByEnum(ev.researchKey);

                title = color.displayName();
                desc = "A new form of crystal energy";
                type = CC_TYPE_COLOR;
            } else if (ev.type == ProgressionEvent.ResearchType.DIMSTRUCT) {
                CrystalElementAccessor.CrystalElementProxy color = CrystalElementAccessor.getByEnum(ev.researchKey);

                title = color.displayName() + " Core";
                desc = "Another piece of the puzzle";
                type = CC_TYPE_DIMSTRUCT;
            } else if (ev.type == ProgressionEvent.ResearchType.FRAGMENT) {
                ChromaResearch research = ChromaResearch.getByName(ev.researchKey);

                title = research.getTitle();
                desc = "Something new to investigate";
                type = CC_TYPE_FRAGMENT;
            } else if (ev.type == ProgressionEvent.ResearchType.PROGRESS) {
                ProgressStage stage;

                try {
                    stage = ProgressStage.valueOf(ev.researchKey);

                    if (stage.getShareability() == ProgressStage.Shareability.ALWAYS || stage.getShareability() == ProgressStage.Shareability.PROXIMITY) {
                        title = stage.getTitle();
                        desc = stage.getShortDesc();
                        type = CC_TYPE_PROGRESS;
                    } else {
                        LOGGER.trace("CC: Ignoring non-shareable ProgressStage");
                    }
                } catch (IllegalArgumentException e) {
                    LOGGER.error("CC: Not a ProgressStage? : " + ev.researchKey);
                }
            }

            if (title != null && desc != null) {
                sendMessage(spe, type, title, desc);
            }
        }
    }

    public static void onProgressionLevel(EntityPlayer player, ResearchLevel level) {
        if (player instanceof EntityPlayerMP spe) {
            String title = level.getTitle();
            String desc = level.getShortDesc();
            String type = CC_TYPE_LEVEL;

            if (title != null && desc != null) {
                sendMessage(spe, type, title, desc);
            }
        }
    }

    private static void sendMessage(EntityPlayerMP player, String type, String title, String desc) {
        String msg = String.format(CC_PROGRESS_FORMAT, CC_PROGRESS, player.getCommandSenderName(), player.getDisplayName(), type, title, desc);
        //PacketSender.sendMessage(player, msg);
    }
}
