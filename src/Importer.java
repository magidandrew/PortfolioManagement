import java.io.File;
import java.util.ArrayList;

public class Importer {
    private ArrayList<String> csvTickerArrayList;
    private ArrayList<String> csvTickerPathArrayList;

    private final String pathToDownloadedRawPrices = "./DownloadedRawPrices/";

    //TODO REWORK THE IMPORTER
    public ArrayList<String> createCSVTickerArrayListFromDirectory(){
        try {
            File folderPath = new File(pathToDownloadedRawPrices);
            File[] importedPricesCSVFolder = folderPath.listFiles();

            csvTickerArrayList = new ArrayList<>();
            csvTickerPathArrayList = new ArrayList<>();

            if (importedPricesCSVFolder != null) {

                for (File child : importedPricesCSVFolder) {
                    String[] csvSplitName = child.getName().split("\\.",2);


                    //0 --> ticker string
                    //1 --> "csv" extensions
                    if (csvSplitName[1].contains("csv")) {
                        csvTickerArrayList.add(csvSplitName[0]);
                        csvTickerPathArrayList.add(child.getAbsolutePath());
                    }
                }
            }
            return csvTickerArrayList;
        }
        catch(Exception e){
            System.out.println("Importer createCSVTickerArrayList went wrong. Error: " + e);
            return null;
        }
    }

    public void populateFundDataFromCSV(){
        Parser parser = new Parser();
        for (Fund fund : FundCollections.mutualFundUniverse){
            if (csvTickerArrayList.contains(fund.getTicker())){
                int index = csvTickerArrayList.indexOf(fund.getTicker());
                String path = pathToDownloadedRawPrices+csvTickerArrayList.get(index)+".csv";

                try {
                    fund.setFilePath(csvTickerPathArrayList.get(index));
                    fund.readHistoricalPrices(path);
                    fund.calculateFundRiskReturn(fund.getRawHistoricalPrices());
                    fund.setStartDate();
                    fund.setEndDate();
                    fund.toggleInclusionInDownload();
                }
                catch(IndexOutOfBoundsException e){
                    System.out.println("Could not find " + path);
                }

                FundCollections.tickersToDownload.remove(fund.getTicker());

            }
        }
    }

    public ArrayList<String> getCSVTickerArrayList(){
        return csvTickerArrayList;
    }

}
