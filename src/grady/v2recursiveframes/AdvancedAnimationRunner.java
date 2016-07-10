package grady.v2recursiveframes;
import external.EllitKroo.GifSequenceWriter;
import grady.common.Frame;
import grady.common.FramePrinter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;


public class AdvancedAnimationRunner {

	private static final long MAX_BYTES_ALLOCATED = 1000 * 1000 * 1000; // 1 GB
	
	
	private List<double[]> parameters;
	private String filename;
	private int resolution;
	private int repetitions;
	
	public AdvancedAnimationRunner(List<double[]> params){
		this.parameters = params;
	}
	
	public AdvancedAnimationRunner withFilename(String s){
		this.filename = s;
		return this;
	}
	
	public AdvancedAnimationRunner withResolution(int s){
		this.resolution = s;
		return this;
	}
	
	public AdvancedAnimationRunner withRepetitions(int s){
		this.repetitions = s;
		return this;
	}
	
	private String frameFolderName(){
		return filename+"-frames";
	}
	
	private String frameName(int frameNo){
		return frameFolderName() + "/frame"+frameNo+"."+FramePrinter.PHOTO_FILE_EXTENSION;
	}
	
	public void run() throws FileNotFoundException, InterruptedException, IOException{
		new File(frameFolderName()).mkdir();
		createFrames();
		System.out.println("CREATED FRAMES");
		printGifFromFrames();
		System.out.println("PRINTED GIF");
	}
	
	public void createFrames() throws InterruptedException, FileNotFoundException, IOException{
		int maxThreads = (int) (MAX_BYTES_ALLOCATED / ((new Frame(0,0,0,0,0,0)).getNumBytes() * 2));
		System.out.println("MAX BATCH SIZE: " + maxThreads);
		
		Set<Thread> runPool = new HashSet<Thread>();
		Set<Thread> printPool = new HashSet<Thread>();
		Set<Frame> framePool = new HashSet<Frame>();
		Set<FramePrinter> framePrinterPool = new HashSet<FramePrinter>();
		
		Map<double[], Integer> indexMapping = new HashMap<double[], Integer>();
		int index = 0;
		for (double[] d : parameters){
			indexMapping.put(d, index++);
		}
		
		Collections.shuffle(parameters);
		
		int maxCount = Integer.MIN_VALUE;
		int currentParamIndex = 0;
		
		while (currentParamIndex < parameters.size()){
			System.out.println("BEGINNING NEW BATCH, CURRENT PARAM INDEX: " + currentParamIndex + "/" + (parameters.size()-1));
			for (int nThreads = 0; nThreads < maxThreads && currentParamIndex < parameters.size(); nThreads++){
				double[] params = parameters.get(currentParamIndex);
				index = indexMapping.get(params);
				Frame f = Frame.create(params).withRepetitions(repetitions).withResolution(resolution);
				Thread t1 = new Thread(f);
				FramePrinter fp = new FramePrinter(f).withFilename(frameName(index));
				Thread t2 = new Thread(fp);
				framePool.add(f);
				framePrinterPool.add(fp);
				runPool.add(t1);
				printPool.add(t2);
				currentParamIndex++;
			}
			
			for (Thread t : runPool){
				t.start();
			}
			System.out.println("\tRUN - ALL STARTED");
			for (Thread t : runPool){
				t.join();
			}
			System.out.println("\tRUN - ALL JOINED");
			runPool.clear();
			
			
			if (maxCount < 0){
				for (Frame f : framePool){
					maxCount = Math.max(maxCount, f.getMaxCount());
				}
				System.out.println("\t\tMAX COUNT SET: "+ maxCount);
			}
			
			for (FramePrinter fp : framePrinterPool){
				fp.adjustToMaxCount(maxCount);
			}
			
			for (Thread t : printPool){
				t.start();
			}
			System.out.println("\tPRINT - ALL STARTED");
			for (Thread t : printPool){
				t.join();
			}
			System.out.println("\tPRINT - ALL JOINED");
			
			printPool.clear();
			framePool.clear();
			framePrinterPool.clear();
		}
	}
		
	public void printGifFromFrames() throws FileNotFoundException, IOException{
		File frameFolder = new File(frameFolderName());
	
		File[] frames = frameFolder.listFiles(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.endsWith("."+FramePrinter.PHOTO_FILE_EXTENSION);
		    }
		});
		
		Arrays.sort(frames, new Comparator<File>(){
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
		
		ImageOutputStream output = new FileImageOutputStream(new File(this.filename+"."+FramePrinter.MULTI_FILE_EXTENSION));
		GifSequenceWriter writer = new GifSequenceWriter(output, BufferedImage.TYPE_INT_RGB, 1, true);
		
		for (File f : frames) {
			writer.writeToSequence(ImageIO.read(f));
	    }
		
		writer.close();
	    output.close();
	    
	    for (File f : frames){
	    	f.delete();
	    }
	    
	    frameFolder.delete();
	}
}
