package Lucene;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextFileReader {
	
	public static List<String> ReadFileParametres(String inputFile) 
	{
		List<String> lines = null;
		List<String> parameters = new ArrayList<>();
		try {
			lines = Files.readAllLines(Paths.get(inputFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		   StringBuilder sb = new StringBuilder();
	        for (String line : lines) {
	        	parameters.add(line.split("=")[1]);	      
	        }

        return parameters;
	}
	
	public static Map<Integer, List<String>> ReadFileQueries(String inputFile) 
	{
		String content = null;
		Map<Integer, List<String>> queries = new HashMap<Integer, List<String>>();
		try {
			content = new String(Files.readAllBytes(Paths.get(inputFile)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		String[] parts = content.split(".");

		return null;
	}

}
