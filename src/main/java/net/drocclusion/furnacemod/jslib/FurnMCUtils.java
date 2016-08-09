package net.drocclusion.furnacemod.jslib;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.V8Value;
import net.drocclusion.furnacemod.Furnace;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

/**
 * Created by Sam Sartor on 8/8/2016.
 */
public class FurnMCUtils extends FurnaceComp {
	public FurnMCUtils(FurnaceContext fc) {
		super(fc);
	}

	public V8Value fromUUID(UUID uuid) {
		V8Array out = new V8Array(v8);
		out.push((int) (uuid.getLeastSignificantBits()));
		out.push((int) (uuid.getLeastSignificantBits() >> 32));
		out.push((int) (uuid.getMostSignificantBits()));
		out.push((int) (uuid.getMostSignificantBits() >> 32));
		return out;
	}

	public UUID toUUID(Object value) {
		// TODO uuid utility funcs, like for "f81d4fae-7dec-11d0-a765-00a0c91e6bf6" sorts of things
		if (!(value instanceof V8Array)) throw new Error("UUID object is not a js array");
		V8Array arr = (V8Array) value;
		long least = arr.getInteger(0);
		least |= (long) arr.getInteger(0) << 32;
		long most = arr.getInteger(0);
		most |= (long) arr.getInteger(0) << 32;
		return new UUID(most, least);
	}

	public V8Value fromPos(double x, double y, double z) {
		V8Object o = new V8Object(v8);
		o.add("x", x);
		o.add("y", y);
		o.add("z", z);
		return o;
	}

	public Vec3d toPos(Object value) {
		double x = 0;
		double y = 0;
		double z = 0;
		if (value instanceof V8Object) {
			V8Object obj = (V8Object) value;
			x = obj.getDouble("x");
			y = obj.getDouble("y");
			z = obj.getDouble("z");
		} else if (value instanceof V8Array) {

		} else {
			throw new Error("Pos object is not a js array or js object");
		}
		return new Vec3d(x, y, z);
	}

	@Override
	public void release() {

	}
}
