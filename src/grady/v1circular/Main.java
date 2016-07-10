package grady.v1circular;
import grady.common.Frame;
import grady.util.RandomCatName;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main {

	public static void main(String[] args){
		for (int i = 0; i < 1; i++){
			makeRandomGif("results/"+RandomCatName.generate()+i);
		}
	}
			
	public static void makeRandomGif(String filename){
		List<double[]> parameterSequence = ParameterSequence.generateCircularParameterSequence();
		List<Frame> frames = new ArrayList<Frame>();
		for (double[] params : parameterSequence){
			Frame f = Frame.create(params).withRepetitions(100000).withResolution(100);
			frames.add(f);
		}
		AnimationRunner ar = new AnimationRunner(frames, filename);
		try {
			ar.run();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
