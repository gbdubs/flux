import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main {

	public static void main(String[] args){
		for (int i = 0; i < 10; i++){
			makeRandomGif("results/"+RandomCatName.generate()+i);
		}
	}
			
	public static void makeRandomGif(String filename){
		List<double[]> parameterSequence = ParameterSequence.generateParameterSequence();
		List<Frame> frames = new ArrayList<Frame>();
		for (double[] params : parameterSequence){
			Frame f = Frame.create(params).withRepetitions(1000000).withResolution(500);
			frames.add(f);
		}
		AnimationRunner ar = new AnimationRunner(frames, filename);
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
