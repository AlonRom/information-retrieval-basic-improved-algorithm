package Lucene;

public class Constants 
{
	public static final String CONTENT = "content";
	public static final String FILE_NAME = "filename";
	public static final String FILE_PATH = "filepath";
	public static final String DOCS_FILE_INDEX_PATH="./files/indexes/docsfile";
	public static final String DOCUMENTS_INDEX_PATH="./files/indexes/documents";
	public static final String PARSED_DOCS_PATH="./files/parsed/";
	public static final String TRUTH_PATH="./files/truth.txt";
	public static final String PARSED_DOCS_FILE_TYPE=".txt";
	public static final int STOP_WORDS_COUNT = 20;
	public static final int MAX_RESULT = 10;
	public static final double SCORE_THRESHOLD = 0.8;
	public static final boolean SYSTEM_RANKING = true; //should be false when the project submitted.
}
