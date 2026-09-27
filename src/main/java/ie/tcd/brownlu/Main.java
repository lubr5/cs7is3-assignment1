// TODO:
// ̶C̶r̶e̶a̶t̶e̶ ̶m̶a̶i̶n̶.̶j̶a̶v̶a̶ ̶a̶n̶d̶ ̶h̶a̶v̶e̶ ̶i̶t̶ ̶i̶n̶v̶o̶k̶e̶ ̶I̶n̶d̶e̶x̶e̶r̶.̶j̶a̶v̶a̶ ̶&̶ ̶S̶e̶a̶r̶c̶h̶e̶r̶.̶j̶a̶v̶a̶ ̶t̶o̶ ̶a̶v̶o̶i̶d̶ ̶t̶h̶e̶ ̶n̶e̶e̶d̶ ̶t̶o̶ ̶r̶u̶n̶ ̶t̶w̶o̶ ̶c̶o̶m̶m̶a̶n̶d̶s̶
// Allow the user to select which analyser they want to use
// Allow the user to select which scorer they want to use
// Allow the user to select if they want to run the given queries or perform an interactive search
// Have the program run as a loop, so that the same instance can handle different analyser/scorer/querying setups
// Allow the user to quit the program

package ie.tcd.brownlu;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;

public class Main {

	public static void main(String[] args) throws Exception {
		Analyzer analyzer=new StandardAnalyzer();
		Similarity similarity=new BM25Similarity();
		String runName="standard_bm25";

		Indexer.run(analyzer);
		Searcher.run(analyzer,similarity,runName);
	}
}
