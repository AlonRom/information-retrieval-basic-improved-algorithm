package Lucene;


import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

public class SystemRanking {

    public static long[] numRelevantItemsRetrieved(Map<Integer,Integer[]> result, String truthPath){
        String truth;
        String TruthLines[]=null;
        long[] relvantRetrived=new long[result.size()];
        Arrays.fill(relvantRetrived, 0);

        try {
            truth= new String(Files.readAllBytes(Paths.get(truthPath)));
            TruthLines=truth.split("[\\r\\n]+");
            for (String line:TruthLines){
                if (line.equals(""))
                {
                    continue;
                }
                String numbers[]=line.split(" +");
                Integer retriveDocs[]=result.get(Integer.valueOf(numbers[0]));
                for (int i=1;i<numbers.length;i++){
                    numbers[i]=numbers[i].replaceAll("\r","");
                    for (Integer doc:retriveDocs){
                        if (doc==null)
                                break;
                        if (doc.equals(Integer.valueOf(numbers[i]))){
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

    public static long[] numRelevantItems(String truthPath, Integer numOfQueries){
        String truth;
        String TruthLines[]=null;
        long [] relvant=new long[numOfQueries];

        try {
            truth= new String(Files.readAllBytes(Paths.get(truthPath)));
            TruthLines=truth.split("[\\r\\n]+");
            for (String line:TruthLines) {
                if (line.equals("")) {
                    continue;
                }
                String numbers[] = line.split(" +");
                relvant[Integer.valueOf(numbers[0])-1]=(long)(numbers.length-1);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return relvant;


    }

    public static long[] numItemsRetrieved(Map<Integer,Integer[]> result ){
        long [] numItems=new long[result.size()];
        Arrays.fill(numItems, 0);
        for (Map.Entry<Integer, Integer[]> entry:result.entrySet()){
            Integer retrieved[]=entry.getValue();
            for (Integer i:retrieved){
                if (i==null){
                    break;
                }
                numItems[entry.getKey()-1]++;
            }

        }

        return numItems;
    }

    public static float[] precision(long [] relvantRetrieved,long[] retrieved){
        float[] preci=new float[retrieved.length];
        for (int i=0;i<preci.length;i++){
            if (retrieved[i]==0){
                preci[i]=0;
                continue;
            }
            preci[i]=(float) relvantRetrieved[i]/retrieved[i];
        }
        return preci;
    }



    public static float[] recall(long [] relvantRetrieved,long[] relevant){
        float[] re=new float[relevant.length];
        for (int i=0;i<re.length;i++){
            if (relevant[i]==0){
                re[i]=0;
                continue;
            }
            re[i]=(float) relvantRetrieved[i]/relevant[i];
        }
        return re;
    }



    public static float average(float [] arr){
        if (arr==null) {
            return 0;
        }
        float sum=0;
        for (float a:arr){
            sum+=a;
        }
        return sum/arr.length;
    }
}
