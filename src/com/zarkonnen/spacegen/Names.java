package com.zarkonnen.spacegen;

public class Names {
	static final String[] NTHS = {"Zeroth", "First", "Second", "Third", "Fourth", "Fifth", "Sixth",
	"Seventh", "Eighth", "Ninth", "Tenth", "Eleventh", "Twelfth", "Thirteenth", "Fourteenth"};
	
	static String nth(int n) {
		if (n < NTHS.length) { return NTHS[n]; }
		return n + ".";
	}
	
	static final String[] COLORS = { "Red", "Green", "Blue", "Orange", "Yellow", "Black", "White",
		"Purple", "Grey"
	};
}
