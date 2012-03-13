package com.zarkonnen.spacegen;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Input implements KeyListener, MouseListener, MouseMotionListener {
	boolean[] keys = new boolean[65536];
	Point click = null;
	Point mouse = null;

	boolean keyDown(int code) {
		return keys[code];
	}

	public void keyTyped(KeyEvent ke) {}

	public void keyPressed(KeyEvent ke) {
		keys[ke.getKeyCode()] = true;
	}

	public void keyReleased(KeyEvent ke) {
		keys[ke.getKeyCode()] = false;
	}

	public void mouseClicked(MouseEvent me) {}

	public void mousePressed(MouseEvent me) {
		click = me.getPoint();
	}

	public void mouseReleased(MouseEvent me) {}

	public void mouseEntered(MouseEvent me) { mouse = me.getPoint(); }

	public void mouseExited(MouseEvent me) { mouse = null; }

	public void mouseDragged(MouseEvent me) {}

	public void mouseMoved(MouseEvent me) {
		mouse = me.getPoint();
	}
}
