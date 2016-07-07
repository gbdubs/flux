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

import EllitKroo.GifSequenceWriter;


public class AnimationRunner {

	private List<Frame> frames;
	private String filename;
	
	public AnimationRunner(List<Frame> frames, String outputName){
		this.frames = frames;
	}
	
	private String frameName(int frameNo){
		return filename+"-frames/frame"+(frameNo++)+".png";
	}
	
	public void run() throws InterruptedException, FileNotFoundException, IOException{
		Set<Thread> threads = new HashSet<Thread>();
		int frameNo = 0;
		for (Frame f : frames){
			f.withFilename(frameName(frameNo++));
			Thread t = new Thread(f);
			threads.add(t);
			t.start();
		}
		for (Thread t : threads){
			t.join();
		}
		
		BufferedImage frame0 = ImageIO.read(new File(frameName(0)));
		ImageOutputStream output = new FileImageOutputStream(new File(this.filename));
		GifSequenceWriter writer = new GifSequenceWriter(output, frame0.getType(), 1, true);
		
		for (frameNo = 0; frameNo < frames.size(); frameNo++) {
			writer.writeToSequence(ImageIO.read(new File(frameName(frameNo))));
	    }
		
		writer.close();
	    output.close();
	}
	
}
