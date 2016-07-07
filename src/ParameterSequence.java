import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ParameterSequence {

	private static double ACTUALIZATION_CONSTANT = .1;
	private static double NATURAL_POINT = 1;
	
	public static List<double[]> generate(int numFrames){
		double[] pos = generateRandom();
		double[] velo = generateInitialVelo(pos);
		double[] accel = generateAccel(pos);
		List<double[]> result = new ArrayList<double[]>();
		result.add(pos);
		for (int i = 1; i < numFrames; i++){
			velo = actualizeAcceleration(velo, accel);
			pos = actualizeVelocity(pos, velo);
			ensureBounds(pos, velo);
			accel = generateAccel(pos);
			result.add(pos);
		}
		return result;
	}
	
	private static void ensureBounds(double[] pos, double[] velo){
		for (int i = 0; i < 6; i++){
			if (pos[i] >= Math.PI){
				System.err.println("CRASH!");
				pos[i] = Math.PI - .01;
				if (velo[i] > 0){
					velo[i] = -1 * velo[i];
				}
			}
			if (pos[i] <= 0) {
				System.err.println("CRASH!");
				pos[i] = 0.01;
				if (velo[i] < 0){
					velo[i] = -1 * velo[i];
				}
			}
		}
	}
	
	private static double[] generateInitialVelo(double[] pos){
		double[] result = new double[6];
		double dotTotal = 0;
		for (int i = 0; i < 5; i++){
			result[i] = .5 * (Math.random() - .5);
			dotTotal += (pos[i] - 1) * result[i];
		}
		result[5] = -1 * dotTotal / (pos[5] - 1);
		return result;
	}
	
	private static double[] generateRandom(){
		double[] result = new double[6];
		for (int i = 0; i < 6; i++){
			result[i] = Math.pow(Math.random(), 2) * Math.PI;
		}
		return result;
	}
	
	private static double[] actualizeVelocity(double[] pos, double[] velo){
		double[] newPos = new double[6];
		for(int i = 0; i < 6; i++){
			newPos[i] = pos[i] + velo[i] * ACTUALIZATION_CONSTANT;
		}
		return newPos;
	}
	
	private static double[] actualizeAcceleration(double[] velo, double[] accel){
		double[] newVelo = new double[6];
		for(int i = 0; i < 6; i++){
			newVelo[i] = velo[i] + accel[i] * ACTUALIZATION_CONSTANT;
		}
		return newVelo;
	}
	
	private static double generateForce(double d){
		return NATURAL_POINT - d;
	}
	
	private static double[] generateAccel(double[] pos){
		double[] result = new double[6];
		double[] random = new double[6];
		double dist = dist(pos);
		for (int i = 0; i < 6; i++){
			result[i] = generateForce(pos[i])/(dist*dist) + random[i];
		}
		return result;
	}

	public static double dist(double[] a){
		double result = 0;
		for (double d : a){
			result += d*d;
		}
		return Math.sqrt(result);
	}
	
	public static void main(String[] args){
		List<double[]> result = generate(100);
		for (double[] d : result){
			System.out.println(Arrays.toString(d) + " = " + dist(d));
		}
		
	}
	
}
