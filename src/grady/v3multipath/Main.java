package grady.v3multipath;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Main {

	public static void main(String[] args) throws FileNotFoundException, InterruptedException, IOException{
		///*
		//Stitcher.createRawOptions(6, 3000000, 600);
		//*/
		for (int i = 0; i < 10; i++){
			List<Integer> nums = getRandomWOReplacement(5, 3);
			
			Map<String, Integer> name2Color = new HashMap<String, Integer>();
			name2Color.put("explicated" + nums.get(0), Stitcher.toRGB(235, 94, 85));
			name2Color.put("explicated" + nums.get(1), Stitcher.toRGB(255, 212, 68));
			name2Color.put("explicated" + nums.get(2), Stitcher.toRGB(53, 144, 243));
			Stitcher.make(name2Color, "EXP" + i);
		}
		//*/
		
	}
	
	public static List<Integer> getRandomWOReplacement(int max, int n){
		Set<Integer> result = new HashSet<Integer>();
		while (result.size() < n){
			int possibility = (int) Math.floor(Math.random() * (max + 1));
			result.add(possibility);
		}
		return new ArrayList<Integer>(result);
	}
	
}
