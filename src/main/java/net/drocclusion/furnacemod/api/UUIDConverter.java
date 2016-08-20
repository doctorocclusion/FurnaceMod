package net.drocclusion.furnacemod.api;

import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.V8Value;
import net.drocclusion.furnacemod.adapters.converters.Converter;

import java.util.UUID;

/**
 * Created by Sam Sartor on 8/19/2016.
 */
public class UUIDConverter extends Converter<UUID> {
	@Override
	public Object getJs(UUID java) {
		return java.toString(); // TODO long array not hex string
	}

	@Override
	public UUID getJava(Object js) {
		if (js instanceof String) {
			try {
				return UUID.fromString((String) js);
			} catch (Exception e) {
				throw new Error("Invalid UUID string", e);
			}
		}
		if (js instanceof V8Array) {
			V8Array arr = (V8Array) js;
			int len = arr.length();
			//  the array is ordered most significant -> least significant
			if (len == 2) {
				long l1 = Double.doubleToRawLongBits(arr.getDouble(0));
				long l2 = Double.doubleToRawLongBits(arr.getDouble(1));
				return new UUID(l1, l2);
			} else if (len == 4) {
				long l1 = Integer.toUnsignedLong(arr.getInteger(0)) << 32;
				l1 |= Integer.toUnsignedLong(arr.getInteger(1));
				long l2 = Integer.toUnsignedLong(arr.getInteger(2)) << 32;
				l2 |= Integer.toUnsignedLong(arr.getInteger(3));
				return new UUID(l1, l2);
			} else {
				throw new Error("Invalid UUID array");
			}
		}
		if (js instanceof V8Object) {
			V8Object obj = (V8Array) js;
			long l1;
			if (obj.contains("most")) {
				l1 = Double.doubleToRawLongBits(obj.getDouble("most"));
			} else {
				l1 = Integer.toUnsignedLong(obj.getInteger("most_high")) << 32;
				l1 |= Integer.toUnsignedLong(obj.getInteger("most_low"));
			}
			long l2;
			if (obj.contains("least")) {
				l2 = Double.doubleToRawLongBits(obj.getDouble("least"));
			} else {
				l2 = Integer.toUnsignedLong(obj.getInteger("least_high")) << 32;
				l2 |= Integer.toUnsignedLong(obj.getInteger("least_low"));
			}
			return new UUID(l1, l2);
		}
		if (js instanceof V8Value) {
			if (((V8Value) js).isUndefined()) throw new Error("UUID is undefined");
		}
		throw new Error("UUID is not array, object, or string");
	}
}
