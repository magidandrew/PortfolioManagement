import javax.swing.*;
import java.util.ArrayList;

public class Main{
    //---------FOR TESTING PURPOSES ONLY------//
    public static ArrayList<Fund> fundList;
    //---------FOR TESTING PURPOSES ONLY------//

    //entry point
    public static void main(String[] args) {
//        Fund VPCCX = new Fund("/Users/andrewmagid/IdeaProjects/CapmModel/src/VPCCX.csv");
//        Fund POGRX = new Fund("/Users/andrewmagid/IdeaProjects/CapmModel/src/POGRX.csv");
//        Fund VFIAX = new Fund("/Users/andrewmagid/IdeaProjects/CapmModel/src/VFIAX_500Index_2011_09_01.csv");
//
//        ArrayList<Fund> arr = new ArrayList<>();
//        arr.add(VPCCX);
//        arr.add(POGRX);
//
//        arr.add(VFIAX);
//
//        VPCCX.setWeight(0);
//        POGRX.setWeight(0);
//        VFIAX.setWeight(.5);
//
//        Portfolio myPortfolio = new Portfolio(arr);
//
//        myPortfolio.printResults();
//        System.out.println(VFIAX);

//        Importer importer = new Importer();
////        ArrayList<Fund> fundArr = importer.importFolder("/Users/andrewmagid/IdeaProjects/CapmModel/DadsFunds");
////        fundList = fundArr;
////
////        HashMap<String,Double> fundWeights = new HashMap<>();
////        fundWeights.put("IVOG",.0599);
////        fundWeights.put("IVOV",.1083);
////        fundWeights.put("POGRX",.1588);
////        fundWeights.put("VDC",.0163);
////        fundWeights.put("VDIGX",.2582);
////        fundWeights.put("VFIAX",.0454);
////        fundWeights.put("VFICX",.0075);
////        fundWeights.put("VFSTX",.015);
////        fundWeights.put("VGHAX",.0674);
////        fundWeights.put("VGSLX",.0081);
////        fundWeights.put("VGT",.0167);
////        fundWeights.put("VHCAX",.0613);
////        fundWeights.put("VIMAX",.0206);
////        fundWeights.put("VPCCX",.0176);
////        fundWeights.put("VPMCX",.0244);
////        fundWeights.put("VSGAX",.017);
////        fundWeights.put("VWILX",.0975);
////
////        for (Fund fund : fundArr){
////            String[] splitArr = fund.getName().split("_",0);
////            fund.setWeight(fundWeights.get(splitArr[0]));
////        }
////        Portfolio myPortfolio = new Portfolio(fundArr);
////
////        myPortfolio.printResults();

//

        //SAMPLE FILTER
        String[] sampleFundFamilyFilter = {"Vanguard Group"};

        FundCollections fCollection = new FundCollections();
        Importer importer = new Importer();

        fCollection.populateMutualFundUniverse();
        importer.createCSVTickerArrayListFromDirectory();
        importer.populateFundDataFromCSV();
        //FundCollections.filteredMutualFundUniverse = fCollection.getMutualFundUniverse();
//        fCollection.filterMutualFundUniverseByFundFamily(new ArrayList<String>(Arrays.asList(sampleFundFamilyFilter)));

        //fundList = fCollection.getMutualFundUniverse();
        System.out.println("fund universe parsing complete");

        JFrame frame = new PortfolioManagementEditor("Portfolio Management");
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
