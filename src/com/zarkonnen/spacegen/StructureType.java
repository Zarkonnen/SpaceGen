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

public interface StructureType {	
	static enum Standard implements StructureType {
		MILITARY_BASE("military base"),
		MINING_BASE("mining base"),
		SCIENCE_LAB("science lab"),
		CITY("city of spires"),
		VAULT("vast underground vault"),
		PALACE("grand palace"),
		MUSEUM("vast museum"),
		ARCOLOGY("complex of arcologies"),
		ORBITAL_ELEVATOR("orbital elevator"),
		SKULL_PILE("skull pile");

		final String name;
		
		@Override
		public String getName() { return name; }

		private Standard(String name) {
			this.name = name;
		}

		static StructureType[] COLONY_ONLY = {
			CITY, VAULT, PALACE, MUSEUM, ARCOLOGY, ORBITAL_ELEVATOR
		};
	}
	
	public String getName();
}
