package Lucene;

import org.apache.lucene.analysis.CharArrayMap;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.CharsRef;
import java.io.IOException;


public class MySynonym {
    SynonymMap.Builder builder;
    SynonymMap map ;
    CharArraySet phrasesSet;

    public MySynonym(){

       builder = new SynonymMap.Builder(true);
       map=null;
        phrasesSet=new CharArraySet(1000,true);
    }

    public void AddSynonym(String term, String syn, boolean includeOrignal){
        term.replaceAll(" ","\u0000");
        syn.replaceAll(" ","\0");
        builder.add(new CharsRef(term),new CharsRef(syn),includeOrignal);
    }

    public SynonymMap getMyMap(){
        return map;
    }

    public void BuildMyMap(){
        try{
            map = builder.build();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    public void UseDefaultSynonyms(){
        DefaultSynonym(true);
    }

    public void UseDefaultSynonyms(boolean saveOriginal){
        DefaultSynonym(saveOriginal);
    }

    private void DefaultSynonym(boolean saveOriginal){
        AddSynonym("united state america","usa",saveOriginal);
        AddSynonym("u.s","usa",saveOriginal);
        AddSynonym("u.s.","usa",saveOriginal);
        AddSynonym("united_states","usa",saveOriginal);
        AddSynonym("kennedy_administration","kennedyadministration",saveOriginal);
        AddSynonym("u.n","unitednations",saveOriginal);
        AddSynonym("united_nations","unitednations",saveOriginal);
        AddSynonym("viet_nam","vietnam",saveOriginal);
        AddSynonym("ngo_dinh_diem","ngodinhdiem",saveOriginal);
        AddSynonym("president_diem","ngodinhdiem",saveOriginal);
        AddSynonym("diem","ngodinhdiem",saveOriginal);
        AddSynonym("west germany","westgermany",saveOriginal);
        AddSynonym("president_diem","ngodinhdiem",saveOriginal);
        AddSynonym("west_germany","westgermany",saveOriginal);
    }

    public void AddPhrase(String phrase){
        phrasesSet.add(phrase.toCharArray());
    }

    public void UseDefaultPhrases(){
        DefaultPhrase();
    }

    public CharArraySet getPhrasesSet(){
        return phrasesSet;
    }

    private void DefaultPhrase(){
        AddPhrase("united states");
//        AddPhrase("kennedy administration");
        //AddPhrase("president diem");
        //AddPhrase("ngo dinh diem");
         //AddPhrase("united nations");
         AddPhrase("viet nam");
//        AddPhrase("west germany");

    }







}
