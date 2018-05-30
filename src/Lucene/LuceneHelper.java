package Lucene;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.TermStats;

public class LuceneHelper 
{
	public static Document CreateDocument(IndexWriter writer, String filePath) throws Exception 
	{
		Document document = new Document();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) 
		{
			document.add(new TextField(Constants.CONTENT, br));
			writer.addDocument(document);
			writer.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return document;
	}
	
	public static CharArraySet GetMostFrequentWords(IndexReader reader) throws Exception 
	{
		TermStats[] states = HighFreqTerms.getHighFreqTerms(reader, Constants.STOP_WORDS_COUNT, Constants.CONTENT, new HighFreqTerms.TotalTermFreqComparator());
		List<TermStats> stopWordsCollection = Arrays.asList(states);
		return new CharArraySet(stopWordsCollection, true);
	}

}
