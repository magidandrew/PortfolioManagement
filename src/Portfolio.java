import java.security.spec.RSAOtherPrimeInfo;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Portfolio {

    private ArrayList<Fund> funds;
    private Fund marketBenchmark;
    private Fund[][] fundPairs;
    private Covariance[] covarianceArray;
    private double standardDevOfPortfolio;
    private double returnOfPortfolio;
    private double portfolioBeta;
    private final SimpleDateFormat dateFormatter;
    private Date portfolioStartDate;
    private Date portfolioEndDate;



    public Portfolio(ArrayList<Fund> inputFunds){
        funds = new ArrayList<>();
        if (inputFunds == null){
            funds.add(null);
        }
        funds.addAll(inputFunds);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        //TODO IDEALLY THIS WOULD BE MODIFIED WITH AN ADD/DELETE FUND OPTION
//        try {
//            calculatePortfolioValues();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
    }

    public void calculatePortfolioValues() throws ParseException {
        cutPortfolioStartDates();
        //TODO HAVE TWO PLACES FOR THE CUT AND UNCUT HISTORICAL PRICES
        for (Fund fund : funds){
            fund.calculateFundRiskReturn(fund.getCutHistoricalPrices());
            fund.calculateAnnualizedReturn();
            fund.calculateAnnualizedRisk();
//            fund.calculateBeta();
        }
        pairFunds();
        createCovarianceArray();
        calculateStandardDevPortfolio();
        calculateReturnPortfolio();
        calculatePortfolioBeta();

    }

    public void calculateFundValues(){
        try{
            cutPortfolioStartDates();
        }
        catch(Exception e){
            System.out.println("Error cutting portfolio start dates | " + e);
        }
        for (Fund fund : funds){
            fund.calculateFundRiskReturn(fund.getCutHistoricalPrices());
            fund.calculateAnnualizedReturn();
            fund.calculateAnnualizedRisk();
//            fund.calculateBeta();
        }
    }

    public void initializeFundsInPortfolio() throws ParseException {
        cutPortfolioStartDates();
        //TODO HAVE TWO PLACES FOR THE CUT AND UNCUT HISTORICAL PRICES
        for (Fund fund : funds){
            fund.calculateFundRiskReturn(fund.getCutHistoricalPrices());
            fund.calculateAnnualizedReturn();
            fund.calculateAnnualizedRisk();
        }
        System.out.println("initialized funds");
        for (Fund fund : funds){
            System.out.println(fund.toString());
        }
    }

    public void cutPortfolioStartDates() throws ParseException {
        ArrayList<Date> startDates = new ArrayList<>();
        ArrayList<Date> endDates = new ArrayList<>();
        for (Fund fund : funds){
            startDates.add(fund.getStartDate());
            endDates.add(fund.getEndDate());
        }

        //add market benchmark for cut portfolio dates calculation
        try{
            startDates.add(marketBenchmark.getStartDate());
            endDates.add(marketBenchmark.getEndDate());
        }
        catch(NullPointerException e){
            System.out.println("no market benchmark selected when cutting portfolio start/end dates");
        }

        if (!startDates.isEmpty() && !endDates.isEmpty()) {
            Date minDate = Collections.max(startDates);
            Date maxDate = Collections.min(endDates);

            setPortfolioStartDate(minDate);
            setPortfolioEndDate(maxDate);


            for (Fund fund : funds) {
                //create temp LinkedHashMap that will be set to the fund
                LinkedHashMap<String, Double> tempRawHistoricalPrices = new LinkedHashMap<>();
                //make an arraylist with the dates for easier manipualtion
                ArrayList<String> fundRawHistoricalPricesDates = new ArrayList<>(fund.getRawHistoricalPrices().keySet());
                //add values that are after the minimum date for every fund
                for (int i = 0; i < fund.getRawHistoricalPrices().size(); i++) {
                    String currentDate = fundRawHistoricalPricesDates.get(i);
                    //compare to method takes care of this and compares to our minDate
                    if (dateFormatter.parse(currentDate).compareTo(minDate) >= 0 &&
                            dateFormatter.parse(currentDate).compareTo(maxDate) <= 0) {
                        tempRawHistoricalPrices.put(currentDate, fund.getRawHistoricalPrices().get(currentDate));
                    }
                }
                //sets cutHistoricalPrices
                fund.setCutHistoricalPrices(tempRawHistoricalPrices);
            }
        }
    }

    public Fund[][] pairFunds(){
        int numberOfFunds = funds.size();
        int numberOfFundPairs = (numberOfFunds* (numberOfFunds-1))/2;
        fundPairs = new Fund[numberOfFundPairs][2];

        int leftPointer = 0;
        int rightPointer = 1;
        int counter = 0;
        while (leftPointer < (numberOfFunds-1)){
            fundPairs[counter][0] = funds.get(leftPointer);
            fundPairs[counter][1] = funds.get(rightPointer);
            rightPointer++;

            if (rightPointer >= numberOfFunds) {
                leftPointer++;
                rightPointer = leftPointer + 1;
            }
            counter++;
        }
        return fundPairs;
    }

    public void createCovarianceArray(){
        covarianceArray = new Covariance[fundPairs.length];
        for (int i=0; i<fundPairs.length;i++){
            covarianceArray[i] = new Covariance(fundPairs[i][0],fundPairs[i][1]);
            covarianceArray[i].calculateCovariance();
        }
        //return covarianceArray;
    }

    public double calculateStandardDevPortfolio(){
        standardDevOfPortfolio = 0;
        for (Fund fund : funds) {
            standardDevOfPortfolio += (Math.pow(fund.getWeight(), 2) *
                    Math.pow(fund.getSigma(), 2));
        }
        for (Covariance covariance : covarianceArray) {
            standardDevOfPortfolio += (2 * covariance.calculateCovariance() *
                    covariance.getFundA().getWeight() *
                    covariance.getFundB().getWeight());
        }
        standardDevOfPortfolio = Math.sqrt(12* standardDevOfPortfolio);
        return standardDevOfPortfolio;
    }

    public double calculateReturnPortfolio(){
        returnOfPortfolio =0;
        for (Fund fund : funds) {
            returnOfPortfolio += (fund.getWeight() * fund.getAnnualizedReturn());
        }
        return returnOfPortfolio;
    }

    public double calculatePortfolioBeta(){
        portfolioBeta = 0;
        for (Fund fund : funds){
            portfolioBeta += fund.getBeta() * fund.getWeight();
        }
        return portfolioBeta;
    }

    public double calculatePortfolioTotalWeight(){
        double portfolioWeight = 0;
        for (Fund fund : FundCollections.mutualFundUniverse){
            if (fund.getInclusionInPortfolio()){
                portfolioWeight += fund.getWeight();
            }
        }
        return portfolioWeight;
    }



    public void printResults() {
        for (Fund fund : funds){
            System.out.println(fund.getTicker() + ":  " + "Risk: " + Round.round(fund.getAnnualizedRisk(),3)
                    + "     " + "Return: " + Round.round(fund.getAnnualizedReturn(),3));
        }
        System.out.println("Portfolio Risk: " + Round.round(standardDevOfPortfolio,5) + "    " +
                "Porfolio Return: " + Round.round(returnOfPortfolio,5));
    }

    //Setter Methods
    public void addFund(Fund inputFund){
        funds.add(inputFund);
    }

    public void removeFund(Fund inputFund){
        funds.remove(inputFund);
    }

    public void setPortfolioStartDate(Date startDate){
        portfolioStartDate = startDate;
    }

    public void setPortfolioEndDate(Date endDate){
        portfolioEndDate = endDate;
    }

    public void setMarketBenchmark(Fund marketBenchmark){
        this.marketBenchmark = marketBenchmark;
    }


    //Getter Methods
    public ArrayList<Fund> getFundList(){
        return funds;
    }
    public String getPortfolioStartDateString(){
        if (portfolioStartDate == null){
            return "";
        }
        else{
            return dateFormatter.format(portfolioStartDate);
        }
    }
    public String getPorfolioEndDateString(){
        if (portfolioStartDate == null){
            return "";
        }
        else{
            return dateFormatter.format(portfolioEndDate);
        }
    }
    public double getStandardDevOfPortfolio() {
        return standardDevOfPortfolio;
    }
    public double getReturnOfPortfolio() {
        return returnOfPortfolio;
    }
    public double getPortfolioBeta(){
        return portfolioBeta;
    }
    public Covariance[] getCovarianceArray(){
        return covarianceArray;
    }

    public Boolean isPortfolioEmpty(){
        if (funds.isEmpty()){
            return true;
        }
        else{
            return false;
        }
    }

}
