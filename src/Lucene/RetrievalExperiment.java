package Lucene;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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

		ClassicSimilarity similarity=new ClassicSimilarity();
	    try 
	    {
	    	stopWordsSet = LuceneHelper.GetMostFrequentWords(reader, stopWords);
		} 
	    catch (Exception e) 
	    {
			e.printStackTrace();
		}
	    
		//Split the big document into it's sub-documents and index each one of them
		try
		{
			//Split the document into it's sub documents
			int numberOfDocs = TextFileReader.SplitDocuments(docsFilePath,Constants.PARSED_DOCS_PATH);
						
			//create a writer that indexes each document separately!    
			StandardAnalyzer documentsStandardAnalyzer = new StandardAnalyzer(stopWordsSet);
			Directory documentsIndexdirectory = FSDirectory.open(Paths.get(Constants.DOCUMENTS_INDEX_PATH));
			IndexWriterConfig documentsConfig = new IndexWriterConfig(documentsStandardAnalyzer);
			documentsConfig.setOpenMode(OpenMode.CREATE);
			documentsConfig.setSimilarity(similarity);
			IndexWriter documentsWriter = new IndexWriter(documentsIndexdirectory, documentsConfig);

			for (Integer i=0;i<numberOfDocs;i++){
				String path=Constants.PARSED_DOCS_PATH.concat(Integer.toString(i+1));
				path=path.concat(Constants.PARSED_DOCS_FILE_TYPE);
				LuceneHelper.IndexDocument(documentsWriter, path);
			}
			documentsWriter.close();


		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		//get queries from the query file
		queries = TextFileReader.ReadFileQueries(queryFilePath, stopWords);

		//search queries
		Map<Integer, Integer[]> result=new HashMap<Integer, Integer[]>();
		result=LuceneHelper.SearchIndexForQueries(queries, stopWordsSet, outputFilePath,similarity);
		if (Constants.SYSTEM_RANKING) {
			//for each query the number of *relevant* items that was retrieved by the system
			long[] relvantRetrieved = SystemRanking.numRelevantItemsRetrieved(result, Constants.TRUTH_PATH);

			//for each query the number of true relevant items from the whole collection
			long[] relevant=SystemRanking.numRelevantItems(Constants.TRUTH_PATH,Integer.valueOf(result.size()));

			//for each query the number of items that was retrieved by the system
			long[] retrieved = SystemRanking.numItemsRetrieved(result);

			//for each query the precision of our system -  (relevant items retrieve)/(retrieved items)
			float[] precision=SystemRanking.precision(relvantRetrieved,retrieved);

			//for each query the recall of our system - (relevant items retrieve)/relevant
			float[] recall=SystemRanking.recall(relvantRetrieved,relevant);

			//print the average precision and recall
			System.out.println("The average precision: "+ SystemRanking.average(precision));
			System.out.println("The average recall: "+ SystemRanking.average(recall));


		}


				
	}		
}
