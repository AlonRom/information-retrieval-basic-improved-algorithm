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
import org.apache.lucene.document.Document;


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
	
	public static Map<Integer, String> ReadFileQueries(String inputFile, List<String> stopWords) 
	{
		String content = null;
		Map<Integer, String> queries = new HashMap<Integer, String>();
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
			String queryTerms = GetQueryTerms(part, stopWords);
			
		    System.out.println("Query Id " + queryId + " original query: " + part); 
			
			if(queryId > 0)
			{
				System.out.println("Query Id " + queryId + " after removing stop words, hyphenated,.. query: " + queryTerms); 
				queries.put(queryId, queryTerms);
			}
		}
		return queries;		
	}

	public static void SplitDocuments(String inputFile,String outPath)
	{
		String content = null;
		try
		{
			content = new String(Files.readAllBytes(Paths.get(inputFile)));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

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
			try {
				File file = new File(outPathID);
				file.createNewFile();
				BufferedWriter fileWriter = new BufferedWriter(new FileWriter(outPathID));
				fileWriter.write(part);
				fileWriter.close();
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	private static Integer GetNumberFromString(String str) 
	{
		str = str.replaceAll("\\D+","");
		if(str.matches("-?\\d+(\\.\\d+)?"))
			return Integer.parseInt(str.replaceAll("\\D+",""));
		
		return -1;
	}
	
	private static String GetQueryTerms(String str, List<String> stopWords) 
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
				   if(IfNotStopWord(word, stopWords))
					   listOfTerms.add(word);
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
