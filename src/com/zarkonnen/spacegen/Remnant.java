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

public class Remnant implements Stratum {
	Population remnant;
	int collapseTime;
	Cataclysm cat;
	String reason;
	Plague plague;
	boolean transcended;

	public Remnant(Population remnant, int extinctionTime, Cataclysm cat, String reason, Plague plague) {
		this.remnant = remnant;
		this.collapseTime = extinctionTime;
		this.cat = cat;
		this.reason = reason;
		this.plague = plague;
	}
	
	public Remnant(Population remnant, int transcendenceTime) {
		this.remnant = remnant;
		this.collapseTime = transcendenceTime;
		transcended = true;
	}
	
	@Override
	public String toString() {
		if (transcended) {
			return "Remnants of a culture of " + remnant.type.getName() + " that transcended the bounds " +
					"of this universe in " + collapseTime + ".";
		}
		return "Remnants of a culture of " + remnant.type.getName() + " that collapsed " +
				(cat == null ? reason : "due to a " + cat.name) + " in " + collapseTime + "." + 
				(plague != null ? " The " + plague.desc() + ", slumbers in their corpses." : "");
	}

	@Override
	public int time() { return collapseTime; }
}
