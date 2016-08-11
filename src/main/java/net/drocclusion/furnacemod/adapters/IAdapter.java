package net.drocclusion.furnacemod.adapters;

import com.eclipsesource.v8.V8Value;

/**
 * Created by Sam Sartor on 8/10/2016.
 */
public interface IAdapter<V> {
	public V8Value getJs(V java);

	public V getJava(V8Value js);
}
