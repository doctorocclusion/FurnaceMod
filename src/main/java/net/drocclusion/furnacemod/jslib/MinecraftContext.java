package net.drocclusion.furnacemod.jslib;

import com.eclipsesource.v8.V8Object;
import net.drocclusion.furnacemod.FurnaceUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.List;

/**
 * Created by Sam Sartor on 8/9/2016.
 */
public class MinecraftContext extends FurnaceComp {
	public MinecraftContext(FurnaceContext fc) {
		super(fc);

		FurnaceUtils.PropBuilder pb = f.utils.beginObject();
		init(pb);
		V8Object obj = pb.build();
		fc.context.add("mc", obj);
		obj.release();
	}

	private void init(FurnaceUtils.PropBuilder mc) {
		mc.newMember("players").getter((receiver, parameters) -> {
			List<EntityPlayerMP> players = f.ms.getPlayerList().getPlayerList();
			V8Object list = new V8Object(v8);
			for (EntityPlayerMP ep : players) {
				String username = ep.getGameProfile().getName();
				V8Object ent = fc.entities.getV8(ep);
				list.add(username, ent);
				ent.release();
			}
			return list;
		});
	}

	@Override
	public void release() {

	}
}
