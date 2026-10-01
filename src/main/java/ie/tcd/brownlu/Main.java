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
import org.apache.lucene.analysis.custom.CustomAnalyzer;

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
				System.out.println("  4 = Lucia's Custom Analyser");
				System.out.println("  5 = More detail...");
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
					analyzer=CustomAnalyzer.builder()
						.withTokenizer("standard")		//Standard analyser
						.addTokenFilter("englishPossessive")	//Remove 's from words
						.addTokenFilter("lowercase")		//Lowercase all words
						.addTokenFilter("stop","words","org/apache/lucene/analysis/snowball/english_stop.txt","format","snowball") //block 174 common words
						.addTokenFilter("porterStem") 		//cut words to the root 
						.build();
					analyzerName="custom";
					break;
				}
				else if(choice.equals("5")) {
					System.out.println();
					System.out.println("Standard:      splits text into words, removes punctuation, lowercases.Keeps common words (the, of, what)");
					System.out.println("English:       standard, plus removes common English word and cuts words to their root");
					System.out.println("Whitespace:    splits only at spaces. Keeps punctuation and capitals");
					System.out.println("Custom:	   English, but cuts a larger number of common words");
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
				System.out.println("  2 = Interactive search");
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
