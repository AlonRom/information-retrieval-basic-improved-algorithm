package Lucene;

import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.CharsRef;

import java.io.IOException;


public class MySynonym {
    SynonymMap.Builder builder;
    SynonymMap map = null;

    public MySynonym(){

       builder = new SynonymMap.Builder(true);
    }

    public void AddSynonym(String term, String syn, boolean includeOrignal){
        term.replaceAll(" ","\0");
        syn.replaceAll(" ","\");
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

    public void initizlaize(){
        builder = new SynonymMap.Builder(true);
        map = null;
    }

    public void UseDefault(){
        DefaultSynonym(true);
    }

    public void UseDefault(boolean saveOriginal){
        DefaultSynonym(saveOriginal);
    }

    private void DefaultSynonym(boolean saveOriginal){
        AddSynonym("U.S","United State Of America",saveOriginal);
        AddSynonym("U.S","USA",saveOriginal);
        AddSynonym("U.S","UNITED STATES",saveOriginal);
        AddSynonym("U.N","United Nations",saveOriginal);
        AddSynonym("VIET NAM","VIETNAM",saveOriginal);
    }




}
