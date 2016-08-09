package net.drocclusion.furnacemod;

import com.eclipsesource.v8.*;
import net.drocclusion.furnacemod.jslib.FurnaceContext;
import net.minecraft.server.MinecraftServer;

import java.util.concurrent.TimeUnit;

/**
 * Created by Sam Sartor on 8/6/2016.
 */
public class Furnace {
	public static Furnace inst;

	public V8 runtime;
	public FurnaceUtils utils;
	public FurnaceContext context;
	public MinecraftServer ms;

	public Furnace(MinecraftServer ms) {
		this.ms = ms;
		runtime = V8.createV8Runtime();
		utils = new FurnaceUtils(runtime);
		context = new FurnaceContext(this);
		/* // Needs to be more defensive
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				safeRelease();
			}
		});
		*/
	}

	public Object execute(String script, boolean ignoreOutput) {
		if (runtime == null) return null;
		Object out = runtime.executeScript(script);
		if (ignoreOutput) {
			FurnaceUtils.safeRelease(out);
			return null;
		}
		return out;
	}

	public Object execute(String script) {
		return execute(script, false);
	}

	public void release() {
		if (runtime != null) {
			utils.release();
			context.release();
			runtime.release();
		}
		context = null;
		runtime = null;
		utils = null;
	}
}
