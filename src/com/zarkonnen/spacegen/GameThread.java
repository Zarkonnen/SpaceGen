package com.zarkonnen.spacegen;

import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import javax.swing.JOptionPane;

public class GameThread implements Runnable {
	GameWorld world;
	GameDisplay display;
	BufferStrategy bs;
	Input input;
	GameControls controls;
	public GameThread(GameWorld world, Input input, GameDisplay display, GameControls controls, BufferStrategy bs) {
		this.world = world;
		this.input = input;
		this.display = display;
		this.controls = controls;
		this.bs = bs;
	}

	@Override
	public void run() {
		try {
			while (true) {
				controls.processInput();
				world.tick();
				Graphics2D g = (Graphics2D) bs.getDrawGraphics();
				display.draw(g);
				bs.show();

				Thread.sleep(35);
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
}
