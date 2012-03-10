package com.zarkonnen.spacegen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.Transparency;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import java.util.HashMap;

import java.awt.geom.Rectangle2D;
import java.awt.image.WritableRaster;
import java.io.File;
import java.lang.ref.SoftReference;
import javax.sound.sampled.FloatControl;

/** Helper class for loading images and sounds. */
public class MediaProvider {
	static MediaProvider it;

	public static void createInstance(GraphicsConfiguration config) {
		it = new MediaProvider(config);
	}

	/** Map for caching images. */
	private HashMap<String, SoftReference<BufferedImage>> images = new HashMap<String, SoftReference<BufferedImage>>();

	/** Map for caching sounds. */
	private HashMap<String, Clip> sounds = new HashMap<String, Clip>();

	/** GraphicsConfiguration for properly formatting images. */
	private GraphicsConfiguration config;

	public int getVolume() { return volume; }
	public void setVolume(int volume) { this.volume = volume; }
	private int volume = 5;

	private float getGain() {
		return (volume - 5.0f) * 8;
	}

	public MediaProvider(GraphicsConfiguration config) {
		this.config = config;
	}

	public synchronized BufferedImage createImage(int width, int height, int transparency) {
		BufferedImage img = config.createCompatibleImage(width, height, transparency);
		return img;
	}

	/** Creates a scaled version of the given image. */
	public synchronized BufferedImage getScaledImage(BufferedImage image, double scale) {
		if (image == null) { return null; }
		return scale(image, scale);
	}

	public BufferedImage scale(BufferedImage src, double scale) {
		int sWidth = (int) (scale * src.getWidth());
		int sHeight = (int) (scale * src.getHeight());
 		final BufferedImage dst = config.createCompatibleImage(sWidth, sHeight,
				Transparency.BITMASK);
		final WritableRaster ar = src.getAlphaRaster();
		// Iterate over pixels in dst.
		for (int dstY = 0; dstY < dst.getHeight(); dstY++) { for (int dstX = 0; dstX < dst.getWidth(); dstX++) {
			double red = 0;
			double green = 0;
			double blue = 0;
			double alpha = 0;
			// The destination pixel projected into source space.
			Rectangle2D.Double dstPx = new Rectangle2D.Double(dstX / scale, dstY / scale, 1 / scale, 1 / scale);
			// The top left candidate pixel.
			Rectangle srcPxTopLeft = new Rectangle((int) Math.floor(dstPx.x), (int) Math.floor(dstPx.y), 1, 1);
			Rectangle srcPx = new Rectangle(srcPxTopLeft);

			Rectangle2D.Double intersection = new Rectangle2D.Double();

			int dY = 0;
			while (dstPx.intersects(srcPx) && srcPx.y < src.getHeight()) {
				int dX = 0;
				while (dstPx.intersects(srcPx) && srcPx.x < src.getWidth()) {
					Rectangle.intersect(srcPx, dstPx, intersection);
					double area = intersection.width * intersection.height;
					Color c = new Color(src.getRGB(srcPx.x, srcPx.y));
					red += c.getRed() * area;
					green += c.getGreen() * area;
					blue += c.getBlue() * area;
					alpha += ar.getSample(srcPx.x, srcPx.y, 0) * area;
					dX++;
					srcPx.x = srcPxTopLeft.x + dX;
				}

				dY++;
				srcPx.y = srcPxTopLeft.y + dY;
				srcPx.x = srcPxTopLeft.x;
			}

			// Normalise wrt/how much area we actually used.
			double dstPxArea = (1 / scale) * (1 / scale);
			red /= dstPxArea;
			green /= dstPxArea;
			blue /= dstPxArea;
			alpha /= dstPxArea;
			red = red > 255 ? 255 : red; red = red < 0 ? 0 : red;
			green = green > 255 ? 255 : green; green = green < 0 ? 0 : green;
			blue = blue > 255 ? 255 : blue; blue = blue < 0 ? 0 : blue;
			alpha = alpha > 255 ? 255 : alpha; alpha = alpha < 0 ? 0 : alpha;
			// Will prolly have to bound.
			Color c = new Color((int) red, (int) green, (int) blue, (int) alpha);
			dst.setRGB(dstX, dstY, c.getRGB());
		}}
		dst.flush();
		return dst;
	}

