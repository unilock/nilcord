package cc.unilock.nilcord.transformer;

import nilloader.api.lib.mini.MiniTransformer;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

// Required for Forge compatibility
@Patch.Class("org.objectweb.asm.ClassReader")
public class ClassReaderTransformer extends MiniTransformer {
	@Patch.Method("<init>([BII)V")
	public void patchInit(PatchContext ctx) {
		PatchContext.SearchResult res = ctx.search(BIPUSH(51));
		if (res.isSuccessful()) {
			res.jumpAfter();
			ctx.add(
					POP(),
					BIPUSH(52)
			);
		}
	}
}
