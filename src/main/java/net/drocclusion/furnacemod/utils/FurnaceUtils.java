package net.drocclusion.furnacemod.utils;

import com.eclipsesource.v8.*;
import net.drocclusion.furnacemod.Furnace;

/**
 * Created by Sam Sartor on 8/8/2016.
 */
public class FurnaceUtils {
	public final V8 runtime;
	public final Furnace furnace;
	private V8Function _defineProperty;
	private V8Function _defineProperties;
	private V8Function _createObject;
	private V8Object _objectObject;

	public FurnaceUtils(Furnace furnace) {
		this.furnace = furnace;
		runtime = furnace.v8;
		_objectObject = runtime.getObject("Object");
		_defineProperty = (V8Function) _objectObject.getObject("defineProperty");
		_defineProperties = (V8Function) _objectObject.getObject("defineProperties");
		_createObject = (V8Function) _objectObject.getObject("create");
	}

	public Object releaseThenReturn(Object toReturn, V8Value toRelease) {
		toRelease.release();
		return toReturn;
	}

	/**
	 * See <a href=https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/defineProperties>Object.defineProperties</a>
	 */
	public void defineProperties(V8Object obj, V8Object props) {
		V8Array args = new V8Array(runtime).push(obj).push(props);
		_defineProperties.call(_objectObject, args); // <--
		args.release();
	}

	/**
	 * See <a href=https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/defineProperty>Object.defineProperty</a>
	 */
	public void defineProperty(V8Object obj, String name, V8Object descriptor) {
		V8Array args = new V8Array(runtime).push(obj).push(name).push(descriptor);
		_defineProperties.call(_objectObject, args); // <--
		args.release();
	}

	/**
	 * See <a href=https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/create>Object.create</a>
	 */
	public V8Object createObject(V8Object proto, V8Object props) {
		V8Array args = new V8Array(runtime).push(proto).push(props);
		return (V8Object) releaseThenReturn(_createObject.call(_objectObject, args), args); // <--
	}

	/**
	 * See <a href=https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/create>Object.create</a>
	 */
	public V8Object createObject(V8Object proto) {
		V8Object o = new V8Object(runtime);
		o.setPrototype(proto);
		return o;
	}

	/**
	 * Same as {@code createObject(null)} or {@code new V8Object(v8)}.
	 */
	public V8Object createObject() {
		return new V8Object(runtime);
	}

	/**
	 * A nice way of using <a href=https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/defineProperties>Object.defineProperties</a>
	 */
	public PropBuilder beginProperties(V8Object obj) {
		return new PropBuilder(this.furnace, obj);
	}

	/**
	 * A nice way of using <a href=https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/create>Object.create</a>
	 */
	public PropBuilder beginObject() {
		return new PropBuilder(this.furnace);
	}

	public void release() {
		_objectObject.release();
		_createObject.release();
		_defineProperties.release();
		_defineProperty.release();
	}

	public static void safeRelease(Object o) {
		if (o instanceof V8Value) {
			((V8Value) o).release();
		}
	}
}
