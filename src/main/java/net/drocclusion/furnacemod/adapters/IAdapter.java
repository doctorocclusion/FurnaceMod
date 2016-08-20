package net.drocclusion.furnacemod.adapters;

/**
 * Created by Sam Sartor on 8/10/2016.
 */
public interface IAdapter<V> {
	public Object getJs(V java);

	public V getJava(Object js);
}
