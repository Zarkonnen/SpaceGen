package com.zarkonnen.spacegen;

public class Population {
	SentientType type;
	int size;

	public Population(SentientType type, int size) {
		this.type = type;
		this.size = size;
	}
	
	@Override
	public String toString() {
		return size + " billion " + type.name;
	}
}
