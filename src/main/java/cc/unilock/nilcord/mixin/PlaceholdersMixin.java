package cc.unilock.nilcord.mixin;

import eu.pb4.placeholders.api.PlaceholderHandler;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.parent.ParentNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.impl.placeholder.NodePlaceholderParserImpl;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Map;
import java.util.regex.Pattern;

@Mixin(value = Placeholders.class, remap = false)
public class PlaceholdersMixin {
    /**
     * @author unilock
     * @reason backport from 1.19.3
     */
    @Overwrite
    public static ParentNode parseNodes(TextNode node, Pattern pattern, Map<String, Text> placeholders) {
        return new ParentNode(NodePlaceholderParserImpl.recursivePlaceholderParsing(node, pattern, new Placeholders.PlaceholderGetter() {
            @Override
            public PlaceholderHandler getPlaceholder(String placeholder) {
                var x = placeholders.get(placeholder);
                return x != null ? (ctx, arg) -> PlaceholderResult.value(x) : null;
            }

            @Override
            public boolean isContextOptional() {
                return true;
            }
        }, NodeParser.NOOP));
    }
}
