package Lucene;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.FlattenGraphFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymFilter;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.util.PagedBytes;

public class MyCustomAnalyzer extends Analyzer {
    SynonymMap map;
    IndexReader reader;
    CharArraySet stopWordSet=null;
    public MyCustomAnalyzer(SynonymMap map){
        this.map = map;
    }

    public MyCustomAnalyzer(SynonymMap map, CharArraySet stopWordSet){
        this.map = map;
        this.stopWordSet = stopWordSet;
    }



    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer source = new StandardTokenizer();
        TokenStream filter = new StandardFilter(source);
        filter = new LowerCaseFilter(filter);
        filter = new SynonymGraphFilter(filter,map,true);
        if (stopWordSet!=null)
            filter = new StopFilter(filter,stopWordSet);
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
