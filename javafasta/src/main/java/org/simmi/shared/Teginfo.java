package org.simmi.shared;

import java.util.HashSet;
import java.util.Set;

public class Teginfo implements Teg {
	public Set<Tegeval> tset;
	public Tegeval best;

	public void add(Tegeval tv) {
		if (tset == null)
			tset = new HashSet<>();
		tset.add(tv);
		if (best == null || tv.eval < best.eval)
			best = tv;
	}

	public String toString() {
		String ret = best.toString();
		String design = best.designation;
		if( design != null ) ret += " " + design;
		int i = 0;
		for (Tegeval tv : tset) {
			if (tv != best) {
				ret += " " + tv.toString();
			}
			i++;
			if( i > 10 || ret.length() > 50 ) break;
		}
		return ret;
	}

	@Override
	public int compareTo(Object o) {
		if( o instanceof Teginfo ) {
			return best.compareTo(((Teginfo)o).best);
		} else if( o instanceof Tegeval ) {
			return -1;
		} else if( o instanceof Teg ) {
			return 1;
		}
		return 0;
	}
}