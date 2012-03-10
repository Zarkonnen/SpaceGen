package com.zarkonnen.spacegen;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Sprite {
	public int x;
	public int y;
	BufferedImage img;
	public ArrayList<Sprite> children = new ArrayList<Sprite>();
	public Sprite parent;
	boolean highlight;
	boolean flash;
	
	public int globalX() {
		if (parent == null) { return x; }
		return parent.globalX() + x;
	}
	
	public int globalY() {
		if (parent == null) { return y; }
		return parent.globalY() + y;
	}
}
