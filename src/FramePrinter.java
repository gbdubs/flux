import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class FramePrinter implements Runnable {

	private String filename;
	private int[][] counts;
	private int resolution;
	private int scaledMax;
	
	public FramePrinter(Frame f){
		this.filename = RandomCatName.generate();
		this.counts = f.getCounts();
		this.resolution = f.getResolution();
	}
	
	public FramePrinter withFilename(String filename){
		this.filename = filename;
		return this;
	}
	
	@Override
	public void run() {
		this.print(this.filename);
	}
	
	private void print(String filename){
		short[][] gray = countsToGrayscale(counts);
		printGrayscaleImageToFile(gray, filename);
	}
	
	private short[][] countsToGrayscale(int[][] counts){
		short[][] result = new short[counts.length][counts[0].length];
		for (int i = 0; i < counts.length; i++){
			for (int j = 0; j < counts[0].length; j++){
				double d = 255 - 255.0 * Math.sqrt((counts[i][j] * 1.0) / scaledMax);
				short s = (short) Math.floor(d);
				result[i][j] = s;
			}
		}
		return result;
	}

	private void printGrayscaleImageToFile(short[][] grayscale, String filename){
		BufferedImage image = new BufferedImage(resolution, resolution, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < resolution; y++) {
		    for (int x = 0; x < resolution; x++) {
		        image.setRGB(x, y, grayscaleToRGBInt(grayscale[y][x]));
			}
		}
		File outputfile = new File(filename);
		try {
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static int grayscaleToRGBInt(short s){
		int i = (int) s;
		i = (i << 8) + s;
		i = (i << 8) + s;
		return i;
	}

	public void adjustToMaxCount(int maxCount) {
		this.scaledMax = maxCount;
	}
	
}
