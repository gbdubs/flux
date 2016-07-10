package grady.v1circular;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ParameterSequence {

	private static final int dimensions = 6;
	private static double ACTUALIZATION_CONSTANT = .01;
	private static double K = 200;
	
	
	public static List<double[]> generateCircularParameterSequence(){
		List<double[]> result = generateUnitCircle();
		for (double[] ds : result){
			for (int i = 0; i < dimensions; i++){
				ds[i] = ds[i] + 1;
			}
		}
		System.out.println(result.size());
		return result;
	}
	
	private static List<double[]> generateUnitCircle(){
		double[] pos = generateRandom();
		double[] initialPos = pos.clone();
		double[] velo = generateInitialVelo(pos);
		double[] accel = generateAccel(pos);
		List<double[]> result = new ArrayList<double[]>();
		result.add(pos);
		int numFrames = 0;
		while (numFrames++ < 100 || dist(pos, initialPos) > .05){
			velo = actualize(velo, accel, ACTUALIZATION_CONSTANT);
			pos = actualize(pos, velo, ACTUALIZATION_CONSTANT);
			ensureBounds(pos, velo);
			accel = generateAccel(pos);
			result.add(pos);
		}
		return result;
	}
	
	private static void ensureBounds(double[] pos, double[] velo){
		for (int i = 0; i < dimensions; i++){
			if (pos[i] >= Math.sqrt(dimensions)){
				System.err.println("CRASH!");
			}
			if (pos[i] <= -1*Math.sqrt(dimensions)) {
				System.err.println("CRASH!");
			}
		}
	}
	
	private static double[] generateInitialVelo(double[] pos){
		double[] result = new double[dimensions];
		double dotTotal = 0;
		
		for (int i = 0; i < dimensions - 1; i++){
			result[i] = .5 * (Math.random() - .5);
			dotTotal += pos[i] * result[i];
		}
		result[dimensions - 1] = -1 * dotTotal / pos[dimensions - 1];
		
		double currentMagnitude = 0;
		for (int i = 0; i < dimensions; i++){
			currentMagnitude += result[i] * result[i];
		}
		currentMagnitude = Math.sqrt(currentMagnitude);
		
		double goalMagnitude = Math.sqrt(K / dist(pos));
		for (int i = 0; i < dimensions; i++){
			result[i] *= goalMagnitude / currentMagnitude;
		}
		
		return result;
	}
	
	private static double[] generateRandom(){
		double[] result = new double[dimensions];
		for (int i = 0; i < dimensions; i++){
			result[i] = Math.sqrt(Math.random());
			result[i] = Math.random() > .5 ? result[i] : -1 * result[i];
		}
		return result;
	}
	
	private static double[] actualize(double[] velo, double[] accel, double actualizationFactor){
		double[] newVelo = new double[dimensions];
		for(int i = 0; i < dimensions; i++){
			newVelo[i] = velo[i] + accel[i] * actualizationFactor;
		}
		return newVelo;
	}
	
	private static double generateForce(double d){
		return -1 * d;
	}
	
	private static double[] generateAccel(double[] pos){
		double[] result = new double[dimensions];
		double dist = dist(pos);
		double currentMagnitude = 0;
		for (int i = 0; i < dimensions; i++){
			result[i] = generateForce(pos[i]);
			currentMagnitude += result[i] * result[i];
		}
		currentMagnitude = Math.sqrt(currentMagnitude);
		double targetMagnitude = K / (dist * dist);
		for (int i = 0; i < dimensions; i++){
			result[i] *= targetMagnitude / currentMagnitude;
		}
		return result;
	}

	private static double dist(double[] a){
		double result = 0;
		for (double d : a){
			result += d*d;
		}
		return Math.sqrt(result);
	}
	
	private static double dist(double[] a, double[] b){
		double result = 0;
		for (int i = 0; i < a.length; i++){
			result += (a[i]-b[i])*(a[i]-b[i]);
		}
		return Math.sqrt(result);
	}
	
	public static void main(String[] args) throws FileNotFoundException{
		List<double[]> result = generateCircularParameterSequence();
		System.out.println(result.size());
		for (double[] d : result){
			System.out.println(Arrays.toString(d) + " = " + dist(d));
		}
		System.out.println("==============================================");
		PrintWriter pw = new PrintWriter(new File("/Users/Grady/Desktop/points.txt"));
		for (double[] d : result){
			pw.println(Arrays.toString(d).replace("[","").replace("]", ""));	
		}
		pw.close();
	}
	
}
