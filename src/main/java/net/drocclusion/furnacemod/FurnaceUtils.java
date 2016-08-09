package net.drocclusion.furnacemod;

import com.eclipsesource.v8.*;

/**
 * Created by Sam Sartor on 8/8/2016.
 */
public class FurnaceUtils {
	public class PropBuilder {
		public boolean autoRelease = true;
		private V8Object obj;
		private V8Object props;
		private boolean setProto = false;
		private V8Object proto;
		private String currentName;
		private V8Object current;

		public PropBuilder(V8Object obj) {
			this();
			this.obj = obj;
		}

		public PropBuilder() {
			props = createObject();
		}

		/**
		 * End the current descriptor.
		 */
		private void endCurrent() {
			if (current != null) {
				props.add(currentName, current);
				current.release();
				current = null;
			}
		}

		/**
		 * Should PropBuilder release the V8Values passed to value, getter, and setter (but not to setProto)? The default is true.
		 */
		public PropBuilder autoRelease(boolean shouldAutoRelease) {
			autoRelease = shouldAutoRelease;
			return this;
		}

		/**
		 * Start a new <a href=https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/defineProperty>descriptor</a>.
		 */
		public PropBuilder newMember(String name) {
			endCurrent();
			current = new V8Object(runtime);
			currentName = name;
			return this;
		}

		public PropBuilder setProto(V8Object proto) {
			setProto = true;
			this.proto = proto;
			return this;
		}

		/**
		 * true if and only if the type of this property descriptor may be changed and if the property may be deleted from the corresponding object.
		 * Defaults to false.
		 */
		public PropBuilder configurable(boolean value) {
			current.add("configurable", value);
			return this;
		}

		/**
		 * true if and only if this property shows up during enumeration of the properties on the corresponding object.
		 * Defaults to false.
		 */
		public PropBuilder enumerable(boolean value) {
			current.add("enumerable", value);
			return this;
		}

		/**
		 * Sets configurable, enumerable, and writable to true. Same as if the member was added by assignment.
		 * @return
		 */
		public PropBuilder standard() {
			configurable(true);
			enumerable(true);
			writable(true);
			return this;
		}

		/**
		 * The value associated with the property. Can be any valid JavaScript value (number, object, function, etc).
		 */
		public PropBuilder value(int value) {
			current.add("value", value);
			return this;
		}

		/**
		 * The value associated with the property. Can be any valid JavaScript value (number, object, function, etc).
		 */
		public PropBuilder value(double value) {
			current.add("value", value);
			return this;
		}

		/**
		 * The value associated with the property. Can be any valid JavaScript value (number, object, function, etc).
		 */
		public PropBuilder value(boolean value) {
			current.add("value", value);
			return this;
		}

		/**
		 * The value associated with the property. Can be any valid JavaScript value (number, object, function, etc).
		 */
		public PropBuilder value(String value) {
			current.add("value", value);
			return this;
		}

		/**
		 * The value associated with the property. Can be any valid JavaScript value (number, object, function, etc).
		 * This function will call release on {@code value} if autoRelease is true;
		 */
		public PropBuilder value(V8Value value) {
			current.add("value", value);
			if (autoRelease) value.release();
			return this;
		}

		/**
		 * The value associated with the property. Can be any valid JavaScript value (number, object, function, etc).
		 */
		public PropBuilder value(JavaCallback value) {
			current.registerJavaMethod(value, "value");
			return this;
		}

		/**
		 * The value associated with the property. Can be any valid JavaScript value (number, object, function, etc).
		 */
		public PropBuilder value(JavaVoidCallback value) {
			current.registerJavaMethod(value, "value");
			return this;
		}

		/**
		 * true if and only if the value associated with the property may be changed with an assignment operator.
		 * Defaults to false.
		 */
		public PropBuilder writable(boolean value) {
			current.add("writable", value);
			return this;
		}

		/**
		 * A function which serves as a getter for the property, or undefined if there is no getter. The function return will be used as the value of property.
		 * Defaults to undefined.
		 * This function will call release on {@code getter} if autoRelease is true;
		 */
		public PropBuilder getter(V8Function getter) {
			current.add("get", getter);
			if (autoRelease) getter.release();
			return this;
		}

		/**
		 * A function which serves as a getter for the property, or undefined if there is no getter. The function return will be used as the value of property.
		 * Defaults to undefined.
		 */
		public PropBuilder getter(JavaCallback getter) {
			current.registerJavaMethod(getter, "get");
			return this;
		}

		/**
		 * A function which serves as a setter for the property, or undefined if there is no setter. The function will receive as only argument the new value being assigned to the property.
		 * Defaults to undefined.
		 * This function will call release on {@code setter} if autoRelease is true;
		 */
		public PropBuilder setter(V8Function setter) {
			current.add("set", setter);
			if (autoRelease) setter.release();
			return this;
		}

		/**
		 * A function which serves as a setter for the property, or undefined if there is no setter. The function will receive as only argument the new value being assigned to the property.
		 * Defaults to undefined.
		 */
		public PropBuilder setter(JavaVoidCallback setter) {
			current.registerJavaMethod(setter, "set");
			return this;
		}

		public V8Object build() {
			endCurrent();
			if (obj != null) {
				defineProperties(obj, props); // <--
				if (setProto) obj.setPrototype(proto);
			} else {
				obj = createObject(setProto ? proto : null, props); // <--
			}
			props.release();
			props = null;
			return obj;
		}
	}

	public final V8 runtime;
	private V8Function _defineProperty;
	private V8Function _defineProperties;
	private V8Function _createObject;
	private V8Object _objectObject;

	public FurnaceUtils(V8 runtime) {
		this.runtime = runtime;
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
	 * Same as {@code createObject(null)} or {@code new V8Object(runtime)}.
	 */
	public V8Object createObject() {
		return new V8Object(runtime);
	}

	/**
	 * A nice way of using <a href=https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/defineProperties>Object.defineProperties</a>
	 */
	public PropBuilder beginProperties(V8Object obj) {
		return new PropBuilder(obj);
	}

	/**
	 * A nice way of using <a href=https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/create>Object.create</a>
	 */
	public PropBuilder beginObject() {
		return new PropBuilder();
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
