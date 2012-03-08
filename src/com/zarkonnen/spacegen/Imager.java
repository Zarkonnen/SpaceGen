package com.zarkonnen.spacegen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Imager {
	static final HashMap<String, Color> TINTS = new HashMap<String, Color>();
	
	static {
		TINTS.put("Red", new Color(255, 0, 0, 127));
		TINTS.put("Green", new Color(0, 255, 0, 127));
		TINTS.put("Blue", new Color(50, 50, 255, 127));
		TINTS.put("Orange", new Color(255, 127, 0, 127));
		TINTS.put("Yellow", new Color(255, 255, 0, 127));
		TINTS.put("Black", new Color(0, 0, 0, 127));
		TINTS.put("White", new Color(255, 255, 255, 127));
		TINTS.put("Purple", new Color(255, 0, 255, 127));
		TINTS.put("Grey", new Color(127, 127, 127, 127));
	}
	
	public static BufferedImage getImg(SentientType st) {
		BufferedImage img = MediaProvider.it.createImage(32, 32, Transparency.BITMASK);
		MediaProvider m = MediaProvider.it;
		Graphics2D g = img.createGraphics();
		if (st.prefixes.contains(SentientType.Prefix.FLYING)) {
			g.drawImage(m.getImage("sentients/wings"), 0, 0, null);
		}
		if (st.postfix == SentientType.Postfix.TAILS) {
			g.drawImage(m.getImage("sentients/tail"), 0, 0, null);
		}
		if (st.prefixes.contains(SentientType.Prefix.TELEPATHIC)) {
			g.drawImage(m.getImage("sentients/telepathic"), 0, 0, null);
		}
		if (st.prefixes.contains(SentientType.Prefix.AMORPHOUS)) {
			g.drawImage(m.getImage("sentients/amorphous_body"), 0, 0, null);
		} else if (st.prefixes.contains(SentientType.Prefix.SLIM)) {
			g.drawImage(m.getImage("sentients/slim_body"), 0, 0, null);
		} else {
			g.drawImage(m.getImage("sentients/body"), 0, 0, null);
		}
		if (st.prefixes.contains(SentientType.Prefix.SIX_LEGGED)) {
			g.drawImage(m.getImage("sentients/6_legs"), 0, 0, null);
		} else {
			g.drawImage(m.getImage("sentients/legs"), 0, 0, null);
		}
		if (st.prefixes.contains(SentientType.Prefix.FOUR_ARMED)) {
			g.drawImage(m.getImage("sentients/4_arms"), 0, 0, null);
		} else {
			g.drawImage(m.getImage("sentients/arms"), 0, 0, null);
		}
		if (st.prefixes.contains(SentientType.Prefix.SCALY)) {
			g.drawImage(m.getImage("sentients/scales"), 0, 0, null);
		} else if (st.prefixes.contains(SentientType.Prefix.FEATHERED)) {
			g.drawImage(m.getImage("sentients/feathers"), 0, 0, null);
		}
		g.drawImage(m.getImage("sentients/humanoid"), 0, 0, null); // qqDPS
		if (st.postfix == SentientType.Postfix.EYES) {
			g.drawImage(m.getImage("sentients/big_eyes"), 0, 0, null);
		}
		if (st.color != null) {
			img = m.tint(img, TINTS.get(st.color));
		}
		if (st.prefixes.contains(SentientType.Prefix.GIANT)) {
			return m.scale(img, 1.25);
		}
		if (st.prefixes.contains(SentientType.Prefix.TINY)) {
			return m.scale(img, 0.75);
		}
		return img;
	}
}
