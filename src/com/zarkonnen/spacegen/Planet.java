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
import java.util.Random;

import static com.zarkonnen.spacegen.Stage.*;
import static com.zarkonnen.spacegen.Main.*;

public class Planet {
	static final String[] P_NAMES = {
		"Taranis", "Krantor", "Mycon", "Urbon", "Metatron", "Autorog", "Pastinakos", "Orra",
		"Hylon", "Wotan", "Erebus", "Regor", "Sativex", "Vim", "Freia", "Tabernak", "Helmettepol",
		"Lumen", "Atria", "Bal", "Orgus", "Hylus", "Jurvox", "Kalamis", "Ziggurat", "Xarlan",
		"Chroma", "Nid", "Mera"
	};
	
	public final String name;
	
	private int pollution;
	boolean habitable;
	int evoPoints;
	int evoNeeded;
	ArrayList<PlanetSpecial> specials = new ArrayList<PlanetSpecial>();
	ArrayList<SpecialLifeform> lifeforms = new ArrayList<SpecialLifeform>();
	ArrayList<Population> inhabitants = new ArrayList<Population>();
	ArrayList<Artefact> artefacts = new ArrayList<Artefact>();
	private Civ owner;
	ArrayList<Structure> structures = new ArrayList<Structure>();
	ArrayList<Plague> plagues = new ArrayList<Plague>();
	
	ArrayList<Stratum> strata = new ArrayList<Stratum>();
	int x;
	int y;
	
	PlanetSprite sprite;
	
	public int getPollution() {
		return pollution;
	}

	public void setPollution(int pollution) {
		this.pollution = pollution;
		animate(change(sprite, Imager.get(this)));
	}
	
	public void addPlague(Plague p) {
		plagues.add(p);
		Sprite ps = new Sprite(Imager.get(p), (plagues.size() - 1) * 36, 36 * 4);
		sprite.plagueSprites.put(p, ps);
		animate(tracking(sprite, delay()));
		animate(add(ps, sprite));
	}
	
	public void removePlague(Plague p) {
		int sIndex = plagues.indexOf(p);
		Sprite ss = sprite.plagueSprites.get(p);
		animate(tracking(sprite, delay()));
		animate(remove(ss));
		for (int i = sIndex + 1; i < plagues.size(); i++) {
			add(move(sprite.plagueSprites.get(plagues.get(i)), (i - 1) * 36, 36 * 2));
		}
		animate();
		plagues.remove(p);
		sprite.plagueSprites.remove(p);
	}
	
	public void clearPlagues() {
		for (Plague p : plagues) {
			add(remove(sprite.plagueSprites.get(p)));
		}
		animate();
		plagues.clear();
		sprite.plagueSprites.clear();
	}
	
	public void addArtefact(Artefact a) {
		artefacts.add(a);
		Sprite as = new Sprite(Imager.get(a), (artefacts.size() - 1) * 36, 36);
		sprite.artefactSprites.put(a, as);
		animate(tracking(sprite, delay()));
		animate(add(as, sprite));
	}
	
	public void moveArtefact(Artefact a, Planet dst) {
		Sprite as = sprite.artefactSprites.get(a);
		animate(emancipate(as));
		animate(tracking(as, move(as, dst.sprite.x + dst.artefacts.size() * 36, dst.sprite.y + 36)));
		animate(subordinate(as, dst.sprite));
		dst.sprite.artefactSprites.put(a, as);
		dst.artefacts.add(a);
		int aIndex = artefacts.indexOf(a);
		for (int i = aIndex + 1; i < artefacts.size(); i++) {
			add(move(sprite.artefactSprites.get(artefacts.get(i)), (i - 1) * 36, 36));
		}
		animate();
		artefacts.remove(a);
		sprite.artefactSprites.remove(a);
	}
	
	public void removeArtefact(Artefact a) {
		int aIndex = artefacts.indexOf(a);
		Sprite as = sprite.artefactSprites.get(a);
		animate(tracking(sprite, delay()));
		animate(remove(as));
		for (int i = aIndex + 1; i < artefacts.size(); i++) {
			add(move(sprite.artefactSprites.get(artefacts.get(i)), (i - 1) * 36, 36));
		}
		animate();
		artefacts.remove(a);
		sprite.artefactSprites.remove(a);
	}
	
	public void clearArtefacts() {
		for (Artefact a : artefacts) {
			add(remove(sprite.artefactSprites.get(a)));
		}
		animate();
		artefacts.clear();
		sprite.artefactSprites.clear();
	}
	
