package Lucene;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.index.IndexReader;

public class MyCustomAnalyzer extends Analyzer {
    SynonymMap map;
    IndexReader reader;
    CharArraySet stopWordSet=null;
    CharArraySet pharesSet=null;
    public MyCustomAnalyzer(SynonymMap map){
        this.map = map;
    }

    public MyCustomAnalyzer(SynonymMap map, CharArraySet stopWordSet,CharArraySet pharesSet){
        this.map = map;
        this.stopWordSet = stopWordSet;
        this.pharesSet = pharesSet;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer source = new StandardTokenizer();
        TokenStream filter = new StandardFilter(source);
        filter = new LowerCaseFilter(filter);
        if (pharesSet!=null)
            filter = new AutoPhrasingTokenFilter(filter,pharesSet,false);
        if (map!=null)
            filter = new SynonymGraphFilter(filter,map,false);
        if (stopWordSet!=null)
            filter = new StopFilter(filter,stopWordSet);
        filter = new PorterStemFilter(filter);
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
