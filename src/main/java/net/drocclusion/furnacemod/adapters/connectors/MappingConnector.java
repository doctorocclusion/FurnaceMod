package net.drocclusion.furnacemod.adapters.connectors;

import com.eclipsesource.v8.V8Value;

import java.util.Map;
import java.util.function.Function;

/**
 * Created by Sam Sartor on 8/10/2016.
 */
public abstract class MappingConnector<K, V> extends Connector<V> {
	private Function<K, V> demap;

	public MappingConnector(Function<K, V> demap) {
		this.demap = demap;
	}

	public MappingConnector(Map<K, V> map) {
		this(map::get);
	}

	public abstract K getKeyFromJs(Object js);

	public V getJava(Object js) {
		return demap.apply(getKeyFromJs(js));
	}
}
