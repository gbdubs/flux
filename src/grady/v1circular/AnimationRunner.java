package grady.v1circular;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import external.EllitKroo.GifSequenceWriter;
import grady.common.Frame;
import grady.common.FramePrinter;


public class AnimationRunner {

	private List<Frame> frames;
	private String filename;
	
	public AnimationRunner(List<Frame> frames, String outputName){
		this.filename = outputName;
		new File(filename+"-frames").mkdir();
		this.frames = frames;
	}
	
	private String frameName(int frameNo){
		return filename+"-frames/frame"+(frameNo++)+".png";
	}
	
	public void run() throws InterruptedException, FileNotFoundException, IOException{
		Set<Thread> threads = new HashSet<Thread>();
		
		
		// All the computation of the frames is done in this portion.
		for (Frame f : frames){
			Thread t = new Thread(f);
			threads.add(t);
			t.start();
		}
		for (Thread t : threads){
			t.join();
		}
		
		threads.clear();
		
		
		int maxCount = 0;
		for (Frame f : frames){
			int count = f.getMaxCount();
			maxCount = Math.max(maxCount, count);
		}
		
		int frameNo = 0;
		
		for (Frame f : frames){
			FramePrinter fp = new FramePrinter(f).withFilename(frameName(frameNo++));
			fp.adjustToMaxCount(maxCount);
			Thread t = new Thread(fp);
			threads.add(t);
			t.start();
		}
		for (Thread t : threads){
			t.join();
		}
		
		ImageOutputStream output = new FileImageOutputStream(new File(this.filename + ".gif"));
		GifSequenceWriter writer = new GifSequenceWriter(output, BufferedImage.TYPE_INT_RGB, 1, true);
		
		for (frameNo = 0; frameNo < frames.size(); frameNo++) {
			writer.writeToSequence(ImageIO.read(new File(frameName(frameNo))));
	    }
		
		writer.close();
	    output.close();
	    
	    for (int i = 0; i < frames.size(); i++){
	    	new File(frameName(i)).delete();
	    }
	    new File(filename+"-frames").delete();
	}
	
}