	/**
	 * Loads an image from in-jar resource, relative to com/metalbeetle/defaultgame/images.
	 * @param The image name. Assuming extension of jpg unless png extension is supplied.
	 * @return The image, or null on failure.
	*/
	public synchronized BufferedImage getImage(String name) {
		return getImage(name, Transparency.TRANSLUCENT);
	}
	
	public synchronized BufferedImage getImage(String name, int transparency) {
		if (images.containsKey(name)) {
			SoftReference<BufferedImage> r = images.get(name);
			BufferedImage img = r.get();
			if (img != null) { return img; }
		}
		BufferedImage img = readImage(name, transparency);
		images.put(name, new SoftReference<BufferedImage>(img));
		return img;
	}

	public synchronized BufferedImage readImage(String name, int transparency) {
		String extName = name;

		if (!extName.endsWith(".jpg")) {
			extName += ".png";
		}

		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(new File(Utils.getGameFolder(), "images"), extName));
		} catch (Exception e) { /* feh */ }
		if (img == null) {
			try {
				img = ImageIO.read(getClass().getResource("images/" + extName));
			} catch (Exception e) { /* feh */ }
		}
		try {
			BufferedImage fixedImg = config.createCompatibleImage(img.getWidth(), img.getHeight(),
					transparency);
			Graphics2D fig = fixedImg.createGraphics();
			fig.drawImage(img, 0, 0, null);
			fig.dispose();
			fixedImg.flush();
			return fixedImg;
		}
		catch (Exception e) { return null; }
	}

	/** Preloads a sound. */
	public synchronized void preloadSound(String name, int copies) {
		try {
			for (int i = 0; i < copies; i++) {
				String n = name + "#" + i++;
				if (!sounds.containsKey(n)) {
					sounds.put(n, getClip(name));
				}
			}
		} catch (Exception e) {}
	}

	/**
	 * Plays a sound loaded from in-jar resource, relative to com/zarkonnen/play/SEGame/sounds.
	 * @param name The sound name, assuming extension of wav.
	*/
	public synchronized void playSound(String name) {
		if (volume == 0) { return; }
		int i = 0;
		// Search for a unused sound.
		while (true) {
			String n = name + "#" + i++;
			if (sounds.containsKey(n)) {
				if (sounds.get(n).isRunning()) {
					continue;
				} else {
					// Found one!
					Clip c = sounds.get(n);
					// Try to set audio volume.
					try {
						FloatControl gc = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
						gc.setValue(getGain());
					} catch (Exception e) {}
					c.setFramePosition(0);
					c.start();
					//if (SEGame.isLinux()) { try { Thread.sleep(120); } catch (Exception e) {} }
					return;
				}
			} else {
				// Load a new sound.
				try {
					Clip c = getClip(name);
					sounds.put(n, c);
					// Try to set audio volume.
					try {
						FloatControl gc = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
						gc.setValue(getGain());
					} catch (Exception e) {}
					c.start();
					//if (SEGame.isLinux()) { try { Thread.sleep(120); } catch (Exception e) {} }
				} catch (Exception e) {}
				return;
			}
		}
	}

	/** Starts looping a given sound. */
	public synchronized void loopSound(String name) {
		try {
			String n = name + "#looping";
			if (!sounds.containsKey(n)) {
				sounds.put(n, getClip(name));
			}
			if (!sounds.get(n).isRunning()) {
				sounds.get(n).loop(Clip.LOOP_CONTINUOUSLY);
			}
		} catch (Exception e) { e.printStackTrace(); /* swallow */ }
	}

	/** Stops looping a given sound. */
	public synchronized void stopLooping(String name) {
		String n = name + "#looping";
		try {
			if (sounds.containsKey(n)) {
				sounds.get(n).stop();
				sounds.get(n).setFramePosition(0);
			}
		} catch (Exception e) { e.printStackTrace(); /* swallow */ }
	}

	/** Used to set the gain on a particular looped sound. */
	public synchronized void setLoopGain(String name, double gain) {
		try {
			gain = (gain<=0.0)? 0.0001 : ((gain>1.0)? 1.0 : gain);
	        float dB = (float)(Math.log(gain)/Math.log(10.0)*20.0);

			String n = name + "#looping";
			if (sounds.containsKey(n)) {
				FloatControl mg = (FloatControl) sounds.get(n).getControl(FloatControl.Type.MASTER_GAIN);
				mg.setValue(dB);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Loads a sound clip. */
	private Clip getClip(String name) throws Exception {
		AudioInputStream stream = AudioSystem.getAudioInputStream(getClass().getResource("sounds/" + name + ".wav"));
		DataLine.Info info = new DataLine.Info(Clip.class, stream.getFormat());
		Clip clip = (Clip) AudioSystem.getLine(info);
		clip.open(stream);
		return clip;
	}
	
	public BufferedImage border(BufferedImage src, Color borderC) {
		final int w = src.getWidth();
		final int h = src.getHeight();
		final BufferedImage dst = createImage(w + 2, h + 2, Transparency.BITMASK);
		Graphics2D g = dst.createGraphics();
		g.drawImage(src, 1, 1, null);
		final WritableRaster ar = src.getAlphaRaster();
		for (int y = 0; y < h + 2; y++) { for (int x = 0; x < w + 2; x++) {
			if (y > 0 && y < h + 1 && x > 0 && x < h + 1 && ar.getSample(x - 1, y - 1, 0) > 0) { continue; }
			boolean border = false;
			lp: for (int dy = -2; dy < 1; dy++) { for (int dx = -2; dx < 1; dx++) {
				if (y + dy < 0 || y + dy >= h || x + dx < 0 || x + dx >= w) {
					continue;
				}
				if (ar.getSample(x + dx, y + dy, 0) > 0) { border = true; break lp; }
			}}
			if (border) {
				dst.setRGB(x, y, borderC.getRGB());
			}
		}}
		return dst;
	}
	
	public BufferedImage mask(BufferedImage src, Color fillC) {
		final int w = src.getWidth();
		final int h = src.getHeight();
		final BufferedImage dst = createImage(w, h, Transparency.BITMASK);
		final WritableRaster ar = src.getAlphaRaster();
		for (int y = 0; y < h; y++) { for (int x = 0; x < w; x++) {
			if (ar.getSample(x, y, 0) > 0) {
				dst.setRGB(x, y, fillC.getRGB());
			}
		}}
		return dst;
	}
	
	public BufferedImage tint(BufferedImage src, Color tint) {
		final int w = src.getWidth();
		final int h = src.getHeight();
		final BufferedImage dst = createImage(w, h, src.getTransparency());
		final WritableRaster ar = src.getAlphaRaster();
		final int a = tint.getAlpha();
		final int r = tint.getRed() * a;
		final int g = tint.getGreen() * a;
		final int b = tint.getBlue() * a;
		final int na = 255 - a;

		for (int y = 0; y < h; y++) { for (int x = 0; x < w; x++) {
			// Need to extract alpha value from alpha raster because getRGB is broken.
			Color c = new Color(src.getRGB(x, y));
			c = new Color(
					(c.getRed() * na + (r * c.getRed()) / 256) / 256,
					(c.getGreen() * na + (g * c.getGreen()) / 256) / 256,
					(c.getBlue() * na + (b * c.getBlue()) / 256) / 256,
					ar.getSample(x, y, 0)
			);
			dst.setRGB(x, y, c.getRGB());
		}}
		return dst;
	}
}