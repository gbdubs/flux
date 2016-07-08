import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RandomCatName {
	private static List<String> catWords;
	
	static {
		Scanner scan;
		try {
			scan = new Scanner(new File("catwords.txt"));
			catWords = new ArrayList<String>();
			while(scan.hasNextLine()){
				catWords.add(scan.nextLine().trim());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static String generate(){
		int index = (int) Math.floor(Math.random() * catWords.size());
		return catWords.get(index);
	}
}
