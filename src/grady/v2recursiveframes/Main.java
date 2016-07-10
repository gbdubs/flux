package grady.v2recursiveframes;
import grady.util.RandomCatName;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;


public class Main {

	public static void main(String[] args){
		for (int i = 0; i < 1; i++){
			//System.out.println(ParameterSequence.generateCircularParameterSequence().size());
			makeRandomGif("results/"+RandomCatName.generate());
		}
	}
			
	public static void makeRandomGif(String filename){
		List<double[]> parameterSequence = ParameterSequence.generateCircularParameterSequence();
		AdvancedAnimationRunner ar = new AdvancedAnimationRunner(parameterSequence);
		ar = ar.withFilename(filename).withResolution(500).withRepetitions(1000000);
		try {
			ar.run();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
