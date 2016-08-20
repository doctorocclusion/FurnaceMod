package net.drocclusion.furnacemod.adapters;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import net.drocclusion.furnacemod.Furnace;
import net.minecraft.util.Tuple;
import scala.collection.mutable.MultiMap;

import java.util.*;
import java.util.function.Function;

/**
 * Created by Sam Sartor on 8/19/2016.
 */
public class AdapterManager {
	private static class Node {
		public String id;
		public Function<Adapters, IAdapter<?>> supplier;
		public Set<String> depends;
		public int num;

		@Override
		public int hashCode() {
			return id.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			return ((Node) o).id.equals(id);
		}
	}

	private static boolean changed = false;

	private static HashMap<String, Node> suppliers = new HashMap<>();

	private static LinkedList<Node> sorted = new LinkedList<>();

	/**
	 * Set a supplier for an adapter by id.
	 * @param id The name for the adapter, i.e. "minecraft.entity"
	 * @param supplier A function that outputs a Connector, Converter, or other object implementing IAdapter
	 * @param depends The ids of adapters required by this adapter
	 */
	public synchronized static void addAdapter(String id, Function<Adapters, IAdapter<?>> supplier, String... depends) {
		changed = true;
		Node n = new Node();
		n.id = id;
		n.supplier = supplier;
		n.depends = Sets.newHashSet(depends);
		suppliers.put(id, n);
	}

	/**
	 * Resolve adapter dependencies
	 */
	public synchronized static void sort() {
		sorted.clear();

		LinkedList<Node> noIncoming = new LinkedList<>();
		Multimap<String, Node> edges = MultimapBuilder.hashKeys().linkedListValues().build();

		for (Node n : suppliers.values()) {
			n.num = n.depends.size();
			if (n.depends.isEmpty()) noIncoming.add(n);
			else for (String i : n.depends) {
					edges.put(i, n);
				}
		}

		// using https://en.wikipedia.org/wiki/Topological_sorting#Kahn.27s_algorithm

		while (!noIncoming.isEmpty()) {
			Node c = noIncoming.poll();
			sorted.push(c);

			for (Node n : edges.removeAll(c.id)) {
				n.num--;
				if (n.num == 0) {
					noIncoming.add(n);
				}
			}
		}

		if (!edges.isEmpty()) throw new IllegalStateException("Can not resolve adapter dependencies");

		changed = false;
	}

	/**
	 * Create usable list of adapters
	 */
	public synchronized static Adapters build(Furnace furnace) {
		if (changed) sort();
		Adapters a = new Adapters(furnace);
		if (furnace.adapters == null) furnace.adapters = a;
		for (Node n : sorted) {
			try {
				a.finishedAdapters.put(n.id, n.supplier.apply(a));
			} catch (Exception e) {
				System.err.printf("Could not create adapter %s, cause:%n", n.id);
				e.printStackTrace();
			}
		}
		return a;
	}
}