	public void addStructure(Structure s) {
		structures.add(s);
		Sprite ss = new Sprite(Imager.get(s), (structures.size() - 1) * 36, 36 * 2);
		sprite.structureSprites.put(s, ss);
		animate(tracking(sprite, delay()));
		animate(add(ss, sprite));
	}
	
	public void removeStructure(Structure s) {
		int sIndex = structures.indexOf(s);
		Sprite ss = sprite.structureSprites.get(s);
		animate(tracking(sprite, delay()));
		animate(remove(ss));
		for (int i = sIndex + 1; i < structures.size(); i++) {
			add(move(sprite.structureSprites.get(structures.get(i)), (i - 1) * 36, 36 * 2));
		}
		animate();
		structures.remove(s);
		sprite.structureSprites.remove(s);
	}
	
	public void clearStructures() {
		for (Structure s : structures) {
			add(remove(sprite.structureSprites.get(s)));
		}
		animate();
		structures.clear();
		sprite.structureSprites.clear();
	}
	
	public void addLifeform(SpecialLifeform slf) {
		lifeforms.add(slf);
		Sprite slfs = new Sprite(Imager.get(slf), (lifeforms.size() - 1) * 36, 36 * 3);
		sprite.lifeformSprites.put(slf, slfs);
		animate(tracking(sprite, delay()));
		animate(add(slfs, sprite));
	}
	
	public void removeLifeform(SpecialLifeform slf) {
		int lfIndex = lifeforms.indexOf(slf);
		Sprite slfs = sprite.lifeformSprites.get(slf);
		animate(tracking(sprite, delay()));
		animate(remove(slfs));
		for (int i = lfIndex + 1; i < lifeforms.size(); i++) {
			add(move(sprite.lifeformSprites.get(lifeforms.get(i)), (i - 1) * 36, 36 * 3));
		}
		animate();
		lifeforms.remove(slf);
		sprite.lifeformSprites.remove(slf);
	}
	
	public void clearLifeforms() {
		for (SpecialLifeform slf : lifeforms) {
			add(remove(sprite.lifeformSprites.get(slf)));
		}
		animate();
		lifeforms.clear();
		sprite.lifeformSprites.clear();
	}
	
	public Civ getOwner() {
		return owner;
	}

	public void setOwner(Civ owner) {
		if (this.owner != null) {
			this.owner.sprites.remove(sprite.ownerSprite);
			animate(tracking(sprite, remove(sprite.ownerSprite)));
			sprite.ownerSprite = null;
		}
		this.owner = owner;
		if (owner != null) {
			sprite.ownerSprite = new CivSprite(owner, false);
			owner.sprites.add(sprite.ownerSprite);
			sprite.ownerSprite.init();
			animate(tracking(sprite, add(sprite.ownerSprite, sprite)));
		}
		animate(delay());
	}
	
	public void dePop(Population pop, int time, Cataclysm cat, String reason, Plague plague) {
		strata.add(new Remnant(pop, time, cat, reason, plague));
		pop.eliminate();
		lp: for (Plague p : new ArrayList<Plague>(plagues)) {
			for (Population p2 : inhabitants) {
				if (p.affects.contains(p2.type)) {
					continue lp;
				}
			}
			removePlague(p);
		}
	}
	
	void darkAge(int time) {
		for (Structure s : structures) {
			strata.add(new Ruin(s, time, null, "during the collapse of the " + getOwner().name));
		}
		clearStructures();
		for (Artefact a : artefacts) {
			strata.add(new LostArtefact("lost", time, a));
		}
		clearArtefacts();
		
		setOwner(null);
	}
	
	public void transcend(int time) {
		if (getOwner() == null) { return; }
		for (Population p : new ArrayList<Population>(inhabitants)) {
			strata.add(new Remnant(p, time));
			p.eliminate();
		}
		for (Structure s : structures) {
			strata.add(new Ruin(s, time, null, "after the transcendence of the " + getOwner().name));
		}
		clearStructures();
		for (Artefact a : artefacts) {
			strata.add(new LostArtefact("lost and buried when the " + getOwner().name + " transcended", time, a));
		}
		clearArtefacts();
		clearPlagues();
		setOwner(null);
	}
	
