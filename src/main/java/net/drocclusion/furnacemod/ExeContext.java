package net.drocclusion.furnacemod;

import com.eclipsesource.v8.V8Object;
import net.drocclusion.furnacemod.jslib.FurnaceComp;
import net.drocclusion.furnacemod.jslib.FurnaceContext;

/**
 * Created by Sam Sartor on 8/9/2016.
 */
public class ExeContext extends FurnaceComp {
	public V8Object context;

	public ExeContext(FurnaceContext fc) {
		super(fc);
		context = f.utils.createObject(fc.context);
	}

	@Override
	public void release() {
		context.release();
	}
}
