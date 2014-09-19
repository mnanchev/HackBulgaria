import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;

class StainedGlassFilter {
	private static Random r;
	private static KDTree<Integer> kd;
	private static double[][] keys;
	private static int counter;
	private static int x;

	public static void main(String[] args) throws IOException,
			KeySizeException, KeyDuplicateException {
		BufferedImage img = null;
		x = Integer.parseInt(args[2]);
		// input an image
		img = ImageIO.read(new File(args[0]));
		// get image width and height
		int w = img.getWidth();
		int h = img.getHeight();
		int[][] empty = new int[h][w];
		// generate a kdtree
		keys = new double[x][2];
		kd = new KDTree<Integer>(2);
		int[] totalBlue = new int[x];
		int[] totalGreen = new int[x];
		int[] totalRed = new int[x];
		int[] counter_sec = new int[x];
		// generate random centres
		for (int i = 0; i < x; i++) {
			generateCentre(empty, h, w);
		}
		// calculate the regions and average pixels per region
		double[] coords = new double[2];
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				coords[0] = i;
				coords[1] = j;
				Color c = new Color(img.getRGB(j, i));
				final int nearest = kd.nearest(coords);
				totalBlue[nearest] += c.getBlue();
				totalRed[nearest] += c.getRed();
				totalGreen[nearest] += c.getGreen();
				counter_sec[nearest]++;
			}
		}
		// change the region color to average region color
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				double[] array = new double[2];
				array[0] = i;
				array[1] = j;
				final int nearest = kd.nearest(array);
				Color color = new Color(totalRed[nearest]
						/ counter_sec[nearest], totalGreen[nearest]
						/ counter_sec[nearest], totalBlue[nearest]
						/ counter_sec[nearest]);
				img.setRGB(j, i, color.getRGB());
			}
		}
		// output the picture
		File outputfile = new File("args[1]");
		ImageIO.write(img, "jpg", outputfile);
	}

	// the method for random centre points generation
	public static void generateCentre(int[][] empty, int h, int w)
			throws KeySizeException, KeyDuplicateException {
		r = new Random();
		int height = r.nextInt(h);
		int width = r.nextInt(w);
		for (int row = 0; row < h; row++) {
			for (int col = 0; col < w; col++) {
				if (row == height && col == width && empty[height][width] == 0) {
					empty[height][width] = 1;
					keys[counter][0] = row;
					keys[counter][1] = col;
					kd.insert(keys[counter], counter);
				}
			}
		}
		if (counter < x) {
			counter++;
		}
	}
}
