package Lucene;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;


public class TextFileReader {
	
	public static List<String> ReadFileParametres(String inputFile) 
	{
		List<String> lines = null;
		List<String> parameters = new ArrayList<>();
		try 
		{
			lines = Files.readAllLines(Paths.get(inputFile));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		//get parameters values
        for (String line : lines) 
        {
        	parameters.add(line.split("=")[1]);	      
        }

        return parameters;
	}
	
	public static List<Document> CreateCollection(String inputFile) 
	{		
		return null;
	}
	
	
	public static List<String> GetStopWords(List<Document> collection, int numberOfWords) 
	{	
		return null;
	}

	public static Map<Integer, List<String>> ReadFileQueries(String inputFile) 
	{
		String content = null;
		Map<Integer, List<String>> queries = new HashMap<Integer, List<String>>();
		try 
		{
			content = new String(Files.readAllBytes(Paths.get(inputFile)));
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	
		String[] parts = content.split("\\*");
		for (String part : parts) 
		{		
			//get query Id
			Integer queryId = GetNumberFromString(part);
			
			//retrieve terms for a query
			List<String> queryTerms = GetQueryTerms(part);
			
			if(queryId > 0)
				queries.put(queryId, queryTerms);					
		}
		return queries;		
	}

	private static Integer GetNumberFromString(String str) 
	{
		str = str.replaceAll("\\D+","");
		if(str.matches("-?\\d+(\\.\\d+)?"))
			return Integer.parseInt(str.replaceAll("\\D+",""));
		
		return -1;
	}
	
	private static List<String> GetQueryTerms(String str) 
	{
		List<String> listOfTerms = new ArrayList<String>(); 
		//split string in order to take the query lines 
		String splitStr[] =  str.split("[\\r\\n]+");
		
		if(splitStr.length > 1)
		{		
			//the query can have more then 1 line so we need to iterate each line 
			for(int i=1; i < splitStr.length; i++)
			{
				//removes all non-letter characters, folds to lower case, then splits the input, Spaces are initially left in the input 
				String[] wordsInLine = splitStr[i].replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
				//take each word in the line and add it only if it's not a stop word
				for (String word : wordsInLine)
				{
				   if(IfNotStopWord(word))
					   listOfTerms.add(word);
				}	
			}	
		}
		
		return listOfTerms;
	}

	private static boolean IfNotStopWord(String word) 
	{
		return true;
	}
}
