import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class FundCollections {
    //private ArrayList<String> fundsAvailableForDown

    public static ArrayList<Fund> mutualFundUniverse;
    public static ArrayList<Fund> filteredMutualFundUniverse;

    public static ArrayList<String> fundFamilyFilterTerms;
    public static ArrayList<String> tickersToDownload;

    public FundCollections(){
        fundFamilyFilterTerms = new ArrayList<>();
        tickersToDownload = new ArrayList<>();
    }

    public void populateMutualFundUniverse(){
        Parser p = new Parser();
        try{
            mutualFundUniverse = new ArrayList<>();
            mutualFundUniverse.addAll(p.parseMutualFundUniverse("mfundslist.txt",true));
            mutualFundUniverse.addAll(p.parseMutualFundUniverse("nasdaqlisted.txt",false));
            mutualFundUniverse.addAll(p.parseMutualFundUniverse("otherlisted.txt",false));
            filteredMutualFundUniverse = copyArrayList(mutualFundUniverse);
        }
        catch(FileNotFoundException e){
            System.out.println("Security Universe(s) File Not Found");
        }

    }

    public void populateFilteredMutualFundUniverseBySearchBar(String searchBarText){
        searchBarText = searchBarText.toLowerCase();

        if (searchBarText.equals("")){
            filteredMutualFundUniverse = copyArrayList(mutualFundUniverse);
        }
        else{
//            String[] searchBarKeywords = searchBarText.split(" ",10);
            filteredMutualFundUniverse.clear();
            for (Fund fund : mutualFundUniverse){
                if (fund.getName().toLowerCase().contains(searchBarText)){
                    filteredMutualFundUniverse.add(fund);
                }
                if (fund.getTicker().toLowerCase().contains(searchBarText)){
                    filteredMutualFundUniverse.add(fund);
                }
            }
        }
        System.out.println("Filtered Mutual Fund Universe By Search Bar");
    }

    public void deleteSelectedFunds(){
        for (Fund fund : FundCollections.mutualFundUniverse){
            if (FundCollections.tickersToDownload.contains(fund.getTicker())){
                try{
                    File fundCSVFile = new File(fund.getAbsolutePath());
                    fundCSVFile.delete();
                    FundCollections.tickersToDownload.remove(fund.getTicker());
                    fund.deleteData();
                    System.out.println("Deleted " + fund.getTicker() + " : " + fund.getName());
                }
                catch(Exception e){
                    System.out.println("Error deleting " + fund.getName() + "CSV file | " + e);
                }

            }
        }
    }

    public static ArrayList<Fund> downloadedPortfolioRiskAndReturnFunds(){
        ArrayList<Fund> downloadedPortfolioFunds = new ArrayList<>();
        for (Fund fund : FundCollections.mutualFundUniverse){
            //.hasHistoricalData() because we only show downloaded funds
            if (fund.hasHistoricalData()){
                downloadedPortfolioFunds.add(fund);
            }
        }
        return downloadedPortfolioFunds;
    }

    public static ArrayList<Fund> fundsForMarketBenchmark(){
        ArrayList<Fund> downloadedPortfolioFunds = new ArrayList<>();
        for (Fund fund : FundCollections.mutualFundUniverse){
            //.hasHistoricalData() because we only show downloaded funds
            if (fund.hasHistoricalData()){
                downloadedPortfolioFunds.add(fund);
            }
        }

        //add null fund for no benchmark selected
        downloadedPortfolioFunds.add(null);
        return downloadedPortfolioFunds;
    }

    public static ArrayList<Fund> getSelectedPortfolioFundArray(){
        ArrayList<Fund> selectedPortfolioFundArray = new ArrayList<>();
        for (Fund fund : FundCollections.mutualFundUniverse){
            if (fund.getInclusionInPortfolio()){
                selectedPortfolioFundArray.add(fund);
            }
        }
        return selectedPortfolioFundArray;
    }

    public void populateFundFamilyFilterTerms(){
        for (Fund fund : mutualFundUniverse){
            if(!fundFamilyFilterTerms.contains(fund.getFundFamily())){
                fundFamilyFilterTerms.add(fund.getFundFamily());
            }
        }
    }

    private ArrayList<Fund> copyArrayList(ArrayList<Fund> sourceArrayList){
        ArrayList<Fund> tempArrayTarget = new ArrayList<>(sourceArrayList);
        return tempArrayTarget;
    }

    public ArrayList<Fund> getMutualFundUniverse(){
        return mutualFundUniverse;
    }

    public ArrayList<Fund> getFilteredMutualFundUniverse(){
        return filteredMutualFundUniverse;
    }

    public ArrayList<String> getFundFamilies(){
        return fundFamilyFilterTerms;
    }
    public void setFilteredMutualFundUniverse(ArrayList<Fund> mutualFundUniverse){
        filteredMutualFundUniverse = mutualFundUniverse;
    }

}
