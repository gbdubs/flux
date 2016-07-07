import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class Frame implements Runnable{

	private String filename;
	private int repetitions;
	private int resolution;
	
	private double a;
	private double b;
	private double c;
	private double d;
	private double e;
	private double f;
	
	private int[][] counts;
	
	public static Frame create(double[] vars){
		return new Frame(vars[0],vars[1],vars[2],vars[3],vars[4],vars[5] );
	}
	
	public Frame(double a, double b, double c, double d, double e, double f){
		this.resolution = 1000;
		this.repetitions = 1000000;
		this.filename = "default.png";
		
		this.a = a; this.b = b; this.c = c; this.d = d; this.e = e; this.f = f;
		
		this.counts = new int[resolution][resolution];
	}
	
	public Frame withFilename(String filename){
		this.filename = filename;
		return this;
	}
	
	public Frame withResolution(int resolution){
		this.resolution = resolution;
		return this;
	}
	
	public Frame withRepetitions(int repetitions){
		this.repetitions = repetitions;
		return this;
	}
	
	@Override
	public void run() {
		this.execute(this.repetitions);
		this.print(filename);
	}

	private void execute(int repetitions){
		double x = 0, y = 0, z = 0;
		double newX, newY, newZ;
		for (int rep = 0; rep < repetitions; rep++){
			increment(x, y, z);
			newX = xTransform(x, y, z);
			newY = yTransform(x, y, z);
			newZ = zTransform(x, y, z);
			x = newX;
			y = newY;
			z = newZ;
		}
	}

	private void increment(double x, double y, double z){
		int ix = (int) Math.floor((x + 3) / 6 * (resolution - 1));
		int iix = (int) Math.floor((y + 3) / 6 * (resolution - 1));
		//int iiix = (int) Math.floor((z + 3) / 6 * (resolution - 1));
		this.counts[ix][iix]++;
	}

	private double xTransform(double x, double y, double z){
		return Math.cos(b * x) + Math.sin(a * y) - Math.cos(c * z);
	}
	
	private double yTransform(double x, double y, double z){
		return Math.sin(d * x) + Math.cos(e * y) - Math.cos(f * z);
	}
	
	private double zTransform(double x, double y, double z){
		return z + .01;
	}
	
	private void print(String filename){
		short[][] gray = countsToGrayscale(counts);
		printGrayscaleImageToFile(gray, filename);
	}
	
	private static short[][] countsToGrayscale(int[][] counts){
		int max = 0;
		int min = Integer.MAX_VALUE;
		short[][] result = new short[counts.length][counts[0].length];
		for (int i = 0; i < counts.length; i++){
			for (int j = 0; j < counts[0].length; j++){
				max = Math.max(max, counts[i][j]);
				min = Math.min(min, counts[i][j]);
			}
		}
		for (int i = 0; i < counts.length; i++){
			for (int j = 0; j < counts[0].length; j++){
				double d = 255 - 255.0 * Math.sqrt((counts[i][j] * 1.0) / max);
				//double d = counts[i][j] > 0 ? 0.0 : 255.0;
				short s = (short) Math.floor(d);
				result[i][j] = s;
			}
		}
		return result;
	}

	private void printGrayscaleImageToFile(short[][] grayscale, String filename){
		BufferedImage image = new BufferedImage(resolution, resolution, BufferedImage.TYPE_USHORT_GRAY);
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
	
	public static void main(String[] args){
		
		double a = .77;
		double b = .73;
		double c = .41;
		double d = .63;
		double e = 1.56;
		double f = .47;
		
		int frameNo = 0;
		
		
		for (double delta = 0; delta < 1; delta+=.01) {
			Frame frame = new Frame(a + delta, b, c, d, e - delta, f).withRepetitions(1000000).withFilename("frame"+(frameNo++)+".png").withResolution(1000);
			Thread t = new Thread(frame);
			t.start();
		}
	}
	
}
