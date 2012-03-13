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

public class Structure {
	StructureType type;
	Civ builders;
	int buildTime;
	Sprite sprite;

	public Structure(StructureType type, Civ builders, int buildTime) {
		this.type = type;
		this.builders = builders;
		this.buildTime = buildTime;
		this.sprite = new Sprite(Imager.get(this), 0, 0);
	}

	@Override
	public String toString() {
		return type.getName() + ", built by the " + builders.name + " in " + buildTime;
	}
}
