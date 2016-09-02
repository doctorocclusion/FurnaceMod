package net.drocclusion.furnacemod.adapters;

import com.eclipsesource.v8.V8Value;

import java.util.Map;
import java.util.function.Function;

/**
 * Created by Sam Sartor on 8/10/2016.
 */
public abstract class MappedAdapter<K, V> extends Adapter<V> {
	private Function<K, V> demap;

	public MappedAdapter(Function<K, V> demap) {
		this.demap = demap;
	}

	public MappedAdapter(Map<K, V> map) {
		this(map::get);
	}

	public abstract K getKeyFromJs(V8Value js);

	public V getJava(V8Value js) {
		return demap.apply(getKeyFromJs(js));
	}
}
