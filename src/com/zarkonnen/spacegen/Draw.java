package com.zarkonnen.spacegen;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Draw {
	static HashMap<String, BufferedImage> tints = new HashMap<String, BufferedImage>();
	static BufferedImage font;
	static final int F_WIDTH = 8;
	static final int F_DISP_WIDTH = 7;
	static final int F_HEIGHT = 13;
	static final int F_BASE = (int) ' ';
	static final int F_CEIL = (int) '~' + 1;
	static final int F_ERR = (int) '?' - F_BASE;
	static final int IMG_OFFSET = 0;
	
	static void loadFont() {
		if (font == null) {
			font = MediaProvider.it.readImage("font4", Transparency.BITMASK);
		}
	}
	
	public static void text(Graphics g, String text, int x, int y) {
		text(g, text, x, y, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
		
	public static void text(Graphics g, String text, int x, int y, int maxWidth, int maxHeight) {
		loadFont();
		BufferedImage f = font;
		int cols = maxWidth / F_WIDTH;
		int rows = maxHeight / F_HEIGHT;
		int c = 0;
		int r = 0;
		int n = 0;
		char[] cs = text.toCharArray();
		while (n < cs.length) {
			if (c >= cols) {
				c = 0;
				r++;
			}
			if (r >= rows) {
				return;
			}
			if (cs[n] == '\\') {
				n++;
			} else {
				if (cs[n] == '\n') {
					c = 0;
					r++;
					n++;
					continue;
				}
				if (cs[n] == '{') {
					int n2 = n + 1;
					while (cs[n2] != '}') { n2++; }
					char[] name = new char[n2 - n - 1];
					System.arraycopy(cs, n + 1, name, 0, name.length);
					BufferedImage sym = MediaProvider.it.getImage(new String(name).toUpperCase());
					g.drawImage(sym, x + (c) * F_DISP_WIDTH, y + r * F_HEIGHT, null);
					n = n2 + 1;
					c += (sym.getWidth() / F_DISP_WIDTH) + (sym.getWidth() % F_DISP_WIDTH == 0 ? 0 : 1);
					continue;
				}
				if (cs[n] == '[') {
					int n2 = n + 1;
					while (cs[n2] != ']') { n2++; }
					char[] name = new char[n2 - n - 1];
					System.arraycopy(cs, n + 1, name, 0, name.length);
					String tintN = new String(name);
					Color tintC = null;
					if (!tints.containsKey(tintN)) {
						try {
							tintC = (Color) Color.class.getField(tintN).get(null);
						} catch (Exception e) {
							// Ignore
						}
						if (tintC == null) {
							tints.put(tintN, font);
						} else {
							tints.put(tintN, MediaProvider.it.tint(font, tintC));
						}
					}
					f = tints.get(tintN);
					n = n2 + 1;
					continue;
				}
			}
			int val = (int) cs[n];
			val = val < F_CEIL ? val - F_BASE : F_ERR;
			g.drawImage(
					f,
					x + c * F_DISP_WIDTH, y + r * F_HEIGHT,
					x + (c + 1) * F_DISP_WIDTH, y + r * F_HEIGHT + F_HEIGHT,
					val * F_WIDTH + IMG_OFFSET, 0,
					(val) * F_WIDTH + F_DISP_WIDTH + IMG_OFFSET, F_HEIGHT,
					null
			);
			c++;
			n++;
		}
	}
}
