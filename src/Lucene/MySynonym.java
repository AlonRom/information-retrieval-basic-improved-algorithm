package Lucene;

import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.CharsRef;
import java.io.IOException;


public class MySynonym {
    SynonymMap.Builder builder;
    SynonymMap map ;

    public MySynonym(){

       builder = new SynonymMap.Builder(true);
       map=null;
    }

    public void AddSynonym(String term, String syn, boolean includeOrignal){
        term.replaceAll(" ","\u0000");
        syn.replaceAll(" ","\u0000");
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


    public void UseDefault(){
        DefaultSynonym(true);
    }

    public void UseDefault(boolean saveOriginal){
        DefaultSynonym(saveOriginal);
    }

    private void DefaultSynonym(boolean saveOriginal){
        AddSynonym("us","united state of america",saveOriginal);
        AddSynonym("us","usa",saveOriginal);
        AddSynonym("us","unit state",saveOriginal);
        AddSynonym("united states","us",saveOriginal);
        AddSynonym("U.N","United Nations",saveOriginal);
        AddSynonym("VIET NAM","VIETNAM",saveOriginal);
        AddSynonym("hot","cold",saveOriginal);
        AddSynonym("cold","hot",saveOriginal);
    }




}
