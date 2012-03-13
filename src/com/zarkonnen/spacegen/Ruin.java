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

public class Ruin implements Stratum {
	Structure structure;
	int ruinTime;
	Cataclysm cat;
	String reason;

	public Ruin(Structure structure, int ruinTime, Cataclysm cat, String reason) {
		this.structure = structure;
		this.ruinTime = ruinTime;
		this.cat = cat;
		this.reason = reason;
	}

	@Override
	public int time() { return ruinTime; }
	
	@Override
	public String toString() {
		return "The ruins of a " + structure + ", destroyed in " + ruinTime + " " +
 				(cat == null ? reason : "by a " + cat.name) + ".";
	}
}
