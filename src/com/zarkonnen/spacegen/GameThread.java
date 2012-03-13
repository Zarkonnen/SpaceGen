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

				Thread.sleep(25);
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	void subRun() {
		boolean done = false;
		try {
			while (!done) {
				controls.processInput();
				done = world.subTick();
				Graphics2D g = (Graphics2D) bs.getDrawGraphics();
				display.draw(g);
				bs.show();
				
				Thread.sleep(25);
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
}
