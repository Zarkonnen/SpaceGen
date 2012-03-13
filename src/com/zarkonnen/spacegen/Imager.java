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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Imager {
	static final HashMap<String, Color> TINTS = new HashMap<String, Color>();
	static final ArrayList<Color> TINTS_L = new ArrayList<Color>();
	static final MediaProvider M = MediaProvider.it;
	
	static {
		TINTS.put("Red", new Color(255, 0, 0, 160));
		TINTS.put("Green", new Color(0, 255, 0, 160));
		TINTS.put("Blue", new Color(50, 50, 255, 160));
		TINTS.put("Orange", new Color(255, 127, 0, 160));
		TINTS.put("Yellow", new Color(255, 255, 0, 160));
		TINTS.put("Black", new Color(0, 0, 0, 160));
		TINTS.put("White", new Color(255, 255, 255, 220));
		TINTS.put("Purple", new Color(200, 0, 255, 160));
		TINTS.put("Grey", new Color(127, 127, 127, 160));
		TINTS_L.addAll(TINTS.values());
	}
	
	static final Color BORDER = new Color(20, 20, 20);
	
	public static BufferedImage get(Population pop) {
		return get(pop.type, false, null, pop.p.getOwner() != null && !pop.p.getOwner().fullMembers.contains(pop.type), pop.p.getOwner() == null);
	}
	
	public static BufferedImage get(SentientType st, boolean cage, boolean spear) {
		return get(st, false, null, cage, spear);
	}
	
	public static BufferedImage get(SentientType st, boolean eyepatch, String specialColor,
			boolean cage, boolean spear)
	{		
		if (st.base == SentientType.Base.PARASITES) {
			return M.border(M.getImage("sentients/parasites"), BORDER);
		}
		
		BufferedImage img = MediaProvider.it.createImage(32, 32, Transparency.BITMASK);
		Graphics2D g = img.createGraphics();
		if (st.prefixes.contains(SentientType.Prefix.FLYING)) {
			g.drawImage(M.getImage("sentients/wings"), 0, 0, null);
		}
		if (st.postfix == SentientType.Postfix.TAILS) {
			g.drawImage(M.getImage("sentients/tail"), 0, 0, null);
		}
		if (st.prefixes.contains(SentientType.Prefix.TELEPATHIC)) {
			g.drawImage(M.getImage("sentients/telepathic"), 0, 0, null);
		}
		if (st.prefixes.contains(SentientType.Prefix.AMORPHOUS)) {
			g.drawImage(M.getImage("sentients/amorphous_body"), 0, 0, null);
		} else if (st.prefixes.contains(SentientType.Prefix.SLIM)) {
			g.drawImage(M.getImage("sentients/slim_body"), 0, 0, null);
		} else {
			g.drawImage(M.getImage("sentients/body"), 0, 0, null);
		}
		if (st.prefixes.contains(SentientType.Prefix.SIX_LEGGED)) {
			g.drawImage(M.getImage("sentients/6_legs"), 0, 0, null);
		} else {
			g.drawImage(M.getImage("sentients/legs"), 0, 0, null);
		}
		if (st.prefixes.contains(SentientType.Prefix.FOUR_ARMED)) {
			g.drawImage(M.getImage("sentients/4_arms"), 0, 0, null);
		} else {
			g.drawImage(M.getImage("sentients/arms"), 0, 0, null);
		}
		if (st.prefixes.contains(SentientType.Prefix.SCALY)) {
			g.drawImage(M.getImage("sentients/scales"), 0, 0, null);
		} else if (st.prefixes.contains(SentientType.Prefix.FEATHERED)) {
			g.drawImage(M.getImage("sentients/feathers"), 0, 0, null);
		}
		if (st.prefixes.contains(SentientType.Prefix.TWO_HEADED)) {
			g.drawImage(M.getImage("sentients/" + st.base.name().toLowerCase()), -6, 0, null);
			g.drawImage(M.getImage("sentients/" + st.base.name().toLowerCase()), 6, 0, null);
		} else {
			g.drawImage(M.getImage("sentients/" + st.base.name().toLowerCase()), 0, 0, null);
		}
		if (st.postfix == SentientType.Postfix.EYES) {
			g.drawImage(M.getImage("sentients/giant_eyes"), 0, 0, null);
		}
		img = M.tint(img, new Color(255, 255, 255, 31));
		if (specialColor != null) {
			img = M.tint(img, TINTS.get(specialColor));
		} else {
			if (st.color != null) {
				img = M.tint(img, TINTS.get(st.color));
			}
		}
		if (eyepatch) {
			g = img.createGraphics();
			g.drawImage(M.getImage("agents/eyepatch"), 0, 0, null);
		}
		
		if (st.postfix == SentientType.Postfix.S_3 || st.postfix == SentientType.Postfix.S_5) {
			BufferedImage img2 = M.createImage(32, 32, Transparency.BITMASK);
			Graphics2D g2 = img2.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			int symm = st.postfix == SentientType.Postfix.S_3 ? 3 : 5;
			for (int j = 0; j < symm; j++) {
				g2.translate(16, 16);
				g2.rotate(Math.PI * 2 / symm);
				g2.translate(-16, -16);
				g2.drawImage(img, 0, 0, null);
			}
			img = img2;
		}
		
		if (spear) {
			g = img.createGraphics();
			g.drawImage(M.getImage("sentients/spear"), 0, 0, null);
		}
		
		if (cage) {
			g = img.createGraphics();
			g.drawImage(M.getImage("sentients/cage"), 0, 0, null);
		}
		
		if (st.prefixes.contains(SentientType.Prefix.GIANT)) {
			img = scale(img, 40);
		} else if (st.prefixes.contains(SentientType.Prefix.TINY)) {
			img = scale(img, 24);
		}
		return M.border(img, BORDER);
	}
	
	static BufferedImage scale(BufferedImage src, int sz) {
		BufferedImage s2 = MediaProvider.it.createImage(sz, sz, Transparency.BITMASK);
		Graphics2D g = s2.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g.scale(sz * 1.0 / src.getWidth(), sz * 1.0 / src.getHeight());
		g.drawImage(src, 0, 0, null);
		return s2;
	}
	
	static BufferedImage hologrize(BufferedImage img) {
		BufferedImage hol = M.createImage(32, 32, Transparency.BITMASK);
		BufferedImage src = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		src.createGraphics().drawImage(img, 0, 0, null);
		WritableRaster ar = src.getAlphaRaster();
		for (int y = 0; y < 32; y++) { for (int x = 0; x < 32; x++) {
			if (ar.getSample(x, y, 0) == 0) { continue; }
			Color c = new Color(src.getRGB(x, y));
			if (y % 2 == 1) {
				c = new Color(c.getRed() / 2, c.getGreen() / 2, c.getBlue() / 2, c.getAlpha());
			}
			hol.setRGB(x, y, c.getRGB());
		}}
		return M.tint(hol, new Color(100, 220, 180, 200));
	}
	
	static BufferedImage statuize(BufferedImage img) {
		Random r = new Random();
		BufferedImage stat = M.createImage(32, 32, Transparency.BITMASK);
		BufferedImage src = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		src.createGraphics().drawImage(img, 0, 0, null);
		WritableRaster ar = src.getAlphaRaster();
		for (int y = 0; y < 32; y++) { for (int x = 0; x < 32; x++) {
			if (ar.getSample(x, y, 0) == 0) { continue; }
			Color c = new Color(src.getRGB(x, y));
			int intensity = c.getRed() + c.getGreen() + c.getBlue() + r.nextInt(100) - 50;
			intensity /= 3;
			if (intensity < 0) { intensity = 0; }
			if (intensity > 255) { intensity = 255; }
			c = new Color(intensity, intensity, intensity, c.getAlpha());
			stat.setRGB(x, y, c.getRGB());
		}}
		return stat;
	}
	
	static BufferedImage getTimeIce(Artefact a) {
		BufferedImage img = scale(M.getImage("artefacts/time_ice"), 32);
		Graphics2D g = img.createGraphics(); 
		if (a.containedST != null) {
			BufferedImage port = get(a.containedST, false, false);
			g.drawImage(port, 2, 2, 28, 28, 2, 2, 28, 28, null);
		} else if (a.containedAgent != null) {
			BufferedImage port = get(a.containedAgent);
			g.drawImage(port, 2, 2, 28, 28, 2, 2, 28, 28, null);
		} else if (a.containedArtefact != null) {
			BufferedImage port = get(a.containedArtefact);
			g.drawImage(port, 2, 2, 28, 28, 2, 2, 28, 28, null);
		}
		g.drawImage(M.getImage("artefacts/time_ice_overlay", Transparency.TRANSLUCENT), 0, 0, null);
		return img;
	}
	
	static BufferedImage getArt(Artefact a) {
		ArtefactType.Art artType = (ArtefactType.Art) a.type;
		BufferedImage img;
		Graphics2D g;
		switch (artType) {
			case FILM:
				img = scale(M.getImage("artefacts/film"), 32);
				g = img.createGraphics(); 
				if (a.containedST != null) {
					BufferedImage port = get(a.containedST, false, false);
					g.drawImage(port, 8, 2, 24, 14, 8, 2, 24, 14, null);
					g.drawImage(port, 8, 16, 24, 28, 8, 2, 24, 14, null);
					return img;
				}
				if (a.containedAgent != null) {
					BufferedImage port = get(a.containedAgent);
					g.drawImage(port, 10, 2, 22, 14, 0, 0, 32, 32, null);
					g.drawImage(port, 10, 16, 22, 28, 0, 0, 32, 32, null);
					return img;
				}
				if (a.containedArtefact != null) {
					BufferedImage port = get(a.containedArtefact);
					g.drawImage(port, 10, 2, 22, 14, 0, 0, 32, 32, null);
					g.drawImage(port, 10, 16, 22, 28, 0, 0, 32, 32, null);
					return img;
				}
			case PAINTING:
				img = scale(M.getImage("artefacts/painting"), 32);
				g = img.createGraphics(); 
				if (a.containedST != null) {
					BufferedImage port = get(a.containedST, false, false);
					g.drawImage(port, 3, 5, 29, 27, 3, 0, 29, 22, null);
					return img;
				}
				if (a.containedAgent != null) {
					BufferedImage port = get(a.containedAgent);
					g.drawImage(port, 3, 5, 29, 27, 3, 0, 29, 22, null);
					return img;
				}
				if (a.containedArtefact != null) {
					BufferedImage port = get(a.containedArtefact);
					g.drawImage(port, 3, 5, 29, 27, 3, 0, 29, 22, null);
					return img;
				}
			case HYMN:
				return M.getImage("artefacts/hymn");
			case HOLOGRAM:
				if (a.containedST != null) {
					return hologrize(get(a.containedST, false, false));
				}
				if (a.containedAgent != null) {
					return hologrize(get(a.containedAgent));
				}
				if (a.containedArtefact != null) {
					return hologrize(get(a.containedArtefact));
				}
			case STATUE:
				if (a.containedST != null) {
					return statuize(get(a.containedST, false, false));
				}
				if (a.containedAgent != null) {
					return statuize(get(a.containedAgent));
				}
				if (a.containedArtefact != null) {
					return statuize(get(a.containedArtefact));
				}
		}
		return M.getImage("artefacts/" + ((ArtefactType.Art) a.type).getName().toLowerCase());
	}
	
	static BufferedImage get(Artefact a) {
		if (a.type instanceof ArtefactType.Device) {
			return M.border(M.getImage("artefacts/" + ((ArtefactType.Device) a.type).name().toLowerCase()), BORDER);
		}
		if (a.type instanceof ArtefactType.Art) {
			return M.border(getArt(a), BORDER);
		}
		
		if (a.type == ArtefactType.TIME_ICE) {
			return M.border(getTimeIce(a), BORDER);
		}
		
		if (a.type == ArtefactType.ADVENTURER_TOMB || a.type == ArtefactType.PIRATE_TOMB) {
			return M.border(M.getImage("artefacts/tomb"), BORDER);
		}
		
		if (a.type == ArtefactType.PIRATE_HOARD) {
			return M.border(M.getImage("artefacts/hoard"), BORDER);
		}
		
		if (a.type == ArtefactType.WRECK) {
			return M.border(M.getImage("artefacts/wreck"), BORDER);
		}
		
		return M.getImage("artefacts/artefact");
	}
	
	static BufferedImage get(Structure s) {
		if (s.type instanceof StructureType.Standard) {
			return M.border(M.getImage("structures/" + ((StructureType.Standard) s.type).name().toLowerCase()), BORDER);
		}
		for (SentientType.Prefix p : SentientType.Prefix.values()) {
			if (s.type == p.specialStruct) {
				return M.border(M.getImage("structures/" + p.name().toLowerCase()), BORDER);
			}
		}
		for (SentientType.Base b : SentientType.Base.values()) {
			if (s.type == b.specialStructure) {
				return M.border(M.getImage("structures/" + b.name().toLowerCase()), BORDER);
			}
		}
		
		return M.border(M.getImage("structures/building"), BORDER);
	}
	
	static BufferedImage get(Agent a) {
		switch (a.type) {
			case ADVENTURER:
				return get(a.st, false, false);
			case PIRATE:
				return get(a.st, true, a.color, false, false);
			case SPACE_MONSTER:
				return M.border(M.tint(M.getImage("agents/" + a.mType), TINTS.get(a.color)), BORDER);
			default:
				return M.border(M.getImage("agents/" + a.type.name().toLowerCase()), BORDER);
		}
	}
	
	static BufferedImage get(Plague p) {
		return M.border(M.tint(M.getImage("misc/plague"), TINTS.get(p.color)), BORDER);
	}
	
	static BufferedImage get(SpecialLifeform s) {
		switch (s) {
			case BRAIN_PARASITE:
				return M.border(M.getImage("sentients/parasites"), BORDER);
			case ULTRAVORES:
			case SHAPE_SHIFTER:
				return M.border(M.getImage("agents/" + s.name().toLowerCase()), BORDER);
			default:
				return M.border(M.getImage("lifeforms/" + s.name().toLowerCase()), BORDER);
		}
	}
	
	static BufferedImage get(Planet p) {
		BufferedImage img = null;
		if (p.specials.isEmpty()) {
			img = M.getImage("planets/planet");
		} else {
			img = M.getImage("planets/" + p.specials.get(0).name().toLowerCase());
		}
		
		BufferedImage img2 = img;
		if (p.habitable) {
			img2 = M.tint(img, new Color(0, 255, 0, 63));
		} else {
			img2 = scale(img, 32); // just copies really
		}
		if (p.getPollution() > 0) {
			BufferedImage pollImg = M.tint(img, new Color(111, 88, 63, 220));
			Graphics2D g = img2.createGraphics();
			int amt = Math.max(0, 32 - p.getPollution() * 3);
			g.drawImage(pollImg, 0, amt, 32, 32, 0, amt, 32, 32, null);
		}
		
		return scale(img2, 160);
	}
	
	static BufferedImage EXPEDITION = M.border(M.getImage("misc/ship_large"), BORDER);
	
	static BufferedImage get(Civ civ) {
		BufferedImage crest = M.createImage(32, 32, Transparency.BITMASK);
		if (civ.fullMembers.isEmpty()) { return crest; }
		Graphics2D g = crest.createGraphics();
		int sliceSz = 32 / civ.fullMembers.size();
		boolean monochrome = true;
		for (int i = 0; i < civ.fullMembers.size(); i++) {
			BufferedImage slice = M.getImage("misc/" + civ.getGovt().name().toLowerCase());
			if (civ.number > 1) {
				Graphics2D sg = slice.createGraphics();
				Draw.text(sg, "[333333]" + civ.number, 8, 6);
			}
			if (civ.fullMembers.get(i).color != null) {
				slice = M.tint(slice, TINTS.get(civ.fullMembers.get(i).color));
				monochrome = false;
			}
			g.drawImage(slice, i * sliceSz, 0, i * sliceSz + sliceSz, 32, i * sliceSz, 0, i * sliceSz + sliceSz, 32, null);
		}
		if (monochrome) {
			crest = M.tint(crest, TINTS_L.get(Math.abs(civ.name.hashCode()) % TINTS_L.size()));
		}
		return M.border(crest, BORDER);
	}
}
