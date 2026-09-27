package ie.tcd.brownlu;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.PrintWriter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.search.similarities.Similarity;

public class Searcher {

	static int queryCount=0; //for numbering the queries to match
	static QueryParser parser;
	static IndexSearcher searcher;
	static String runName; //set by the run in main e.g. standard_bm25
	static PrintWriter results;
	static int maxResults=1000;
	

	//for testing Searcher on its own. Main.java calls run() directly
	public static void main(String[] args) throws Exception {
		run(new StandardAnalyzer(),new BM25Similarity(),"standard_bm25");
	}

	public static void run(Analyzer analyzer, Similarity similarity, String name) throws Exception {
		queryCount=0; //reset to 0 in case main does another run
		runName=name;
		//point @ index folder & open the index
		Directory directory=FSDirectory.open(Paths.get("index"));	
		DirectoryReader indexReader=DirectoryReader.open(directory);
		System.out.println("Documents in index: "+indexReader.numDocs());
		searcher=new IndexSearcher(indexReader); //run queries against the index;
		searcher.setSimilarity(similarity);	//scoring model passed from main
		parser=new MultiFieldQueryParser(new String[] {"title","text"},analyzer); //use analyser to put query into lucene-readable format

		//create results file
		Files.createDirectories(Paths.get("results"));
		results=new PrintWriter(Files.newBufferedWriter(Paths.get("results/"+runName+".txt"))); //not hard-coded , run setup will change

		//read test questions
		BufferedReader reader=Files.newBufferedReader(Paths.get("data/cran.qry"));
		
		String section="";
		StringBuilder text=new StringBuilder();

		String line;
		while((line=reader.readLine())!=null) {
			if(line.startsWith(".I")) { //starts new query, use queryCount, not the given number
				if(queryCount>0) {
					handleQuery(queryCount,text.toString().trim());
				}
				queryCount++;
				section="";
				text.setLength(0);
			}
			else if(line.equals(".W")) {
				section=line;
			}
			else if(section.equals(".W")) {
				text.append(line).append(" ");
			}
		}
		if(queryCount>0) {
			handleQuery(queryCount,text.toString().trim());
		}
		reader.close();
		System.out.println("Queries found: "+queryCount);
		System.out.println("Results written to results/"+runName+".txt");
		indexReader.close();
		directory.close();
		results.close();
	}
	//handle the queries and output into a results file
	static void handleQuery(int queryId, String text) throws Exception {
		Query query=parser.parse(QueryParser.escape(text));
		//Write all hits for all queries into results file
		//TODO: allow user to select what maxResults is
		ScoreDoc[] hits=searcher.search(query,maxResults).scoreDocs;
		for(int i=0;i<hits.length;i++) {
			String docId=searcher.storedFields().document(hits[i].doc).get("id");
			results.println(queryId+" Q0 "+docId+" "+(i+1)+" "+hits[i].score+" "+runName); //written to results, not shell
		}
		
		// Test output on 3 random queries, top 3 hits only
		if(queryId==1 || queryId==3 || queryId==225) {
			System.out.println("Query "+queryId+": "+text);
			System.out.println("Lucene query: "+query); //output lucene-readable query to test which analyser is in use
			for(int i=0;i<3 && i<hits.length;i++) {
				String docId=searcher.storedFields().document(hits[i].doc).get("id");
				System.out.println(" Rank "+(i+1)+": doc "+docId+" (score "+hits[i].score+")"); //write top 3 to shell for testing
				System.out.println();
			}
		}
	}
}
