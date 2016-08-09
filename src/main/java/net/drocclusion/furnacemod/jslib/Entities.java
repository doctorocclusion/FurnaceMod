package net.drocclusion.furnacemod.jslib;

import com.eclipsesource.v8.Releasable;
import com.eclipsesource.v8.V8Function;
import net.minecraft.entity.monster.EntityCreeper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Sam Sartor on 8/9/2016.
 */
public class Entities {
	public static void init(EntityContexts ec) {
		ArrayList<Releasable> funcs = new ArrayList<>();

		// Creeper
		try {
			V8Function explode;
			Method explodeReflect = EntityCreeper.class.getMethod("explode");
			explodeReflect.setAccessible(true);
			funcs.add(explode = new V8Function(ec.v8, (receiver, parameters) -> {
				try { explodeReflect.invoke(ec.bind(receiver)); } catch (Exception e) { throw new Error(e); }
				return ec.UNDEFINED;
			}));

			V8Function ignite;
			funcs.add(ignite = new V8Function(ec.v8, (receiver, parameters) -> {
				((EntityCreeper) ec.bind(receiver)).ignite();
				return ec.UNDEFINED;
			}));

			ec.registerMembers(EntityCreeper.class, pb -> {
				pb.newMember("explode").value(explode);
				pb.newMember("ignite").value(ignite);
			});
		} catch (Exception e) { e.printStackTrace(); }

		ec.addConnectedObjects(funcs);
	}
}
