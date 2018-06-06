package Lucene;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;




public class RetrievalExperiment {

	private static String docsFilePath;
	private static String queryFilePath;
	private static String retrievalAlgorithmMode;
	private static String outputFilePath;
	
	static List<String> inputParameters;
	static List<String> stopWords = new ArrayList<String>();
	static Map<Integer, String> queries;

	public static void main(String[] args) throws IOException, ParseException {

		//get parameters file data
		String inputFilePath = "./files/parameters.txt";
		inputParameters= TextFileReader.ReadFileParametres(inputFilePath);
		queryFilePath = inputParameters.get(0);
		docsFilePath = inputParameters.get(1);
		outputFilePath = inputParameters.get(2);
		retrievalAlgorithmMode = inputParameters.get(3);
			
		//create new index
		CharArraySet emptyStopWord = null;
		StandardAnalyzer standardAnalyzer = new StandardAnalyzer(emptyStopWord);
		Directory docsFileIndexdirectory = FSDirectory.open(Paths.get(Constants.DOCS_FILE_INDEX_PATH));
		IndexWriterConfig docsFileConfig = new IndexWriterConfig(standardAnalyzer);
		docsFileConfig.setOpenMode(OpenMode.CREATE);
		
		//create a writer for finding the stop words
		IndexWriter docFileWriter = new IndexWriter(docsFileIndexdirectory, docsFileConfig);

		//index the doc's file
		try 
		{
			LuceneHelper.IndexDocument(docFileWriter, docsFilePath);
			docFileWriter.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		//Open index reader
		IndexReader reader = DirectoryReader.open(docsFileIndexdirectory);
		
		//get 20 words that appear most frequently in the collection 	
		CharArraySet stopWordsSet = null;

		//create a default Similarity
		ClassicSimilarity similarity=new ClassicSimilarity();
	    try 
	    {
	    	stopWordsSet = LuceneHelper.GetMostFrequentWords(reader, stopWords);
		} 
	    catch (Exception e) 
	    {
			e.printStackTrace();
		}
	    
		//Split the document into it's sub documents
		int numberOfDocs = TextFileReader.SplitDocuments(docsFilePath,Constants.PARSED_DOCS_PATH);
		
		//create a writer that indexes each document separately!
		StandardAnalyzer documentsStandardAnalyzer = new StandardAnalyzer(stopWordsSet);
		MySynonym mySynonym = new MySynonym();
		mySynonym.UseDefaultSynonyms();
		mySynonym.BuildMyMap();
		mySynonym.UseDefaultPhrases();
		CharArraySet phrases = mySynonym.getPhrasesSet();
		SynonymMap map = mySynonym.getMyMap();
		MyCustomAnalyzer ana = new MyCustomAnalyzer(map,stopWordsSet,phrases);

		Directory documentsIndexdirectory = FSDirectory.open(Paths.get(Constants.DOCUMENTS_INDEX_PATH));
		IndexWriterConfig documentsConfig = new IndexWriterConfig(ana);
		//IndexWriterConfig documentsConfig = new IndexWriterConfig(documentsStandardAnalyzer);
		documentsConfig.setOpenMode(OpenMode.CREATE);
		documentsConfig.setSimilarity(similarity);

		IndexWriter documentsWriter = new IndexWriter(documentsIndexdirectory, documentsConfig);
		LuceneHelper.IndexSplittedDocuments(documentsWriter, Constants.PARSED_DOCS_PATH, Constants.PARSED_DOCS_FILE_TYPE, numberOfDocs);
	
		documentsWriter.close();

		//get queries from the query file
		queries = TextFileReader.ReadFileQueries(queryFilePath, stopWords);

		//search queries
		Map<Integer, Integer[]> result = LuceneHelper.SearchIndexForQueries(queries, stopWordsSet, outputFilePath,similarity);

		//System ranking
		if (Constants.SYSTEM_RANKING) 
		{
			SystemRanking rank = new SystemRanking(result,Constants.TRUTH_PATH);
			//Print to the console the relevant information
			rank.printSystemRanking();
		}

	}
}
