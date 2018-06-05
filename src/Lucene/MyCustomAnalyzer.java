package Lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.FlattenGraphFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.util.PagedBytes;

public class MyCustomAnalyzer extends Analyzer {
    SynonymMap map;
    IndexReader reader;
    public MyCustomAnalyzer(SynonymMap map, IndexReader reader){
        this.map = map;
        this.reader = reader;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer source = new StandardTokenizer();
        TokenStream filter = new StandardFilter(source);
        filter = new LowerCaseFilter(filter);
        filter = new FlattenGraphFilter(filter);
        return new TokenStreamComponents(source, filter);
    }

//    protected TokenStreamComponents createComponents(String fieldName, IndexReader reader) {
//        Tokenizer source = new StandardTokenizer();
//        TokenStream filter = new StandardFilter(source);
//        filter = new LowerCaseFilter(filter);
//        filter = new FlattenGraphFilter(filter);
//        return new TokenStreamComponents(source, filter);
//    }
}
