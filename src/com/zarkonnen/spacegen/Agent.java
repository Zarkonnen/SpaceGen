package com.zarkonnen.spacegen;

public class Agent {
	Planet p;
	AgentType type;
	int resources;
	int fleet;
	int birth;
	String name;
	SentientType st;
	Civ originator;
	int timer = 0;
	Planet target;

	public Agent(AgentType type, int birth, String name) {
		this.type = type;
		this.birth = birth;
		this.name = name;
	}
}
