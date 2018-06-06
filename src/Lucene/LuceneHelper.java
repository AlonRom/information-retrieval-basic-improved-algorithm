package Lucene;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
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
	
	public static String StemTerm(String term) 
	{
	    PorterStemmer stemmer = new PorterStemmer();
	    return stemmer.stem(term);
	}

	public static Map<Integer, Integer[]> SearchIndexForQueries(Map<Integer, String> queries, CharArraySet stopWordSet, String outputFilePath,ClassicSimilarity similarity) throws FileNotFoundException, UnsupportedEncodingException
	{  
		List<Integer> sortedDocs = new ArrayList<Integer>();
		String matches[] = new String[queries.size()];
		Map<Integer, Integer[]> matchMap = new HashMap<Integer, Integer[]>();
		for (Map.Entry<Integer, String> entry : queries.entrySet())
		{
		    try 
		    {
		    	System.out.println("Search for query " + entry.getKey() + ": " + entry.getValue());
				ScoreDoc[] hits=SearchQuery(entry.getValue(),stopWordSet,similarity);
				Integer matchID[]=new Integer[Constants.MAX_RESULT];
				
				//filter document by score threshold
				int i=0;
				sortedDocs.clear();
				for (ScoreDoc hit: hits)
				{
					if (i==Constants.MAX_RESULT || hit.score < Constants.SCORE_THRESHOLD)
					{
						break;
					}
					if (hit.score >= Constants.SCORE_THRESHOLD)
					{
						sortedDocs.add(hit.doc+1);
						i++;
					}
				}
				
				//sort filtered documents into ascending order and prepare for output print
				i=0;
		        Collections.sort(sortedDocs); 
				String match=entry.getKey().toString().concat("  ");
				for (Integer doc: sortedDocs)
				{
					matchID[i] = doc;
					i++;
					match=match.concat(Integer.toString(doc));
					match=match.concat(" ");				
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
		MySynonym syn = new MySynonym();
		syn.UseDefault();
		syn.BuildMyMap();
		SynonymMap map = syn.getMyMap();
		MyCustomAnalyzer ana=new MyCustomAnalyzer(map,stopWordSet);

		QueryParser queryParser = new QueryParser(Constants.CONTENT, ana);
		Query query = queryParser.parse(searchQuery);
		indexSearcher.setSimilarity(similarity);
		TopDocs results = indexSearcher.search(query, 1000);
		ScoreDoc[] hits = results.scoreDocs;
		
		int numTotalHits = Math.toIntExact(results.totalHits);
		System.out.println(numTotalHits + " total matching documents");
		return hits;
	}

	public static void IndexSplittedDocuments(IndexWriter writer, String path, String type, int numOfDocs)
	{
		for (Integer i=0;i<numOfDocs;i++)
		{
			String tempPath = path;
			tempPath = tempPath.concat(Integer.toString(i+1));
			tempPath = tempPath.concat(type);
			try 
			{
				IndexDocument(writer, tempPath);
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}
}
