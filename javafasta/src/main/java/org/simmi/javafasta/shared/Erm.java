package org.simmi.javafasta.shared;


public class Erm implements Comparable<Erm> {
	public double d;
	public char c;

	Erm(double d, char c) {
		this.d = d;
		this.c = c;
	}

	@Override
	public int compareTo(Erm o) {
		double mis = d - o.d;

		return mis > 0 ? 1 : (mis < 0 ? -1 : 0);
	}
};