package net.drocclusion.furnacemod.adapters;

import net.drocclusion.furnacemod.Furnace;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Sam Sartor on 8/19/2016.
 */
public class Adapters {
	public final Furnace furn;
	HashMap<String, IAdapter<?>> finishedAdapters = new HashMap<>();

	Adapters(Furnace furnace) {
		furn = furnace;
	}

	public <T> IAdapter<T> getAdapter(String s) {
		return (IAdapter<T>) finishedAdapters.get(s);
	}
}
