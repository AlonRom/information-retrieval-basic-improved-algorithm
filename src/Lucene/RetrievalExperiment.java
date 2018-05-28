package Lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.TermStats;

public class RetrievalExperiment {

	private static String docsFilePath;
	private static String queryFilePath;
	private static String retrievalAlgorithmMode;
	private static String outputFilePath;
	
	static List<String> inputParameters;
	static List<Document> collection;
	static List<String> stopWords;
	static Map<Integer, List<String>> queries;

	public static void main(String[] args) throws IOException, ParseException {
		
		//get parameters file data
		String inputFilePath = "./files/parameters.txt";
		inputParameters= TextFileReader.ReadFileParametres(inputFilePath);
		queryFilePath = inputParameters.get(0);
		docsFilePath = inputParameters.get(1);
		outputFilePath = inputParameters.get(2);
		retrievalAlgorithmMode = inputParameters.get(3);
			
		//Create new index
		StandardAnalyzer standardAnalyzer = new StandardAnalyzer();
		Directory directory = FSDirectory.open(Paths.get(outputFilePath.replaceFirst("[.][^.]+$", "")));
		IndexWriterConfig config = new IndexWriterConfig(standardAnalyzer);
		config.setOpenMode(OpenMode.CREATE);
		
		//Create a writer
		IndexWriter writer = new IndexWriter(directory, config);

		//Create a new document
		try 
		{
			Document document = LuceneHelper.CreateDocument(writer, docsFilePath);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		//Open index reader
		IndexReader reader = DirectoryReader.open(directory);
		
		//get 20 words that appear most frequently in the collection 	
		TermStats[] states = null;
	    try 
	    {
	    	states = LuceneHelper.GetMostFrequentWords(reader);
		} 
	    catch (Exception e) 
	    {
			e.printStackTrace();
		}

		//get queries from the query file
		queries = TextFileReader.ReadFileQueries(queryFilePath);
			
	
	}		
}
