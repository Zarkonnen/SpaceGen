package com.zarkonnen.spacegen;

public enum SpecialLifeform {
	ULTRAVORE(
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
