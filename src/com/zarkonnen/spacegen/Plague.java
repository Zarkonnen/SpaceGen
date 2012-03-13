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

import java.util.ArrayList;

public class Plague {
	String name;
	int lethality;
	int mutationRate;
	int transmissivity;
	int curability;
	String color;
	ArrayList<SentientType> affects = new ArrayList<SentientType>();

	public Plague(SpaceGen sg) {
		color = sg.pick(Names.COLORS);
		name =  color + " " + sg.pick(new String[] { "Rot", "Death", "Plague", "Fever", "Wasting", "Pox"});
		lethality = sg.d(9);
		mutationRate = sg.d(3);
		transmissivity = sg.d(3);
		curability = sg.d(3);
	}

	Plague(Plague plague) {
		this.name = plague.name;
		this.lethality = plague.lethality;
		this.mutationRate = plague.mutationRate;
		this.transmissivity = plague.transmissivity;
		this.curability = plague.curability;
		this.color = color;
		affects.addAll(plague.affects);
	}
	
	public String desc() {
		String desc = name + ", which affects ";
		for (int i = 0; i < affects.size(); i++) {
			if (i > 0) {
				if (i == affects.size() - 1) {
					desc += " and ";
				} else {
					desc += ", ";
				}
			}
			desc += affects.get(i).getName();
		}
		return desc;
	}
}
