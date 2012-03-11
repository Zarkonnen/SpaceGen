package com.zarkonnen.spacegen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

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
	
	public static BufferedImage get(SentientType st) {
		return get(st, false, null);
	}
	
	public static BufferedImage get(SentientType st, boolean eyepatch, String specialColor) {		
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
			g.drawImage(M.getImage("sentients/" + st.base.name().toLowerCase()), -7, 0, null);
			g.drawImage(M.getImage("sentients/" + st.base.name().toLowerCase()), 7, 0, null);
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
	
	static BufferedImage get(Artefact a) {
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
				return get(a.st);
			case PIRATE:
				return get(a.st, true, a.color);
			case SPACE_MONSTER:
				return M.border(M.tint(M.getImage("agents/" + a.type.name().toLowerCase()), TINTS.get(a.color)), BORDER);
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
			img2 = M.tint(img, new Color(0, 255, 0, 32));
		}
		if (p.getPollution() > 0) {
			BufferedImage pollImg = M.tint(img, new Color(111, 88, 63, 220));
			Graphics2D g = img2.createGraphics();
			int amt = Math.min(32, p.getPollution() * 3);
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
