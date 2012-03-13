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

import java.awt.Canvas;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.Collection;
import javax.swing.JFrame;

public class Main {
	public static GameThread gt;
	public static GameWorld w;
	public static JFrame frame;
	
	public static void confirm() {
		w.confirmNeeded = true;
		w.confirm = false;
		w.stage.doTrack = !w.autorun;
		gt.subRun();
	}
	
	public static void animate(Stage.Animation... as) {
		w.stage.animate(as);
		gt.subRun();
	}
	
	public static void add(Stage.Animation a) {
		w.stage.animate(a);
	}
	
	public static void animate() {
		gt.subRun();
	}
	
	public static void animate(Collection<Stage.Animation> as) {
		w.stage.animate(as);
		gt.subRun();
	}
	
    public static void main(String[] args) {
		int width = 800;
		int height = 600;
		frame = new JFrame("SpaceGen");
		frame.setIgnoreRepaint(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Transparent 16 x 16 pixel cursor image.
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

		// Create a new blank cursor.
		/*Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
				cursorImg, new Point(0, 0), "blank cursor");

		// Set the blank cursor to the JFrame.
		jf.getContentPane().setCursor(blankCursor);*/

		Canvas c = new Canvas();
		c.setCursor(null);
		c.setIgnoreRepaint(true);
		frame.add(c);
		frame.setSize(width, height);
		frame.setResizable(false);
		frame.setVisible(true);
		MediaProvider.createInstance(c.getGraphicsConfiguration());
		w = new GameWorld();
		GameDisplay d = new GameDisplay(w, width, height);
		c.createBufferStrategy(2);
		Input input = new Input();
		c.addKeyListener(input);
		c.addMouseListener(input);
		c.addMouseMotionListener(input);
		c.requestFocus();
		gt = new GameThread(w, input, d, new GameControls(d, w, input), c.getBufferStrategy());
		new Thread(gt, "Game Thread").start();
    }
}
