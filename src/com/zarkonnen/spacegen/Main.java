package com.zarkonnen.spacegen;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
		int width = 800;
		int height = 600;

		JFrame jf = new JFrame("Game");
		jf.setIgnoreRepaint(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Transparent 16 x 16 pixel cursor image.
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

		// Create a new blank cursor.
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
				cursorImg, new Point(0, 0), "blank cursor");

		// Set the blank cursor to the JFrame.
		jf.getContentPane().setCursor(blankCursor);

		Canvas c = new Canvas();
		c.setCursor(null);
		c.setIgnoreRepaint(true);
		jf.add(c);
		jf.setSize(width, height);
		jf.setResizable(false);
		jf.setVisible(true);
		MediaProvider.createInstance(c.getGraphicsConfiguration());
		GameWorld w = new GameWorld();
		GameDisplay d = new GameDisplay(w, width, height);
		c.createBufferStrategy(2);
		BufferStrategy bs = c.getBufferStrategy();
		Input input = new Input();
		c.addKeyListener(input);
		c.addMouseListener(input);
		c.addMouseMotionListener(input);
		c.requestFocus();
		GameThread gt = new GameThread(w, input, d, new Controls(d, w, input), c.getBufferStrategy());
		new Thread(gt, "Game Thread").start();
    }
}
