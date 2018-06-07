package Lucene;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextFileReader 
{
	public static List<String> ReadFileParametres(String inputFile) throws IOException 
	{
		List<String> lines = null;
		List<String> parameters = new ArrayList<>();
		try 
		{
			lines = Files.readAllLines(Paths.get(inputFile));
			
			//get parameters values
	        for (String line : lines) 
	        {
	        	parameters.add(line.split("=")[1]);	      
	        }
	        return parameters;
		} 
		catch (ArrayIndexOutOfBoundsException e) 
		{
			e.printStackTrace();
			throw e;
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			throw e;
		}		
	}
	
	public static int SplitDocuments(String inputFile,String outPath) throws Exception
	{
		try
		{
			String content = null;	
		    content = new String(Files.readAllBytes(Paths.get(inputFile)));	
			String[] parts = content.split("\\*TEXT");
			String outPathID=null;
			new File(outPath).mkdirs();
			for (String part : parts)
			{			
				if (part.equals(""))
					continue;
	
				//get doc Id
				Matcher matcher = Pattern.compile("\\d+").matcher(part);
				matcher.find();
				Integer docId = Integer.valueOf(matcher.group());
				outPathID=outPath.concat(Integer.toString(docId));
				outPathID=outPathID.concat(Constants.PARSED_DOCS_FILE_TYPE);
	
				//save the rest of the content to file
				try 
				{
					File file = new File(outPathID);
					file.createNewFile();
					BufferedWriter fileWriter = new BufferedWriter(new FileWriter(outPathID));
					
					/*Improved 1 - clean document punctuation and lower case all terms */				
					//retrieve terms for a document
					part = GetTextTerms(part, null);
					fileWriter.write(part);
					fileWriter.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
					throw e;
				}
			}
			return parts.length-1;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}	
	}
	
	public static Map<Integer, String> ReadFileQueries(String inputFile, List<String> stopWords) throws Exception 
	{
		try 
		{
			String content = null;
			Map<Integer, String> queries = new HashMap<Integer, String>();	
		    content = new String(Files.readAllBytes(Paths.get(inputFile)));			
			String[] parts = content.split("\\*");
			for (String part : parts) 
			{	
				if (part.equals(""))
					continue;
				//get query Id
				Integer queryId = GetNumberFromString(part);
				
				//retrieve terms for a query
				String queryTerms = GetTextTerms(part, stopWords);
				
			    System.out.println("Query Id " + queryId + " original query: " + part); 

				if(queryId > 0)
				{
					System.out.println("Query Id " + queryId + " after removing stop words, hyphenated,.. query: " + queryTerms); 
					queries.put(queryId, queryTerms);
				}
			}
			return queries;		
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}	
	}

	private static Integer GetNumberFromString(String str) 
	{
		Matcher matcher = Pattern.compile("\\d+").matcher(str);
		matcher.find();
		return Integer.valueOf(matcher.group());
	}
	
	private static String GetTextTerms(String str, List<String> stopWords) 
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
				//String[] wordsInLine = splitStr[i].replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
				String[] wordsInLine = splitStr[i].split("\\s+");
				//take each word in the line and add it only if it's not a stop word
				for (String word : wordsInLine)
				{
				   if(stopWords == null || IfNotStopWord(word, stopWords))
				   {
					   /*Improved 2 - Stemming document */
					   //word = LuceneHelper.StemTerm(word);
					   listOfTerms.add(word);					   
				   }
				}	
			}	
		}
		
		return String.join(" ", listOfTerms);
	}

	private static boolean IfNotStopWord(String word, List<String> stopWords) 
	{
		for(String str: stopWords) {
		    if(str.trim().toLowerCase().contains(word))
		       return false;
		}
		return true;
	}

}
