package Lucene;

import org.apache.lucene.analysis.synonym.SynonymMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

public class SystemRanking {

    Map<Integer,Integer[]> result;
    long[] numRelevantRetrieved;
    long[] numRelevant;
    long[] numRetrieved;
    String truthPath;
    Integer numOfQueries;

    public SystemRanking(Map<Integer,Integer[]> result,String truthPath){
        this.result = result;
        numOfQueries = result.size();
        this.truthPath = truthPath;
        numRelevantRetrieved = numRelevantItemsRetrieved();
        numRelevant = numRelevantItems();
        numRetrieved = numItemsRetrieved();

    }

    public long[] getNumRelevantRetrieved() {
        return numRelevantRetrieved;
    }

    public long[] getNumRelevant(){
        return numRelevant;
    }

    public long[] getNumRetrieved(){
        return numRetrieved;
    }

    public float[] getPrecision(){
        return precision();
    }

    public float[] getRecall(){
        return recall();
    }

    public float[] getFScore(double beta){
        return fScore(beta);
    }

    private long[] numRelevantItemsRetrieved()
    {
        String truth;
        String TruthLines[] = null;
        long[] relvantRetrived=new long[result.size()];
        Arrays.fill(relvantRetrived, 0);

        try 
        {
            truth= new String(Files.readAllBytes(Paths.get(truthPath)));
            TruthLines=truth.split("[\\r\\n]+");
            for (String line:TruthLines)
            {
                if (line.equals(""))
                {
                    continue;
                }
                String numbers[]=line.split(" +");
                Integer retriveDocs[]=result.get(Integer.valueOf(numbers[0]));
                for (int i=1;i<numbers.length;i++)
                {
                    numbers[i]=numbers[i].replaceAll("\r","");
                    for (Integer doc:retriveDocs)
                    {
                        if (doc==null)
                            break;
                        if (doc.equals(Integer.valueOf(numbers[i])))
                        {
                            relvantRetrived[Integer.valueOf(numbers[0])-1]++;
                        }
                    }
                }
            }
            return relvantRetrived;
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;

    }

    private long[] numRelevantItems()
    {
        String truth;
        String TruthLines[]=null;
        long [] relevant=new long[numOfQueries];

        try 
        {
            truth= new String(Files.readAllBytes(Paths.get(truthPath)));
            TruthLines=truth.split("[\\r\\n]+");
            for (String line:TruthLines) {
                if (line.equals("")) 
                {
                    continue;
                }
                String numbers[] = line.split(" +");
                relevant[Integer.valueOf(numbers[0])-1]=(long)(numbers.length-1);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return relevant;
    }

    private long[] numItemsRetrieved()
    {
        long [] numItems=new long[result.size()];
        Arrays.fill(numItems, 0);
        for (Map.Entry<Integer, Integer[]> entry:result.entrySet())
        {
            Integer retrieved[]=entry.getValue();
            for (Integer i:retrieved)
            {
                if (i==null)
                {
                    break;
                }
                numItems[entry.getKey()-1]++;
            }
        }
        return numItems;
    }

    private float[] precision()
    {
        float[] preci=new float[numRetrieved.length];
        for (int i=0;i<preci.length;i++)
        {
            if (numRetrieved[i]==0)
            {
                preci[i]=0;
                continue;
            }
            preci[i]=(float) numRelevantRetrieved[i]/numRetrieved[i];
        }
        return preci;
    }

    private float[] recall()
    {
        float[] re=new float[numRelevant.length];
        for (int i=0;i<re.length;i++)
        {
            if (numRelevant[i]==0)
            {
                re[i]=0;
                continue;
            }
            re[i]=(float) numRelevantRetrieved[i]/numRelevant[i];
        }
        return re;
    }

    private static float average(float [] arr)
    {
        if (arr==null)
        {
            return 0;
        }
        float sum=0;
        for (float a:arr)
        {
            sum+=a;
        }
        return sum/arr.length;
    }

    public  void printSystemRanking(){

        float[] precision = getPrecision();

        //for each query the recall of our system - (relevant items retrieve)/relevant
        float[] recall = getRecall();

        float[] fs = getFScore(Constants.FSCORE_BETA);

        //print the average precision and recall
        System.out.println("The precision: "+ precision[1]);
        System.out.println("The recall: "+ recall[1]);
        System.out.println("The average precision: "+ average(precision));
        System.out.println("The average recall: "+ average(recall));
        System.out.println("The average 1f-score: "+ average(fs));

    }

    private float[] fScore(double beta) {
        float[] precision = getPrecision();
        float[] recall = getRecall();
        float[] fs = new float[precision.length];

        for (int i=0;i<precision.length;i++){
            if (precision[i]==0 && recall[i]==0){
                fs[i]=0;
                continue;
            }
            fs[i] =  (((float)(1+Math.pow(beta,2.0))*(precision[i]*recall[i]))/(float)((Math.pow(beta,2.0)*precision[i])+recall[i]));

        }
        return fs;
    }
}
