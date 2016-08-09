package net.drocclusion.furnacemod.libsys;

import com.eclipsesource.v8.JavaCallback;
import com.eclipsesource.v8.V8Object;
import net.drocclusion.furnacemod.Furnace;
import net.drocclusion.furnacemod.FurnaceUtils;

import java.util.HashMap;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by Sam Sartor on 8/8/2016.
 */
public class ClassBasedMembers<T> {
	private Supplier<V8Object> baseObjectSupplier;
	public HashMap<Class<?>, Consumer<V8Object>> modifiers = new HashMap<>();
	private HashMap<Class<?>, V8Object> clazzes = new HashMap<>();
	private boolean traverseInterfaces;
	private Furnace furnace;

	public ClassBasedMembers(Furnace furnace, Supplier<V8Object> baseObjectSupplier, boolean traverseInterfaces) {
		this.furnace = furnace;
		this.baseObjectSupplier = baseObjectSupplier;
		this.traverseInterfaces = traverseInterfaces;
	}

	public void addDirectModifier(Class<?> clazz, Consumer<V8Object> modifier) {
		modifiers.put(clazz, modifier);
	}

	public void addPropModifier(Class<?> clazz, Consumer<FurnaceUtils.PropBuilder> modifier) {
		modifiers.put(clazz, obj -> {
			FurnaceUtils.PropBuilder pb = furnace.utils.beginProperties(obj);
			modifier.accept(pb);
			pb.build();
		});
	}

	public boolean hasCreated(Class<?> clazz) {
		return clazzes.containsKey(clazz);
	}

	public V8Object getFor(Class<?> clazz) {
		V8Object out = clazzes.get(clazz);
		if (out != null) return out;
		out = baseObjectSupplier.get();
		build(out, clazz);
		return out;
	}

	private void build(V8Object obj, Class<?> clazz) {
		Consumer<V8Object> mod = modifiers.get(clazz);
		if (mod != null) mod.accept(obj);
		Class<?> up = clazz.getSuperclass();
		if (up != null) build(obj, up);
		if (traverseInterfaces) {
			for (Class<?> upi : clazz.getInterfaces()) {
				build(obj, upi);
			}
		}
	}

	public void release() {
		clazzes.values().forEach(V8Object::release);
	}
}
