package com.zarkonnen.spacegen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class Stage {
	public ArrayList<Sprite> sprites = new ArrayList<Sprite>();
	public ArrayList<Animation> animations = new ArrayList<Animation>();
	public static final Color FLASH = new Color(255, 255, 180);
	
	public int camX, camY;
	
	public void animate(Animation a) { animations.add(a); }
	public void animate(Animation... as) { animations.addAll(Arrays.asList(as)); }
	public void animate(Collection<Animation> as) { animations.addAll(as); }
	
	public boolean tick() {
		for (Iterator<Animation> it = animations.iterator(); it.hasNext();) {
			if (it.next().tick(this)) { it.remove(); }
		}
		return animations.isEmpty();
	}
	
	public void draw(Graphics2D g) {
		for (Sprite s : sprites) { draw(g, s, 0, 0); }
	}
	
	public void draw(Graphics2D g, Sprite s, int dx, int dy) {
		if (s.flash) {
			g.drawImage(MediaProvider.it.mask(s.img, FLASH), dx + s.x, dy + s.y, null);
		} else if (s.highlight) {
			g.drawImage(MediaProvider.it.border(s.img, FLASH), dx + s.x - 1, dy + s.y - 1, null);
		} else {
			g.drawImage(s.img, dx + s.x, dy + s.y, null);
		}
		
		for (Sprite ss : s.children) {
			draw(g, ss, dx + s.x, dy + s.y);
		}
	}
	
	public static interface Animation {
		public boolean tick(Stage stage);
	}
	
	public static Animation delay(int wait, Animation a) { return new Delay(wait, a); }
	public static Animation delay(int wait) { return new Delay(wait, null); }
	public static class Delay implements Animation {
		Animation a;
		int wait;

		public Delay(int wait, Animation a) { this.wait = wait; this.a = a; }

		@Override
		public boolean tick(Stage stage) {
			if (wait > 0) {
				wait--;
				return false;
			}
			return a == null ? true : a.tick(stage);
		}
	}
	
	public static Animation tracking(Sprite s, Animation a) { return new Tracking(s, a); }
	public static class Tracking implements Animation {
		Sprite s;
		Animation a;
		int tick = 0;
		int time = 0;
		int sx, sy;
		boolean lock;

		public Tracking(Sprite s, Animation a) { this.s = s; this.a = a; }

		@Override
		public boolean tick(Stage stage) {
			int tx = s.globalX() + s.img.getWidth() / 2;
			int ty = s.globalY() + s.img.getHeight() / 2;
			if (tick == 0) {
				sx = stage.camX;
				sy = stage.camY;
				time = (int)
						(Math.sqrt((sx - tx) * (sx - tx) + (sy - ty) * (sy - ty)) / 100) + 5;
			}
			if (!lock) {
				stage.camX = sx + (tx - sx) * tick / time;
				stage.camY = sy + (ty - sy) * tick / time;
				tick++;
				lock = tick > time;
				return false;
			} else {
				stage.camX = tx;
				stage.camY = ty;
				return a.tick(stage);
			}
		}
	}
	
	public static Animation seq(Animation... seq) { return new Seq(seq); }
	public static class Seq implements Animation {
		Animation[] seq;
		int index = 0;

		public Seq(Animation... seq) { this.seq = seq; }

		@Override
		public boolean tick(Stage stage) {
			if (seq[index].tick(stage)) {
				index++;
			}
			return index == seq.length;
		}
	}
	
	public static Animation sim(Animation... sim) { return new Sim(sim); }
	public static class Sim implements Animation {
		Animation[] sim;

		public Sim(Animation... sim) { this.sim = sim; }

		@Override
		public boolean tick(Stage stage) {
			boolean live = false;
			for (int i = 0; i < sim.length; i++) {
				if (sim[i] != null) {
					if (sim[i].tick(stage)) {
						sim[i] = null;
					} else {
						live = true;
					}
				}
			}
			return !live;
		}
	}
	
	public static Animation move(Sprite s, int tx, int ty, int time) { return new Move(s, tx, ty, time); }
	public static Animation move(Sprite s, int tx, int ty) { return new Move(s, tx, ty); }
	public static class Move implements Animation {
		Sprite s;
		int sx, sy, tx, ty, time;
		int tick = 0;

		public Move(Sprite s, int tx, int ty, int time) {
			this.s = s;
			this.tx = tx;
			this.ty = ty;
			this.time = time;
		}

		public Move(Sprite s, int tx, int ty) {
			this.s = s;
			this.tx = tx;
			this.ty = ty;
			this.time = 50;
		}
		
		@Override
		public boolean tick(Stage stage) {
			if (tick == 0) {
				sx = s.x;
				sy = s.y;
			}
			s.highlight = true;
			s.x = (tx - sx) * tick / time;
			s.y = (ty - sy) * tick / time;
			if (tick++ > time) {
				s.highlight = false;
				return true;
			}
			return false;
		}
	}
	
	public Animation remove(Sprite s, Sprite parent) { return new Remove(s, parent); }
	public Animation remove(Sprite s) { return new Remove(s); }
	public static class Remove implements Animation {
		Sprite s;
		Sprite parent;
		int tick = 0;

		public Remove(Sprite s, Sprite parent) {
			this.s = s;
			this.parent = parent;
		}

		public Remove(Sprite s) {
			this.s = s;
		}
		
		@Override
		public boolean tick(Stage stage) {
			s.flash = true;
			if (tick++ > 5) {
				if (parent == null) {
					stage.sprites.remove(s);
				} else {
					parent.children.remove(s);
				}
				return true;
			}
			return false;
		}
	}
	
	public static Animation add(Sprite s, Sprite parent) { return new Add(s, parent); }
	public static Animation add(Sprite s) { return new Add(s); }
	public static class Add implements Animation {
		Sprite s;
		Sprite parent;
		int tick = 0;

		public Add(Sprite s, Sprite parent) {
			this.s = s;
			this.parent = parent;
		}

		public Add(Sprite s) {
			this.s = s;
		}
		
		@Override
		public boolean tick(Stage stage) {
			s.flash = true;
			if (tick++ == 0) {
				if (parent == null) {
					stage.sprites.add(s);
				} else {
					parent.children.add(s);
				}
				s.parent = parent;
			}
			if (tick > 5) {
				s.flash = false;
				return true;
			}
			return false;
		}
	}
	
	public static Animation change(Sprite s, BufferedImage newImg) { return new Change(s, newImg); }
	public static class Change implements Animation {
		Sprite s;
		BufferedImage newImg;
		int tick = 0;

		public Change(Sprite s, BufferedImage newImg) {
			this.s = s;
			this.newImg = newImg;
		}
		
		@Override
		public boolean tick(Stage stage) {
			s.flash = true;
			if (tick++ > 2) {
				s.img = newImg;
			}
			if (tick > 5) {
				s.flash = false;
				return true;
			}
			return false;
		}
	}
	
	public static Animation emancipate(Sprite s) { return new Emancipate(s); }
	public static class Emancipate implements Animation {
		Sprite s;

		public Emancipate(Sprite s) {
			this.s = s;
		}
		
		@Override
		public boolean tick(Stage stage) {
			if (s.parent != null) {
				s.x = s.globalX();
				s.y = s.globalY();
				s.parent.children.remove(s);
				s.parent = null;
				stage.sprites.add(s);
			}
			return true;
		}
	}
	
	public static Animation subordinate(Sprite s, Sprite parent) { return new Subordinate(s, parent); }
	public static class Subordinate implements Animation {
		Sprite s;
		Sprite parent;

		public Subordinate(Sprite s, Sprite parent) {
			this.s = s;
			this.parent = parent;
		}
		
		@Override
		public boolean tick(Stage stage) {
			if (s.parent != null) {
				s.x = s.globalX();
				s.y = s.globalY();
				s.parent.children.remove(s);
				s.parent = null;
			}
			stage.sprites.remove(s);
			s.parent = parent;
			parent.children.add(s);
			s.x -= parent.globalX();
			s.y -= parent.globalY();
			return true;
		}
	}
}
