package net.drocclusion.furnacemod.jslib;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Value;
import net.drocclusion.furnacemod.Furnace;

/**
 * Created by Sam Sartor on 8/8/2016.
 */
public abstract class FurnaceComp {
	protected FurnaceContext fc;
	protected Furnace f;
	protected V8 v8;
	protected V8Value UNDEFINED;

	public FurnaceComp(FurnaceContext fc) {
		this.fc = fc;
		f = fc.f;
		v8 = f.runtime;
		UNDEFINED = v8.getUndefined();
	}

	public abstract void release();
}
