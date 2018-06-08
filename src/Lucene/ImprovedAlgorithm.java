package Lucene;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class ImprovedAlgorithm extends BaseAlgorithm
{
	public ImprovedAlgorithm(String docsFilePath, String queryFilePath, String outputFilePath)
	{
		_docsFilePath = docsFilePath;
		_queryFilePath = queryFilePath;
		_outputFilePath	= outputFilePath;
	}
	
	public void Execute() 
	{		
	    try 
	    {
			//get 20 words that appear most frequently in the collection 	
	    	_stopWordsSet = LuceneHelper.GetMostFrequentWords(_docsFilePath, _stopWords);
		} 
	    catch (Exception e) 
	    {
	       System.out.println("Failed to complete Stop Words retrieval process!");
	       System.out.println("Exiting Retrieval Experiment...");
		   System.exit(1);
		}
	   
	    try 
	    {
			//split the document into it's sub documents
		    _numberOfDocs = TextFileReader.SplitDocuments(_docsFilePath, Constants.PARSED_DOCS_PATH);
	    }
	    catch (Exception e) 
	    {
	       System.out.println("Failed to split text document to separate files!");
	       System.out.println("Exiting Retrieval Experiment...");
		   System.exit(1);
		}
	    
	    try 
	    {
	    	MySynonym mySynonym = new MySynonym();
			mySynonym.UseDefaultSynonyms();
			mySynonym.BuildMyMap();
			mySynonym.UseDefaultPhrases();
			CharArraySet phrases = mySynonym.getPhrasesSet();
			SynonymMap map = mySynonym.getMyMap();
			MyCustomAnalyzer customAnalyzer = new MyCustomAnalyzer(map, _stopWordsSet, phrases);

			//create a default Similarity
		    _similarity = new ClassicSimilarity();
		    
			Directory documentsIndexdirectory = FSDirectory.open(Paths.get(Constants.DOCUMENTS_INDEX_PATH));
			IndexWriterConfig documentsConfig = new IndexWriterConfig(customAnalyzer);
			documentsConfig.setOpenMode(OpenMode.CREATE);
			documentsConfig.setSimilarity(_similarity);

			IndexWriter documentsWriter = new IndexWriter(documentsIndexdirectory, documentsConfig);
			LuceneHelper.IndexSplittedDocuments(documentsWriter, Constants.PARSED_DOCS_PATH, Constants.PARSED_DOCS_FILE_TYPE, _numberOfDocs);
		
			documentsWriter.close();	    	
	    }
	    catch (Exception e) 
	    {
	       System.out.println("Failed to index documents!");
	       System.out.println("Exiting Retrieval Experiment...");
		   System.exit(1);
		}
	    	  
	    try
	    {
			//parse queries from the query file
		    _queries = TextFileReader.ReadFileQueries(_queryFilePath, _stopWords);
	    }
	    catch (Exception e) 
	    {
	       System.out.println("Failed to parse queries from query file!");
	       System.out.println("Exiting Retrieval Experiment...");
		   System.exit(1);
		}	  
	    
	    try
	    {
	    	//search queries
	    	_searchQueriesResult = LuceneHelper.SearchIndexForQueries(_queries, _stopWordsSet, _outputFilePath, _similarity);
	    }
	    catch (Exception e) 
	    {
	       System.out.println("Failed to search queries!");
	       System.out.println("Exiting Retrieval Experiment...");
		   System.exit(1);
		}	
	        
	    try
	    {
	    	//rank queries
	    	if (Constants.SYSTEM_RANKING) 
			{
				SystemRanking rank = new SystemRanking(_searchQueriesResult, Constants.TRUTH_PATH);
				//Print to the console the relevant information
				rank.printSystemRanking();
			}	
	    }
	    catch (Exception e) 
	    {
	       System.out.println("Failed to rank queries!");
	       System.out.println("Exiting Retrieval Experiment...");
		   System.exit(1);
		}		    
	}
}