	public void deCiv(int time, Cataclysm cat, String reason) {
		if (getOwner() != null) {
			setOwner(null);
		}
		for (Population p : new ArrayList<Population>(inhabitants)) {
			dePop(p, time, cat, reason, null);
		}
		for (Structure s : structures) {
			strata.add(new Ruin(s, time, cat, reason));
		}
		clearStructures();
		for (Artefact a : artefacts) {
			strata.add(new LostArtefact("buried", time, a));
		}
		clearArtefacts();
	}
	
	public void deLive(int time, Cataclysm cat, String reason) {
		animate(tracking(sprite, delay()));
		deCiv(time, cat, reason);
		evoPoints = 0;
		for (SpecialLifeform slf : lifeforms) {
			strata.add(new Fossil(slf, time, cat));
		}
		clearPlagues();
		clearLifeforms();
		habitable = false;
		animate(tracking(sprite, change(sprite, Imager.get(this))));
	}
	
	public Planet(Random r, SpaceGen sg) {
		this.name = getName(Math.abs(r.nextInt()));
		this.evoNeeded = 15000 + (r.nextInt(3) == 0 ? 0 : 1000000);
		this.evoPoints = -evoNeeded;
		lp: while (true) {
			x = r.nextInt(7);
			y = r.nextInt(7);
			for (Planet p : sg.planets) {
				if (p.x == x && p.y == y) { continue lp; }
			}
			break;
		}
		sprite = new PlanetSprite(this);
	}
	
	public boolean has(StructureType st) {
		for (Structure s : structures) {
			if (s.type == st) { return true; }
		}
		return false;
	}
	
	static String getName(int p) {
		if (p % 2 == 0) {
			return P_NAMES[p % P_NAMES.length] + new String[] { " I", " II", " III", " IV", " V", " VI" }[(p / 77 + 3) % 6]; 
		}
		return new String(new char[] {
			(char) ('A' + (p + 5) % 7),
			new char[] {'u','e','y','o','i'}[(p + 2) % 5],
			(char) ('k' + (p / 3) % 4),
			new char[] {'u','e','i','o','a'}[(p / 2 + 1) % 5],
			(char) ('p' + (p / 2) % 9)
		}) + new String[] { " I", " II", " III", " IV", " V", " VI" }[(p / 4 + 3) % 6];
	}
	
	boolean isOutpost() {
		return	has(StructureType.Standard.MILITARY_BASE) ||
				has(StructureType.Standard.SCIENCE_LAB) ||
				has(StructureType.Standard.MINING_BASE);
	}

	int population() {
		int sum = 0;
		for (Population p : inhabitants) {
			sum += p.getSize();
		}
		return sum;
	}
	
	
	public String fullDesc(SpaceGen sg) {
		StringBuilder sb = new StringBuilder(name.toUpperCase() + "\n");
		sb.append("A ").append(habitable ? "life-bearing " : "barren ").append("planet");
		if (getOwner() != null) {
			sb.append(" of the ").append(getOwner().name);
		}
		sb.append(".\n");
		for (Agent ag : sg.agents) {
			if (ag.getLocation() == this) {
				sb.append(ag.type.describe(ag, sg)).append("\n");
			}
		}
		if (getPollution() > 0) {
			sb.append("It is ");
			switch (getPollution()) {
				case 1: sb.append("a little"); break;
				case 2: sb.append("slightly"); break;
				case 3: sb.append("somewhat"); break;
				case 4: sb.append("heavily"); break;
				case 5: sb.append("very heavily"); break;
				default: sb.append("incredibly"); break;
			}
			sb.append(" polluted.\n");
		}
		if (inhabitants.size() > 0) {
			sb.append("It is populated by:\n");
			for (Population p : inhabitants) {
				sb.append(p).append("\n");
			}
		}
		if (!plagues.isEmpty()) { sb.append("Plagues:\n"); }
		for (Plague pl : plagues) {
			sb.append(pl.desc()).append("\n");
		}
		for (Structure s : structures) {
			sb.append("A ").append(s).append("\n");
		}
		for (Artefact art : artefacts) {
			sb.append("A ").append(art).append("\n");
		}
		for (PlanetSpecial ps : specials) {
			sb.append(ps.explanation).append("\n");
		}
		if (!lifeforms.isEmpty()) {
			sb.append("Lifeforms of note:\n");
		}
		for (SpecialLifeform ps : lifeforms) {
			sb.append(ps.name).append(": ").append(ps.desc).append("\n");
		}
		if (!strata.isEmpty()) {
			sb.append("Strata:\n");
			for (int i = strata.size() - 1; i >= 0; i--) {
				sb.append(strata.get(i)).append("\n");
			}
		}
		
		return sb.toString();
	}
}
