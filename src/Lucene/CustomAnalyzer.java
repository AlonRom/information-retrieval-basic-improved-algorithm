package Lucene;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.FlattenGraphFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.index.IndexReader;

public class CustomAnalyzer extends Analyzer {
    SynonymMap map;
    IndexReader reader;
    CharArraySet stopWordSet=null;
    CharArraySet pharesSet=null;
    public boolean indexing;
    public CustomAnalyzer(SynonymMap map){
        this.map = map;
    }

    public CustomAnalyzer(SynonymMap map, CharArraySet stopWordSet,CharArraySet pharesSet, boolean indexing){
        this.map = map;
        this.stopWordSet = stopWordSet;
        this.pharesSet = pharesSet;
        this.indexing=indexing;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer source = new StandardTokenizer();
        TokenStream filter = new StandardFilter(source);
        filter = new LowerCaseFilter(filter);
        if (pharesSet!=null)
            //filter = new AutoPhrasingTokenFilter(filter, pharesSet, true);
        if (map!=null)
        {
            filter = new SynonymGraphFilter(filter, map, true);
            if (indexing)
                filter =new FlattenGraphFilter(filter);
        }
        if (stopWordSet!=null)
                filter = new StopFilter(filter, stopWordSet);
        filter = new PorterStemFilter(filter);
        return new TokenStreamComponents(source, filter);
    }
}
