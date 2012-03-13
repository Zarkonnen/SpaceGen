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
			w.confirm = true;
			w.cooldown = 2;
		}
		if (input.keyDown(VK_R) && w.cooldown == 0) {
			w.autorun = !w.autorun;
			w.cooldown = 2;
			w.confirm = true;
		}
		
		if (input.keyDown(VK_S) && w.cooldown == 0) {
			JFileChooser jfc = new JFileChooser();
			input.keys[VK_S] = false;
			w.cooldown = 2;
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
