package net.drocclusion.furnacemod.jslib;

import com.eclipsesource.v8.Releasable;
import com.eclipsesource.v8.V8Function;
import com.eclipsesource.v8.V8Object;
import com.ibm.icu.impl.RelativeDateFormat;
import net.drocclusion.furnacemod.FurnaceUtils;
import net.drocclusion.furnacemod.libsys.ClassBasedMembers;
import net.drocclusion.furnacemod.libsys.ObjectState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Created by Sam Sartor on 8/8/2016.
 */
public class EntityContexts extends FurnaceComp {
	public ClassBasedMembers<Entity> cbm;
	private ArrayList<Releasable> connected = new ArrayList<>();
	public ObjectState<UUID, Entity> state = new ObjectState<>(uuid -> {
		Entity e = fc.server.getEntityFromUuid(uuid);
		if (e == null) throw new Error("Entity " + uuid + " does not exist");
		return e;
	});

	public EntityContexts(FurnaceContext fc) {
		super(fc);

		init();

		cbm = new ClassBasedMembers<>(f, () -> {
			FurnaceUtils.PropBuilder pb = f.utils.beginObject();
			pb.autoRelease(false);

			pb.newMember("entityType").getter(entityType);
			pb.newMember("getPos").value(getPos);
			pb.newMember("setPos").value(setPos);

			return pb.build();
		}, false);

		Entities.init(this);
	}

	public void registerMembersDirect(Class<?> clazz, Consumer<V8Object> modifier) {
		cbm.addDirectModifier(clazz, modifier);
	}

	public void registerMembers(Class<?> clazz, Consumer<FurnaceUtils.PropBuilder> modifier) {
		cbm.addPropModifier(clazz, modifier);
	}

	public void addConnectedObjects(Collection<Releasable> toRelease) {
		connected.addAll(toRelease);
	}

	public V8Object getV8(Entity e) {
		V8Object out = f.utils.createObject(cbm.getFor(e.getClass()));
		//out.add("id", e.getEntityId());
		out.add("uuid", fc.adapters.fromUUID(e.getUniqueID()));
		addRecent(e);
		return out;
	}

	public Entity bind(V8Object obj) {
		return state.bind(fc.adapters.toUUID(obj.get("uuid")));
	}

	public Entity addRecent(Entity e) {
		state.current = e;
		state.currentKey = e.getUniqueID();
		return e;
	}

	private V8Function entityType;
	private V8Function getPos;
	private V8Function setPos;
	private void init() {
		entityType = new V8Function(v8, (entjs, parameters) -> EntityList.getEntityString(bind(entjs)));

		getPos = new V8Function(v8, (entjs, parameters) -> {
			Entity e = bind(entjs);
			return fc.adapters.fromPos(e.posX, e.posY, e.posZ);
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
				Vec3d vec = fc.adapters.toPos(parameters.get(0));
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

		connected.forEach(Releasable::release);
	}
}
