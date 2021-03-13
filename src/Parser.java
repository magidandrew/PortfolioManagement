import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Parser {
    private LinkedHashMap<String,Double> info;
    private final String endStringMarker = "File Creation Time";

    //TODO remove info var and replace with a method return
    public void parseCSV(String fileName) throws FileNotFoundException{
        //4 for close, 5 for adj close, 1 for new formatting
       final int PRICETYPE = 1;

        File filename = new File(fileName);
        Scanner s = new Scanner(filename);
        info = new LinkedHashMap<String,Double>();

        //don't use first line (column headers)
        s.nextLine();

        //populate LinkedHashMap
        while (s.hasNextLine()){
            String line = s.nextLine();
            String[] arr = line.split(",",7);
            info.put(arr[0],Double.parseDouble(arr[PRICETYPE]));
        }
        //return info;

    }

    public ArrayList<Fund> parseMutualFundUniverse(String filename,Boolean isMFundsList) throws FileNotFoundException{

        File file = new File("./SecurityUniverse/"+filename);
        Scanner s = new Scanner(file);
        ArrayList<Fund> tempMutualFundUniverse = new ArrayList<>();

        //don't use first line (it shows the document format)
        s.nextLine();

        //nasdaqlisted.txt and otherlisted.txt DO NOT have fund families.
        if (isMFundsList) {
            while (s.hasNextLine()) {
                String line = s.nextLine();
                if (!line.contains(endStringMarker)) {
                    String[] arr = line.split("\\|", 6);
                    tempMutualFundUniverse.add(new Fund(arr[0], arr[1], arr[2]));
                }
            }
        }
        else {
            while (s.hasNextLine()) {
                String line = s.nextLine();
                if (!line.contains(endStringMarker)) {
                    String[] arr = line.split("\\|", 6);
                    tempMutualFundUniverse.add(new Fund(arr[0], arr[1], null));
                }
            }
        }
        return tempMutualFundUniverse;

    }


    public LinkedHashMap<String,Double> getDictionary(){
        return info;
    }

    public void printDictionary(){
        info.forEach((key,value) -> System.out.println(key + ": $" + value));
    }

    //TESTER CODE
    public static void main(String[] args) {
        Parser a = new Parser();
        try{
            a.parseCSV(args[0]);
            a.getDictionary().forEach((key, value) -> System.out.println(key + ": $" + value));
            //System.out.println(a.parseCSV(args[0]).toString());
            //System.out.println(a.parseCSV(args[0]).size());
        }
        catch (FileNotFoundException e){
            System.out.println("File Not Found.");
        }
    }
}

