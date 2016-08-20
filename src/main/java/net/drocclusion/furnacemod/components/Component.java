package net.drocclusion.furnacemod.components;

import com.eclipsesource.v8.V8Object;
import net.drocclusion.furnacemod.Furnace;

import java.util.HashMap;

// TODO javadocs
public class Component implements IPlainReleasable {
	protected HashMap<String,Component> children = new HashMap<>();
	public V8Object object;

	public Component(Furnace furnace) {
		object = new V8Object(furnace.v8);
	}

	public Component addMember(String name, Component member) {
		children.put(name, member);
		object.add(name, member.object);
		return this;
	}

	public void addAsMember(String name, Component to) {
		to.addMember(name, this);
	}

	public Component removeMember(String name) {
		Component removed  = children.remove(name);
		if (removed != null) {
			object.addUndefined(name);
		}
		return removed;
	}

	public Component getMember(String name) {
		return children.get(name);
	}

	public void releaseDown() {
		if (object != null) {
			object.release();
			children.values().forEach(Component::releaseDown);
		}
	}

	@Override
	public void release() {
		releaseDown();
	}
}
