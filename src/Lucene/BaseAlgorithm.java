package Lucene;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.search.similarities.ClassicSimilarity;

public class BaseAlgorithm 
{
	protected String _docsFilePath;
	protected String _queryFilePath;
	protected String _outputFilePath;	
	protected CharArraySet _stopWordsSet;
	protected List<String> _stopWords = new ArrayList<String>();
	protected int _numberOfDocs;
	protected ClassicSimilarity _similarity;
	protected Map<Integer, String> _queries;
	protected Map<Integer, Integer[]> _searchQueriesResult;
}
