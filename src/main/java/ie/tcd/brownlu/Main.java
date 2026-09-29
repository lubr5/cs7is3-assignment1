// TODO:
// C̶r̶e̶a̶t̶e̶ ̶m̶a̶i̶n̶.̶j̶a̶v̶a̶ ̶a̶n̶d̶ ̶h̶a̶v̶e̶ ̶i̶t̶ ̶i̶n̶v̶o̶k̶e̶ ̶I̶n̶d̶e̶x̶e̶r̶.̶j̶a̶v̶a̶ ̶&̶ ̶S̶e̶a̶r̶c̶h̶e̶r̶.̶j̶a̶v̶a̶ ̶t̶o̶ ̶a̶v̶o̶i̶d̶ ̶t̶h̶e̶ ̶n̶e̶e̶d̶ ̶t̶o̶ ̶r̶u̶n̶ ̶t̶w̶o̶ ̶c̶o̶m̶m̶a̶n̶d̶s̶
// A̶l̶l̶o̶w̶ ̶t̶h̶e̶ ̶u̶s̶e̶r̶ ̶t̶o̶ ̶s̶e̶l̶e̶c̶t̶ ̶w̶h̶i̶c̶h̶ ̶a̶n̶a̶l̶y̶s̶e̶r̶ ̶t̶h̶e̶y̶ ̶w̶a̶n̶t̶ ̶t̶o̶ ̶u̶s̶e̶
// A̶l̶l̶o̶w̶ ̶t̶h̶e̶ ̶u̶s̶e̶r̶ ̶t̶o̶ ̶s̶e̶l̶e̶c̶t̶ ̶w̶h̶i̶c̶h̶ ̶s̶c̶o̶r̶e̶r̶ ̶t̶h̶e̶y̶ ̶w̶a̶n̶t̶ ̶t̶o̶ ̶u̶s̶e̶
// Allow the user to select if they want to run the given queries or perform an interactive search
// Have the program run as a loop, so that the same instance can handle different analyser/scorer/querying setups
// Allow the user to quit the program

package ie.tcd.brownlu;

import java.util.Scanner;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;

public class Main {

	public static void main(String[] args) throws Exception {
		Analyzer analyzer;
		String analyzerName;
		Similarity similarity;
		String scorerName;

		Scanner input=new Scanner(System.in);

		while(true) { //outer loop: lets the user change setup without restarting
			//choose analyser
			while(true) {
				System.out.println("Choose an analyser and hit Enter:");
				System.out.println("  1 = Standard");
				System.out.println("  2 = English");
				System.out.println("  3 = Whitespace");
				System.out.println("  4 = More detail...");
				System.out.println("  0 = Exit");
				System.out.print("> ");
				String choice=input.nextLine().trim();

				if(choice.equals("1")) {
					analyzer=new StandardAnalyzer();
					analyzerName="standard";
					break;
				}
				else if(choice.equals("2")) {
					analyzer=new EnglishAnalyzer();
					analyzerName="english";
					break;
				}
				else if(choice.equals("3")) {
					analyzer=new WhitespaceAnalyzer();
					analyzerName="whitespace";
					break;
				}
				else if(choice.equals("4")) {
					System.out.println();
					System.out.println("Standard:   splits text into words, removes punctuation, lowercases.Keeps common words (the, of, what)");
					System.out.println("English:    Standard, plus removes common English word and cuts words to their root");
					System.out.println("Whitespace: splits only at spaces. Keeps punctuation and capitals,");
					System.out.println();
				}
				else if(choice.equals("0")) {
					System.out.println("Exiting.");
					return;
				}
				else {
					System.out.println("Invalid choice: "+choice);
				}
			}

			//choose scorer
			while(true) {
				System.out.println("Choose a scorer and hit Enter:");
				System.out.println("  1 = BM25");
				System.out.println("  2 = VSM (Vector Space Model)");
				System.out.println("  3 = Boolean");
				System.out.println("  4 = More detail...");
				System.out.println("  0 = Exit");
				System.out.print("> ");
				String choice=input.nextLine().trim();

				if(choice.equals("1")) {
					//float k1 =  1.8f;
					//float b = 0.75f;	//changed parameters as a test
					similarity=new BM25Similarity(); //k1,b);
					scorerName="bm25";
					break;
				}
				else if(choice.equals("2")) {
					similarity=new ClassicSimilarity();
					scorerName="vsm";
					break;
				}
				else if(choice.equals("3")) {
					similarity=new BooleanSimilarity();
					scorerName="boolean";
					break;
				}
				else if(choice.equals("4")) {
					System.out.println();
					System.out.println("BM25:         rewards docs that use the query words often. Rare words count more");
					System.out.println("VSM:          docs and queries as vectors of word weights, ranked by how closely they point in the same direction.");
					System.out.println("Boolean:      a doc either contains a query word or not. Ignores how often words appear");
					System.out.println();
				}
				else if(choice.equals("0")) {
					System.out.println("Exiting.");
					return;
				}
				else {
					System.out.println("Invalid choice: "+choice);
				}
			}

			String runName=analyzerName+"_"+scorerName;

			Indexer.run(analyzer);

			while(true){
				System.out.println();
				System.out.println("-- CURRENT SETUP: "+analyzerName+", "+scorerName+" --");
				System.out.println("  1 = Run Cranfield queries");
				System.out.println("  2 = Interactive search TODO");
				System.out.println("  3 = Change analyser/scorer");
				System.out.println("  0 = Exit");
				System.out.print("> ");
				String choice=input.nextLine().trim();

				if(choice.equals("1")) {
					Searcher.run(analyzer,similarity,runName);
				}
				else if(choice.equals("2")) {
					Searcher.interactive(analyzer,similarity,input);
				}
				else if(choice.equals("3")) {
					break; //back to choosing analyser & scorer
				}
				else if(choice.equals("0")) {
					System.out.println("Exiting.");
					return;
				}
				else {
					System.out.println("Invalid choice: "+choice);
				}

			}
		}
	}
}
