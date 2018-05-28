package Lucene;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
	
	public static TermStats[] GetMostFrequentWords(IndexReader reader) throws Exception 
	{
		return HighFreqTerms.getHighFreqTerms(reader, Constants.STOP_WORDS_COUNT, Constants.CONTENT, new HighFreqTerms.TotalTermFreqComparator());
	}
}
