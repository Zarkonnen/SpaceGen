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

public class Fossil implements Stratum {
	SpecialLifeform fossil;
	int fossilisationTime;
	Cataclysm cat;

	public Fossil(SpecialLifeform fossil, int fossilisationTime, Cataclysm cat) {
		this.fossil = fossil;
		this.fossilisationTime = fossilisationTime;
		this.cat = cat;
	}
	
	@Override
	public String toString() {
		return "Fossils of " + fossil.name.toLowerCase() + " that went extinct in " + fossilisationTime + 
				(cat == null ? "." : " due to a " + cat.name + ".");
	}

	@Override
	public int time() { return fossilisationTime; }
}
