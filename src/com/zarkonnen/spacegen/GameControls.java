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

import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import static java.awt.event.KeyEvent.*;

public class GameControls {
	GameDisplay d;
	GameWorld w;
	Input input;

	public GameControls(GameDisplay d, GameWorld w, Input input) {
		this.d = d;
		this.w = w;
		this.input = input;
	}

	public void processInput() {
		d.ptr = input.mouse;
		if (input.keyDown(VK_UP)) { w.stage.camY -= 40; if (w.stage.camY < - 500) { w.stage.camY = -500; } }
		if (input.keyDown(VK_DOWN)) { w.stage.camY += 40; if (w.stage.camY > 2180) { w.stage.camY = 2180; } }
		if (input.keyDown(VK_LEFT)) { w.stage.camX -= 40; if (w.stage.camX < - 500) { w.stage.camX = -500; } }
		if (input.keyDown(VK_RIGHT)) { w.stage.camX += 40; if (w.stage.camX > 2180) { w.stage.camX = 2180; } }
		
		if (input.keyDown(VK_SPACE) && w.cooldown == 0) {
			w.stage.doTrack = true;
			w.confirm = true;
			w.cooldown = 8;
			return;
		}
		if (input.keyDown(VK_R) && w.cooldown == 0) {
			w.autorun = !w.autorun;
			w.cooldown = 10;
			if (w.autorun) {
				w.confirm = true;
				w.stage.doTrack = false;
			}
			return;
		}
		
		if (input.keyDown(VK_S) && w.cooldown == 0) {
			JFileChooser jfc = new JFileChooser();
			input.keys[VK_S] = false;
			w.cooldown = 6;
			if (jfc.showSaveDialog(Main.frame) == JFileChooser.APPROVE_OPTION) {
				try {
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(jfc.getSelectedFile()), "UTF-8"));
					bw.write(w.sg.describe());
					bw.write("\nHISTORY:\n");
					for (String s : w.sg.log) {
						bw.write(s);
						bw.write("\n");
					}
					bw.close();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
				}
			}
		}
	}
}
