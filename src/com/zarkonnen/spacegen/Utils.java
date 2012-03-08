package com.zarkonnen.spacegen;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.Collections.*;

public class Utils {
	private Utils() {}

	/** @return The folder the game jar is in. */
	public static File getGameFolder() throws Exception {
		return new File(
				Utils.class.getProtectionDomain().getCodeSource().getLocation().toURI()).
				getAbsoluteFile().getParentFile();
	}

	/** @return Immutable list of items. */
	public static <T> List<T> l(T... ts) {
		return immute(Arrays.asList(ts));
	}

	/** @return Immutable list of items of a given type. */
	public static <T> List<T> typedL(Class<T> c, Object... ts) {
		return (List<T>)(List)l(ts);
	}

	/** @return Immutable list copy of collection. */
	public static <T> List<T> immute(Collection<T> l) {
		return unmodifiableList(new ArrayList<T>(l));
	}

	/** @return A pair. */
	public static <A, B> Pair<A, B> p(A a, B b) { return new Pair<A, B>(a, b); }

	/** @return Immutable hashmap from pairs. */
	public static <A, B> Map<A, B> m(Pair<A, B>... ps) {
		HashMap<A, B> m = new HashMap<A, B>(ps.length * 2);
		for (Pair<A, B> p : ps) {
			m.put(p.a, p.b);
		}
		return unmodifiableMap(m);
	}

	/** @return Joined version of the lists. */
	public static <T> List<T> join(List<T>... ls) {
		int size = 0;
		for (List<T> l : ls) { size += l.size(); }
		ArrayList<T> joint = new ArrayList<T>(size);
		for (List<T> l : ls) { joint.addAll(l); }
		return unmodifiableList(joint);
	}

	/** An immutable pair of objects. */
	public static final class Pair<A, B> {
		public final A a;
		public final B b;

		public Pair(A a, B b) { this.a = a; this.b = b; }

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Pair)) { return false; }
			final Pair<A, B> p2 = (Pair<A, B>) o;
			return
					(a == null ? p2.a == null : a.equals(p2.a)) &&
					(b == null ? p2.b == null : b.equals(p2.b));
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 83 * hash + (a != null ? a.hashCode() : 0);
			hash = 83 * hash + (b != null ? b.hashCode() : 0);
			return hash;
		}
	}

}