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

public enum SpecialLifeform {
	ULTRAVORES(
		"Ultravores",
		"The ultimate apex predator, the Ultravore is capable of stalking and killing even the most intelligent and well-armed prey."),
	PHARMACEUTICALS(
		"Pharmaceuticals",
		"Plants that contain interesting chemical compounds with medical applications."),
	SHAPE_SHIFTER(
		"Shape-shifters",
		"A predatory creature able to mimic any other, even a sentient one."),
	BRAIN_PARASITE(
		"Brain parasites",
		"A parasitical creature able to interface with the brain of its host, enslaving it."),
	VAST_HERDS(
		"Vast grazing herds",
		"Untold millions of large grazing animals that provide an abundant source of food and other resources."),
	FLYING_CREATURES(
		"Beautiful flying creatures",
		"Fluttering fliers that display a dazzling array of colours."),
	OCEAN_GIANTS(
		"Ocean giants",
		"Huge sea creatures growing to more than a kilometre of length."),
	LIVING_ISLANDS(
		"Living islands",
		"Composed of the shells of millions of small crustaceans, each of these floating islands hosts its own unique ecosystem."),
	GAS_BAGS(
		"Gas bags",
		"Held aloft by sacs of hydrogen, these delicate creatures float about everywhere."),
	RADIOVORES(
		"Radiovores",
		"These small worm-like creatures derive their energy directly from exposed deposits of radioactive materials.");
	final String name;
	final String desc;

	private SpecialLifeform(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}
}
