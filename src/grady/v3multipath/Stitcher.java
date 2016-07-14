package grady.v3multipath;

import external.EllitKroo.GifSequenceWriter;
import grady.common.FramePrinter;
import grady.util.RandomCatName;
import grady.v2recursiveframes.AdvancedAnimationRunner;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

public class Stitcher {

	public static void createRawOptions(int n, int reps, int resolution) throws FileNotFoundException, InterruptedException, IOException{
		List<List<double[]>> paramSequences = ParameterSequence.generateCircularParameterSequences(n);
		String name = RandomCatName.generate();
		int runNo = 0;
		for (List<double[]> ps : paramSequences){
			AdvancedAnimationRunner aar = new AdvancedAnimationRunner(ps).withFilename(name + (runNo++)).withRepetitions(reps).withResolution(resolution).withShouldDelete(false);
			aar.run();
		}
	}
	
	
	public static void make(Map<String, Integer> name2Color, String filename) throws IOException{
		Map<String, File[]> name2Frames = new HashMap<String, File[]>();
		
		for (String name : name2Color.keySet()){
			File frameFolder = new File(name+"-frames");
			
			File[] fs = frameFolder.listFiles(new FilenameFilter() {
			    @Override
			    public boolean accept(File dir, String name) {
			        return name.endsWith("."+FramePrinter.PHOTO_FILE_EXTENSION);
			    }
			});
			
			Arrays.sort(fs, new Comparator<File>(){
				@Override
				public int compare(File f1, File f2) {
					int i1 = getFrameNumber(f1.getName());
					int i2 = getFrameNumber(f2.getName());
					return Integer.compare(i1, i2);
				}
				private int getFrameNumber(String s){
					return Integer.parseInt(s.substring(5, s.indexOf('.')));
				}
			});
			
			name2Frames.put(name, fs);
		}
		
		int nFrames = name2Frames.get(name2Color.keySet().iterator().next()).length;
		int nColors = name2Color.size();
		int[] colors = new int[nColors];
		
		ImageOutputStream output = new FileImageOutputStream(new File(filename+"."+FramePrinter.MULTI_FILE_EXTENSION));
		GifSequenceWriter writer = new GifSequenceWriter(output, BufferedImage.TYPE_INT_RGB, 1, true);
		
		// SKIP FIRST FRAME, IDK THIS ERRROR RN.
		for (int frameNo = 1; frameNo < nFrames; frameNo++) {
			List<BufferedImage> coloredFrames = new ArrayList<BufferedImage>();
			List<Integer> coloredColors = new ArrayList<Integer>();
			int dimension = 0;
			for (String name : name2Frames.keySet()){
				BufferedImage bi = ImageIO.read(name2Frames.get(name)[frameNo]);
				dimension = bi.getHeight();
				coloredFrames.add(bi);
				coloredColors.add(name2Color.get(name));
			}
			
			BufferedImage composite = new BufferedImage(dimension, dimension, BufferedImage.TYPE_INT_RGB);
			
			
			for (int i = 0; i < dimension; i++){
				for (int j = 0; j < dimension; j++){
					for (int k = 0; k < coloredFrames.size(); k++){
						int grayscale = coloredFrames.get(k).getRGB(i, j);
						colors[k] = colorize(grayscale, coloredColors.get(k));
					}
					int compositeColor = combineColors(colors);
					composite.setRGB(i, j, compositeColor);
				}
			}
			
			writer.writeToSequence(composite);
	    }
		
		writer.close();
	    output.close();
	    
	}
	
	private static int colorize(int grayscale, int color){
		double sat = Math.sqrt(1 - (R(grayscale))/255.0);
		if (sat == 0){
			return WHITE;
		}
		int r = R(color);
		int g = G(color);
		int b = B(color);
		r = (int) (255 - ((255 - R(color)) * sat));
		g = (int) (255 - ((255 - G(color)) * sat));
		b = (int) (255 - ((255 - B(color)) * sat));
		return toRGB(r, g, b);
	}
	
	private static int WHITE = toRGB(255, 255, 255);
	
	public static int combineColors(int[] colors){
		int nonWhite = 0;
		int r = 0;
		int g = 0;
		int b = 0;
		
		for (int i : colors){
			if (i != WHITE){
				nonWhite++;
				r += R(i);
				g += G(i);
				b += B(i);
			}
		}
		if (nonWhite > 0){
			return toRGB(r / nonWhite, g / nonWhite, b / nonWhite);
		} else {
			return WHITE;
		}
	}
	
	public static int toRGB(int r, int g, int b){
		return (((r << 8) + g) << 8) + b;
	}
	
	private static int R(int i){
		return (i >>> 16) & 255; 
	}
	
	private static int G(int i){
		return (i >>> 8) & 255;
	}
	
	private static int B(int i){
		return i & 255;
	}
}
