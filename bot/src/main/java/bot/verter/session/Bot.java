package bot.verter.session;

import it.uniroma1.lcl.adw.ADW;
import it.uniroma1.lcl.adw.DisambiguationMethod;
import it.uniroma1.lcl.adw.ItemType;
import it.uniroma1.lcl.adw.comparison.SignatureComparison;
import it.uniroma1.lcl.adw.comparison.WeightedOverlap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Bot {

	public static void main(String argv[]) throws IOException {
		BufferedReader input = new BufferedReader(new InputStreamReader(
				System.in, "utf-8"));
		
		
		String text1 = input.readLine();

		while (text1 != null) {

			//the two lexical items to be compared  
			String text2 = input.readLine();

			//types of the two lexical items
			ItemType srcTextType = ItemType.SURFACE;  
			ItemType trgTextType = ItemType.SURFACE;

			//if lexical items has to be disambiguated
			DisambiguationMethod disMethod = DisambiguationMethod.ALIGNMENT_BASED;      

			//measure for comparing semantic signatures
			SignatureComparison measure = new WeightedOverlap(); 

			ADW pipeLine = new ADW();

			double similarity = pipeLine.getPairSimilarity(text1, text2,
			                          disMethod, measure,
			                          srcTextType, trgTextType); 
			System.out.println(similarity);

			text1 = input.readLine();
		}
	}
}
