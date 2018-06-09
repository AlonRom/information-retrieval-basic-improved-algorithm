package Lucene;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.similarities.ClassicSimilarity;

public class MyClassicSimilarity extends ClassicSimilarity {

    @Override
    public float tf(float freq){
        float t = super.tf(freq);
        return t*t;
    }

    @Override
    public float idf(long docFreq , long docCount){
        if (docFreq==0)
            return 0;
        return (float)Math.log10(docCount/docFreq);
    }

}
