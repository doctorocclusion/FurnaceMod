package net.drocclusion.furnacemod;

import com.eclipsesource.v8.JavaCallback;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;

import java.util.concurrent.TimeUnit;

/**
 * Created by Sam Sartor on 8/6/2016.
 */
public class Furnace {
	public static Furnace inst;

	public V8 runtime;
	public FurnaceUtils utils;


	public Furnace() {
		runtime = V8.createV8Runtime();
		utils = new FurnaceUtils(runtime);
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
			runtime.release();
		}
		runtime = null;
		utils = null;
	}

	public static void main(String... args) throws InterruptedException {
		Furnace f = Furnace.inst = new Furnace();

		// Get V8 warmed up
		for (int i = 0; i < 10; i++) {
			FurnaceUtils.PropBuilder ob = f.utils.beginObject();
			ob.newMember("itzover").value((receiver, parameters) -> (parameters.length() + " < 9,000!"));
			ob.newMember("foo").getter((receiver, parameters) -> "bar");
			ob.newMember("zeb").value("ra");
			ob.build().release();
			f.execute("Object.create(null, {foo: {value: \"hi\"}})", true);
		}

		long t = System.nanoTime();

		FurnaceUtils.PropBuilder ob = f.utils.beginObject();
		ob.newMember("itzover").value((receiver, parameters) -> (parameters.length() + " < 9,000!"));
		ob.newMember("foo"). getter((receiver, parameters) -> "bar");
		ob.newMember("zeb").value("ra");
		V8Object obj = ob.build();

		f.runtime.add("test", obj);
		obj.release();
		System.out.println(f.execute("test.itzover(1, 2, 3)"));
		System.out.println(f.execute("test.foo"));
		System.out.println(f.execute("test.zeb"));

		t -= System.nanoTime();

		System.out.printf("Time: %.2fms%n", -t * 1e-6);

		f.release();
	}
}
