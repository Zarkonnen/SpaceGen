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
