/**
Copyright 2012 David Stark

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

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
