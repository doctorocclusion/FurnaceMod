package net.drocclusion.furnacemod.utils;

import com.eclipsesource.v8.*;
import net.drocclusion.furnacemod.Furnace;

/**
 * Created by Sam Sartor on 8/9/2016.
 */
public class PropBuilder {
	private FurnaceUtils utils;
	public boolean autoRelease = true;
	private V8Object obj;
	private V8Object props;
	private boolean setProto = false;
	private V8Object proto;
	private String currentName;
	private V8Object current;

	public PropBuilder(Furnace furnace, V8Object obj) {
		this(furnace);
		this.obj = obj;
	}

	public PropBuilder(Furnace furnace) {
		utils = furnace.utils;
		props = utils.createObject();
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
		current = new V8Object(utils.runtime);
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
	 *
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
			utils.defineProperties(obj, props); // <--
			if (setProto) obj.setPrototype(proto);
		} else {
			obj = utils.createObject(setProto ? proto : null, props); // <--
		}
		props.release();
		props = null;
		return obj;
	}
}
