package net.drocclusion.furnacemod.api;

import com.eclipsesource.v8.V8Object;
import net.drocclusion.furnacemod.Furnace;
import net.drocclusion.furnacemod.adapters.IAdapter;
import net.drocclusion.furnacemod.adapters.connectors.MappingConnector;
import net.minecraft.entity.Entity;

import java.util.UUID;
import java.util.function.Function;

/**
 * Created by Sam Sartor on 8/19/2016.
 */
public class EntityConnector extends MappingConnector<UUID, Entity> {
	public final Furnace furn;
	private IAdapter<UUID> uuidAdapter;

	public EntityConnector(Function<UUID, Entity> demap, Furnace furn, IAdapter<UUID> uuidAdapter) {
		super(demap);
		this.furn = furn;
		this.uuidAdapter = uuidAdapter;
	}

	@Override
	public Object getJs(Entity java) {
		return null; // TODO
	}

	@Override
	public UUID getKeyFromJs(Object js) {
		if (js instanceof V8Object) return uuidAdapter.getJava(((V8Object) js).get("uuid"));
		throw new Error("Value passed as entity is not an object");
	}
}
