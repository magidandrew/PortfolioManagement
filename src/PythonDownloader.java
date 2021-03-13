import java.io.*;
import java.lang.StringBuilder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class PythonDownloader {
    private ArrayList<String> tickersToDownload;
    private String startDate;
    private String endDate;
    private Process runPythonPriceImporter;
    private static String startDateForDownloading = "1900-01-01";

    public PythonDownloader(ArrayList<String> tickers, String startDate, String endDate){
        tickersToDownload = new ArrayList<>(tickers);
        this.startDate = startDate;
        this.endDate = endDate;
    }


    //TODO change this to reflect the final location of the python project


    public void historicalPricesToCSV() {
        for (String ticker : tickersToDownload) {

            try {
                ProcessBuilder pb = new ProcessBuilder("bash", "RunPriceImporterVenv.sh", ticker, startDate, endDate);
                //TODO CHANGE THIS DIRECTORY TO SOMETHING THAT IS WITHIN THE PROJECT SCOPE
                pb.directory(new File("/Users/andrewmagid/PycharmProjects/PricesImporter/"));
                runPythonPriceImporter = pb.start();
                runPythonPriceImporter.waitFor();
                printProgramOutput();

            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void printProgramOutput() throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(runPythonPriceImporter.getInputStream()));
        String line = null;
        while ( (line = in.readLine()) != null) {
            sb.append(line);
            sb.append(System.getProperty("line.separator"));
        }
        String result = sb.toString();
        System.out.println(result);
    }

    public static void downloadSelectedFunds(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        PythonDownloader pythonDownloader = new PythonDownloader(FundCollections.tickersToDownload, startDateForDownloading ,formatter.format(currentDate));
        pythonDownloader.historicalPricesToCSV();

        Importer importer = new Importer();
        importer.createCSVTickerArrayListFromDirectory();
        importer.populateFundDataFromCSV();
    }

    //TESTER CODE
    public static void main(String[] args) {
        String[] arr = {"VPCCX","IVOV","POGRX","VDC","VFSTX","VPMCX"};
        ArrayList<String> testArr = new ArrayList<>(Arrays.asList(arr));
        PythonDownloader a = new PythonDownloader(testArr,"1987-01-01","2020-01-01");
        a.historicalPricesToCSV();

    }

}
