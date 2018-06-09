package Lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.lucene.queryparser.classic.ParseException;

public class RetrievalExperiment 
{
	static String _inputFilePath;
	static List<String> _inputParameters;
	static String _queryFilePath;
	static String _docsFilePath;
	static String _outputFilePath;
	static String _retrievalAlgorithmMode;
	static List<String> _stopWords = new ArrayList<String>();
	static Map<Integer, String> _queries;

	public static void main(String[] args) throws IOException, ParseException 
	{
		if(args.length < 1)
		{
			System.out.println("Parameters file argument is missing!");
			System.out.println("Exiting Retrieval Experiment...");
			System.exit(1);
		}

		//get parameters file data
		try
		{
			System.out.println("Parameters file path:..." + args[0]);
			_inputFilePath = args[0];
			_inputParameters = TextFileReader.ReadFileParametres(_inputFilePath);		
			_queryFilePath = _inputParameters.get(0);
			_docsFilePath = _inputParameters.get(1);
			_outputFilePath = _inputParameters.get(2);
			_retrievalAlgorithmMode = _inputParameters.get(3);
		}
		catch (ArrayIndexOutOfBoundsException e) 
		{
			 System.out.println("Failed parsing parameters in '" + _inputFilePath + "' file!");
			 System.out.println("Exiting Retrieval Experiment...");
			 System.exit(1);
		}
		catch(IndexOutOfBoundsException e)
		{
			System.out.println("One or more paramters are missing in '" + _inputFilePath + "' file!");
			System.out.println("Exiting Retrieval Experiment...");
			System.exit(1);
		}
		catch (IOException e) 
		{
			 System.out.println("Failed reading '" + _inputFilePath + "' file!");
			 System.out.println("Exiting Retrieval Experiment...");
			 System.exit(1);
		}	

		//create algorithm according to mode parameter	
		switch (_retrievalAlgorithmMode.toLowerCase()) 
		{
			case Constants.BASIC_MODE:
				BasicAlgorithm basic = new BasicAlgorithm(_docsFilePath, _queryFilePath, _outputFilePath);
				basic.Execute();
				break;
				
			case Constants.IMPROVED_MODE:
				ImprovedAlgorithm improved = new ImprovedAlgorithm(_docsFilePath, _queryFilePath, _outputFilePath);
				improved.Execute();
				break;
	
			default:
		       System.out.println("Required mode is not supported!");
		       System.out.println("Exiting Retrieval Experiment...");
			   System.exit(1);
		}	   
	}
}