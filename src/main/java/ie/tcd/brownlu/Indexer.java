package ie.tcd.brownlu;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer {

	static int documentCount=0;
	static int empties=0; //for debugging. there are 2 expected to be empty.
	static IndexWriter writer;

	public static void main(String[] args) throws Exception {
		Analyzer analyzer=new StandardAnalyzer(); //play around w/ different analysers when testing precision/recall
		Directory directory=FSDirectory.open(Paths.get("index"));
		IndexWriterConfig config=new IndexWriterConfig(analyzer); //use current analyser. Might make this loop thru different analysers later stage
		config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);    //fresh index each run
		writer=new IndexWriter(directory,config);

		// Read Docs from data/
		BufferedReader reader=Files.newBufferedReader(Paths.get("data/cran.all.1400"));

		String id=null;
		String section="";
		StringBuilder title=new StringBuilder();
		StringBuilder text=new StringBuilder();

		String line;
		while((line=reader.readLine())!=null) {
			if(line.startsWith(".I")) { //by ID because not all docs have author/bibliography/etc.
				if(id!=null) {
					handleDocument(id,title.toString().trim(),text.toString().trim());
				}
				id=line.substring(2).trim(); //remove .I notation
				section="";
				title.setLength(0);
				text.setLength(0);
			}
			else if(line.equals(".T") || line.equals(".A") || line.equals(".B") || line.equals(".W")) {
				section=line;
			}
			else if(section.equals(".T")) {
				title.append(line).append(" ");
			}
			else if(section.equals(".W")) {
				text.append(line).append(" ");
			}
		}
		if(id!=null) {
			handleDocument(id,title.toString().trim(),text.toString().trim());
		}
		reader.close();
		System.out.println("Documents found: "+documentCount);
		System.out.println("Documents with empty text: "+empties);
		System.out.println("Documents in index: "+writer.getDocStats().numDocs);
		writer.close();
		directory.close();
	}

	static void handleDocument(String id, String title, String text) throws Exception {
		documentCount++;
		if(text.isEmpty()) {
			empties++; //should always result to 2
		}
		if(id.equals("1") || id.equals("184") || id.equals("1400")) {
			System.out.println("ID:    "+id);
			System.out.println("Title: "+title); //test prints for some random docs
			System.out.println("Text:  "+text.substring(0,Math.min(80,text.length()))+"...");
			System.out.println();
		}
		
		//add doc to the index. id for results. text and title for search
		Document doc=new Document();
		doc.add(new StringField("id",id,Field.Store.YES));
		doc.add(new TextField("title",title,Field.Store.YES));
		doc.add(new TextField("text",text,Field.Store.YES));
		writer.addDocument(doc);
	}
}
