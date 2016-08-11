package net.drocclusion.furnacemod;

import com.eclipsesource.v8.*;
import net.drocclusion.furnacemod.components.Component;
import net.drocclusion.furnacemod.utils.FurnaceUtils;

/**
 * Created by Sam Sartor on 8/6/2016.
 */
public class Furnace {
	public V8 v8;
	public FurnaceUtils utils;
	public Component lib;

	public Furnace() {
		v8 = V8.createV8Runtime();
		utils = new FurnaceUtils(this);
		lib = new Component(this);
	}

	public void release() {
		utils.release();
		lib.releaseDown();
		v8.release();
	}
}

