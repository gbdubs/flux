import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class Frame {

	private int resolution;
	
	private double a;
	private double b;
	private double c;
	private double d;
	private double e;
	private double f;
	
	private int[][] counts;
	
	public Frame(){
		this.resolution = 1000;
		
		a = .77;
		b = .73;
		c = .41;
		d = .63;
		e = 1.56;
		f = .47;
		
		this.counts = new int[resolution][resolution];
	}
	
	private double xTransform(double x, double y, double z){
		return Math.cos(a * x) + Math.sin(b * y) - Math.cos(c * z);
	}
	
	private double yTransform(double x, double y, double z){
		return Math.sin(d * x) + Math.cos(e * y) - Math.cos(f * z);
	}
	
	private double zTransform(double x, double y, double z){
		return z + .01;
	}
	
	public void run(int repetitions){
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
	
	public void print(String filename){
		short[][] gray = countsToGrayscale(counts);
		printGrayscaleImageToFile(gray, filename);
	}
	
	private void increment(double x, double y, double z){
		int ix = (int) Math.floor((x + 3) / 6 * (resolution - 1));
		int iix = (int) Math.floor((y + 3) / 6 * (resolution - 1));
		//int iiix = (int) Math.floor((z + 3) / 6 * (resolution - 1));
		this.counts[ix][iix]++;
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
	
	private static int grayscaleToRGBInt(short s){
		int i = (int) s;
		i = (i << 8) + s;
		i = (i << 8) + s;
		return i;
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
	
	public static void main(String[] args){
		Frame f = new Frame();
		long l = System.currentTimeMillis();
		f.run(3000000);
		System.out.println(System.currentTimeMillis() - l);
		f.print("helloworld.png");
	}
	
}
