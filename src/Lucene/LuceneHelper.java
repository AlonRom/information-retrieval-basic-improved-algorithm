package Lucene;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.Term;

public class LuceneHelper 
{
	/** Indexes a single document **/
	public static Document IndexDocument(IndexWriter writer, String filePath) throws Exception 
	{
		// make a new, empty document
		Document document = new Document();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) 
		{
			//add the path of the file as a field named "filepath".  Use a
			//field that is indexed (i.e. searchable), but don't tokenize 
			//the field into separate words and don't index term frequency
			//or positional information
			document.add(new StringField(Constants.FILE_PATH, filePath, Field.Store.YES));
			
			//add the contents of the file to a field named "contents". Specify a Reader,
			//so that the text of the file is tokenized and indexed, but not stored.
			document.add(new TextField(Constants.CONTENT, br));
			
			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) 
			{
				//new index, so we just add the document (no old document can be there):
				System.out.println("adding " + filePath);
				writer.addDocument(document);
		    } 
			else 
			{
				//existing index (an old copy of this document may have been indexed) so 
				//we use updateDocument instead to replace the old one matching the exact 
				//path, if present:
				System.out.println("updating " + filePath);
				writer.updateDocument(new Term("path", filePath), document);
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return document;
	}
	
	public static CharArraySet GetMostFrequentWords(IndexReader reader, List<String> stopWords) throws Exception 
	{

		TermStats[] states = HighFreqTerms.getHighFreqTerms(reader, Constants.STOP_WORDS_COUNT, Constants.CONTENT, new HighFreqTerms.TotalTermFreqComparator());
		List<TermStats> stopWordsCollection = Arrays.asList(states);
		
		System.out.print("Stop Words: ");
		for (TermStats term : states)
		{
		    System.out.print(term.termtext.utf8ToString() + " "); 
		    stopWords.add(term.termtext.utf8ToString());
		}
		System.out.println();
		return new CharArraySet(stopWordsCollection, true);
	}

	public static Map<Integer, Integer[]> SearchIndexForQueries(Map<Integer, String> queries, CharArraySet stopWordSet, String outputFilePath,ClassicSimilarity similarity) throws FileNotFoundException, UnsupportedEncodingException
	{  
		String matches[] = new String[queries.size()];
		Map<Integer, Integer[]> matchMap = new HashMap<Integer, Integer[]>();
		for (Map.Entry<Integer, String> entry : queries.entrySet())
		{
		    try 
		    {
		    	System.out.println("Search for query " + entry.getKey() + ": " + entry.getValue());
				ScoreDoc[] hits=SearchQuery(entry.getValue(),stopWordSet,similarity);
				Integer matchID[]=new Integer[Constants.MAX_RESULT];

				String match=entry.getKey().toString().concat("  ");
				int i=0;
				for (ScoreDoc hit: hits)
				{
					if (i==Constants.MAX_RESULT || hit.score < Constants.SCORE_THRESHOLD)
					{
						break;
					}
					if (hit.score >= Constants.SCORE_THRESHOLD)
					{
						matchID[i]=hit.doc+1;
						i++;
						match=match.concat(Integer.toString(hit.doc+1));
						match=match.concat(" ");
					}
				}
				matches[entry.getKey()-1]=match;
				matchMap.put(entry.getKey(),matchID);
			} 
		    catch (Exception e) 
		    {
				e.printStackTrace();
			}	  
		}
	    PrintMathces(outputFilePath, matches);
		return matchMap;
	}
	
	private static void PrintMathces(String outputFilePath, String[] matches) throws FileNotFoundException, UnsupportedEncodingException 
	{
    	PrintWriter writer = new PrintWriter(outputFilePath, "UTF-8");
		for (String match:matches)
		{
			writer.println(match);
			writer.println();
		}	
		writer.close();
	}

	public static ScoreDoc[] SearchQuery(String searchQuery, CharArraySet stopWordSet, ClassicSimilarity similarity) throws IOException, ParseException
	{
		IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(Constants.DOCUMENTS_INDEX_PATH)));
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);

		QueryParser queryParser = new QueryParser(Constants.CONTENT, new StandardAnalyzer(stopWordSet));
		Query query = queryParser.parse(searchQuery);
		indexSearcher.setSimilarity(similarity);
		TopDocs results = indexSearcher.search(query, 1000);
		ScoreDoc[] hits = results.scoreDocs;
		
		int numTotalHits = Math.toIntExact(results.totalHits);
		System.out.println(numTotalHits + " total matching documents");
		return hits;
	}
}
