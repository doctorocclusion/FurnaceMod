package net.drocclusion.furnacemod.libsys;

import java.util.function.Function;

/**
 * Created by Sam Sartor on 8/8/2016.
 */
public class ObjectState<K, T> {
	public Function<K, T> loader;
	public T current;
	public K currentKey;

	public ObjectState(Function<K, T> loader) {
		this.loader = loader;
	}

	public boolean shouldReload(T value) {
		return false;
	}

	public T bind(K key, boolean forceReload) {
		if (forceReload || !key.equals(currentKey) || shouldReload(current)) {
			current = loader.apply(key);
			currentKey = key;
		}
		return current;
	}

	public T bind(K key) {
		return bind(key, false);
	}
}
