package grady.common;
public class Frame implements Runnable{

	private int repetitions;
	int resolution;
	
	private double a;
	private double b;
	private double c;
	private double d;
	private double e;
	private double f;
	
	private int[][] counts;
	
	public static Frame create(double[] vars){
		return new Frame(vars[0],vars[1],vars[2],vars[3],vars[4],vars[5]);
	}
	
	public Frame(double a, double b, double c, double d, double e, double f){
		this.resolution = 1000;
		this.repetitions = 1000000;
		
		this.a = a; this.b = b; this.c = c; this.d = d; this.e = e; this.f = f;
		
		this.counts = new int[resolution][resolution];
	}
	
	
	public Frame withResolution(int resolution){
		this.resolution = resolution;
		return this;
	}
	
	public Frame withRepetitions(int repetitions){
		this.repetitions = repetitions;
		return this;
	}
	
	@Override
	public void run() {
		this.execute(this.repetitions);
	}

	private void execute(int repetitions){
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

	private void increment(double x, double y, double z){
		int ix = (int) Math.floor((x + 3) / 6 * (resolution - 1));
		int iix = (int) Math.floor((y + 3) / 6 * (resolution - 1));
		//int iiix = (int) Math.floor((z + 3) / 6 * (resolution - 1));
		this.counts[ix][iix]++;
	}

	private double xTransform(double x, double y, double z){
		return Math.cos(b * x) + Math.sin(a * y) - Math.cos(c * z);
	}
	
	private double yTransform(double x, double y, double z){
		return Math.sin(d * x) + Math.cos(e * y) - Math.cos(f * z);
	}
	
	private double zTransform(double x, double y, double z){
		return z + .01;
	}
	
	public int[][] getCounts(){
		return this.counts;
	}

	public int getResolution() {
		return this.resolution;
	}

	public int getMaxCount() {
		int max = 0;
		for (int i = 0; i < counts.length; i++){
			for (int j = 0; j < counts[0].length; j++){
				max = Math.max(max, counts[i][j]);
			}
		}
		return max;
	}
	
	public int getNumBytes(){
		int total =  this.resolution * this.resolution * 4;
		total += 2 * 8;
		total += 6 * 4;
		return total;
	}
	
}