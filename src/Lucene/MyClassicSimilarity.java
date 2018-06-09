package Lucene;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.search.similarities.ClassicSimilarity;

public class MyClassicSimilarity extends ClassicSimilarity {

	@Override
	public float idf(long docFreq, long numDocs) {
		return (float) (1+ (Math.log(numDocs) / (docFreq + 1)));
	}

	@Override
	public float lengthNorm(int numTerms) {
		return (float) (1 / Math.sqrt(numTerms));
	}

	@Override
	public float scorePayload(int doc, int start, int end, BytesRef payload) {
		return 1;
	}

	@Override
	public float sloppyFreq(int distance) {
		 return 1 / (distance + 1);
	}

	@Override
	public float tf(float freq) {
		return (float)(1+Math.log(freq));
	}
}