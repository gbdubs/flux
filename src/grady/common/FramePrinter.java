package grady.common;
import grady.util.RandomCatName;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class FramePrinter implements Runnable {

	public static final String PHOTO_FILE_EXTENSION = "png";
	public static final String MULTI_FILE_EXTENSION = "gif";
	
	private Frame frame;
	private String filename;
	private int scaledMax;
	
	public FramePrinter(Frame f){
		this.filename = RandomCatName.generate();
		this.frame = f;
		this.scaledMax = Integer.MAX_VALUE;
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
		short[][] gray = countsToGrayscale(frame.getCounts());
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
		BufferedImage image = new BufferedImage(frame.resolution, frame.resolution, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < frame.resolution; y++) {
		    for (int x = 0; x < frame.resolution; x++) {
		        image.setRGB(x, y, grayscaleToRGBInt(grayscale[y][x]));
			}
		}
		File outputfile = new File(filename);
		try {
			ImageIO.write(image, PHOTO_FILE_EXTENSION, outputfile);
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
