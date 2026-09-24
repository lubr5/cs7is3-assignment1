package ie.tcd.brownlu;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Searcher {
	static int queryCount=0;

	public static void main(String[] args) throws Exception {
		//TODO: read the test questions
		//TODO: number the test questions
		BufferedReader reader=Files.newBufferedReader(Paths.get("data/cran.qry"));
	}
}
