package net.drocclusion.furnacemod.jslib;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Function;
import com.eclipsesource.v8.V8Object;
import net.drocclusion.furnacemod.FurnaceUtils;
import net.drocclusion.furnacemod.libsys.ClassBasedMembers;
import net.drocclusion.furnacemod.libsys.ObjectState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

/**
 * Created by Sam Sartor on 8/8/2016.
 */
public class Entities extends FurnaceComp {
	public ClassBasedMembers<Entity> cbm;
	public ObjectState<UUID, Entity> state = new ObjectState<>(uuid -> {
		Entity e = fc.server.getEntityFromUuid(uuid);
		if (e == null) throw new Error("Entity " + uuid + " does not exist");
		return e;
	});

	public Entities(FurnaceContext fc) {
		super(fc);
		init();
		cbm = new ClassBasedMembers<>(f, () -> {
			FurnaceUtils.PropBuilder pb = f.utils.beginObject();

			pb.newMember("entityType").getter(entityType);
			pb.newMember("getPos").value(getPos);
			pb.newMember("setPos").value(setPos);

			return pb.build();
		}, false);
	}

	public V8Object getV8(Entity e) {
		V8Object out = f.utils.createObject(cbm.getFor(e.getClass()));
		//out.add("id", e.getEntityId());
		out.add("uuid", fc.furnMCUtils.fromUUID(e.getUniqueID()));
		return out;
	}

	public Entity bind(V8Object obj) {
		return state.bind(fc.furnMCUtils.toUUID(obj.get("uuid")));
	}

	private V8Function entityType;
	private V8Function getPos;
	private V8Function setPos;
	private void init() {
		entityType = new V8Function(v8, (entjs, parameters) -> EntityList.getEntityString(bind(entjs)));
		getPos = new V8Function(v8, (entjs, parameters) -> {
			Entity e = bind(entjs);
			return fc.furnMCUtils.fromPos(e.posX, e.posY, e.posZ);
		});
		setPos = new V8Function(v8, (entjs, parameters) -> {
			double x;
			double y;
			double z;
			if (parameters.length() == 3) {
				x = parameters.getDouble(0);
				y = parameters.getDouble(1);
				z = parameters.getDouble(2);
			} else {
				Vec3d vec = fc.furnMCUtils.toPos(parameters.get(0));
				x = vec.xCoord;
				y = vec.yCoord;
				z = vec.zCoord;
			}
			Entity e = bind(entjs);
			e.setPosition(x, y, z);
			return UNDEFINED;
		});
	}

	@Override
	public void release() {
		entityType.release();
		getPos.release();
		setPos.release();
	}
}
