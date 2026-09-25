package ie.tcd.brownlu;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Searcher {

	static int queryCount=0; //for numbering the queries to match

	public static void main(String[] args) throws Exception {
		//read the test questions
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
	}
	//handle the queries and some output tests
	static void handleQuery(int queryId, String text) {
		if(queryId==1 || queryId==3 || queryId==225) {
			System.out.println("Query "+queryId+": "+text);
			System.out.println();
		}
	}
}
