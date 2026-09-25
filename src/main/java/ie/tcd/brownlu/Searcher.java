package ie.tcd.brownlu;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
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


public class Searcher {

	static int queryCount=0; //for numbering the queries to match
	static QueryParser parser;
	static IndexSearcher searcher;

	public static void main(String[] args) throws Exception {
		//point @ index folder & open the index
		Directory directory=FSDirectory.open(Paths.get("index"));	
		DirectoryReader indexReader=DirectoryReader.open(directory);
		System.out.println("Documents in index: "+indexReader.numDocs());
		searcher=new IndexSearcher(indexReader); //run queries against the index
		searcher.setSimilarity(new BM25Similarity());		//scoring model. play around with this later


		Analyzer analyzer=new StandardAnalyzer(); //play around w/ different analysers when testing precision/recall
		parser=new MultiFieldQueryParser(new String[] {"title","text"},analyzer); //use analyser to put query into lucene-readable format

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
		indexReader.close();
		directory.close();
	}
	//handle the queries and some output tests
	static void handleQuery(int queryId, String text) throws Exception {
		Query query=parser.parse(QueryParser.escape(text));
		if(queryId==1 || queryId==3 || queryId==225) {
			System.out.println("Query "+queryId+": "+text);
			System.out.println("Lucene query: "+query); //output lucene-readable query to test which analyser is in use		
			
			//top 3 hits
			ScoreDoc[] hits=searcher.search(query,3).scoreDocs;
			for(int i=0;i<hits.length;i++) {
				String docId=searcher.storedFields().document(hits[i].doc).get("id");
				System.out.println(" Rank "+(i+1)+": doc "+docId+" (score "+hits[i].score+")");
			}
			System.out.println();
		}
	}
}
