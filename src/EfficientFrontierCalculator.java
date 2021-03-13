import gurobi.*;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;

public class EfficientFrontierCalculator {

    private ArrayList<Fund> efFunds;
    private Fund[][] efFundPairs;
    private GRBEnv env;
    private GRBModel efModel;
    private GRBVar[] grbVars;
    private ArrayList<Portfolio> optimizedPortfolios;
    private double returnConstraint;
    private double increment;
    private double maxReturnConstraint;
    private double minReturnConstraint;
    private GRBQuadExpr varianceExpr;
    private GRBLinExpr returnExpr;
    private GRBLinExpr sumOfWeightsExpr;


    public EfficientFrontierCalculator(ArrayList<Fund> efFunds){
        this.efFunds = efFunds;
        optimizedPortfolios = new ArrayList<>();
    }

    public void generateEfficientFrontier() {
        try {
            env = new GRBEnv("efficientfrontier.log");
            efModel = new GRBModel(env);

            //create lb, ub, names arrays for .addVars()
            double[] varsLowerBounds = new double[efFunds.size()];
            double[] varsUpperBounds = new double[efFunds.size()];
            String[] varsTickers = new String[efFunds.size()];
            for (int i = 0; i < efFunds.size(); i++) {
                varsLowerBounds[i] = efFunds.get(i).getEfLowerBound();
                varsUpperBounds[i] = efFunds.get(i).getEfUpperBound();
                varsTickers[i] = efFunds.get(i).getTicker();
            }

            //create variables
            grbVars = efModel.addVars(varsLowerBounds, varsUpperBounds, null, null, varsTickers);

            //update model after adding vars
            efModel.update();

            //build expressions for variance, return, and sum of weights
            varianceExpr = buildVarianceExpression();
            returnExpr = buildReturnExpression();
            sumOfWeightsExpr = buildSumOfWeightsExpression();

            //set weight constraint
            efModel.addConstr(sumOfWeightsExpr, GRB.EQUAL, 1.0, "weight sum");

            setOptimizedMaximum();

            setOptimizedMinimum();

            setOptimizedFundWeights();
            optimizedPortfolios.add(new Portfolio(efFunds));
            System.out.println(optimizedPortfolios.get(0).calculateReturnPortfolio());
            System.out.println("Optimized portfolio return**************************");

            System.out.println(setOptimizedFundWeights());
            System.out.println(calculatePortfolioRisk(varianceExpr));
            System.out.println(calculateMonthlyPortfolioReturn());
            System.out.println("******************************************************************************");

            //get increment value
            calculateIncrement(20);
            System.out.println("INCREMENT: " + increment);

            //--------iterations begin--------
            for (; minReturnConstraint < maxReturnConstraint; minReturnConstraint += increment) {

                //set return constraint to var so we can remove it from the model
                GRBConstr returnExprConstraint = efModel.addConstr(returnExpr, GRB.EQUAL, minReturnConstraint, "return constraint");

                efModel.optimize();

                System.out.println("*************target return: " + minReturnConstraint + "*************");
                System.out.println(calculateMonthlyPortfolioReturn());
                setOptimizedFundWeights();
                //SETTING THE SAME OPTIMIZED WEIGHT!! SOMETHING IS NOT BEING SAVED IN MEMRORY CORRECTLY

                optimizedPortfolios.add(new Portfolio(efFunds));

                //get weight data from our terms
                for (GRBVar var : grbVars) {
                    System.out.println(var.get(GRB.StringAttr.VarName) + " | " + var.get(GRB.DoubleAttr.X));
                }
                efModel.remove(returnExprConstraint);
                System.out.println("******************************************************************************");
            }

            //memory clean up after the method
            efModel.dispose();
            env.dispose();
        }
        catch(GRBException e){
            System.out.println("something went wrong generating an efficient frontier | " + e);
        }
    }

    private void setOptimizedMaximum(){
        try {
            //get max return constraint
            efModel.setObjective(varianceExpr, GRB.MAXIMIZE);
            efModel.set(GRB.IntParam.NonConvex, 2);

            efModel.optimize();
            maxReturnConstraint = calculateMonthlyPortfolioReturn();
            System.out.println("\n\n\nMAX RETURN CONSTRAINT:" + maxReturnConstraint);
            System.out.println("\n\n\nMAX RISK: " + calculatePortfolioRisk(varianceExpr));

            //set back to default
            efModel.set(GRB.IntParam.NonConvex, -1);
        }
        catch(GRBException e){
            System.out.println("error optimizing max | " + e);
        }
    }

    private void setOptimizedMinimum(){
        try {
            //minimize variance
            efModel.setObjective(varianceExpr, GRB.MINIMIZE);

            efModel.optimize();
            minReturnConstraint = calculateMonthlyPortfolioReturn();
        }
        catch(GRBException e){
            System.out.println("error optimizing minimum | " + e);
        }
    }

    //returns true if weights == 1
    private Boolean setOptimizedFundWeights(){
        try {
            double totalWeight = 0;
            //TODO USE GET VARBYNAME()
            for (GRBVar var : efModel.getVars()) {
                for (Fund fund : efFunds) {
                    if (var.get(GRB.StringAttr.VarName).equals(fund.getTicker())) {
                        double grbVarWeight = var.get(GRB.DoubleAttr.X);
                        fund.setWeight(grbVarWeight);
                        totalWeight += grbVarWeight;
                        //TODO MAKE THIS EFFICIENT
//                        break;
                    }
                }
            }
            return totalWeight == 1.0;
        }
        catch(GRBException e){
            System.out.println("Error setting optimized fund weights | " + e);
            return null;
        }
    }

    private Double calculateMonthlyPortfolioReturn(){
        double optimalMinimumPortfolioReturn = 0;
        try {
            for (Fund fund : efFunds) {
                GRBVar grbVar = efModel.getVarByName(fund.getTicker());
                //TODO IS THIS GET ANNUALIZED RETURN OR MEAN
                optimalMinimumPortfolioReturn += grbVar.get(GRB.DoubleAttr.X) * fund.getMean();
            }
            return optimalMinimumPortfolioReturn;
        }
        catch(GRBException e){
            System.out.println("Something went wrong calculating portfolio return from GRB Model | " + e);
            return null;
        }
    }



    private Double calculatePortfolioRisk(GRBQuadExpr varianceExpr){
        try {
            return Math.sqrt(varianceExpr.getValue());
        } catch (GRBException e) {
            System.out.println("error calculating portfolio risk | " + e);
            return null;
        }
    }

    private Double calculatePortfolioVariance(GRBQuadExpr varianceExpr){
        try {
            return varianceExpr.getValue();
        } catch (GRBException e) {
            System.out.println("error calculating portfolio variance | " + e);
            return null;
        }
    }

    private GRBQuadExpr buildVarianceExpression(){
        GRBQuadExpr varianceExpr = new GRBQuadExpr();
        //variance terms. sigma^2 * var1 * var 1
        for (int i=0; i<grbVars.length; i++){
            varianceExpr.addTerm(Math.pow(efFunds.get(i).getSigma(),2), grbVars[i], grbVars[i]);
        }

        //covariance terms. 2 * cov(1,2) * var 1 * var 2
        Portfolio efPortfolio = new Portfolio(efFunds);
        efPortfolio.pairFunds();
        efPortfolio.createCovarianceArray();
        try {
            for (Covariance covariance : efPortfolio.getCovarianceArray()) {
                varianceExpr.addTerm(2.0 * covariance.calculateCovariance(),
                        efModel.getVarByName(covariance.getFundA().getTicker()), efModel.getVarByName(covariance.getFundB().getTicker()));

            }
        }
        catch(Exception e){
            System.out.println("Something went wrong building variance expression | " + e);
        }

        return varianceExpr;
    }

    private GRBVar getVarByName(String fundName){
        try {
            for (GRBVar var : grbVars) {
                if (var.get(GRB.StringAttr.VarName).equals(fundName)) {
                    return var;
                }
            }
        }
        catch (GRBException e){
            System.out.println("error finding GRBVar by name | " + e);
        }
        return null;
    }

    private GRBLinExpr buildReturnExpression(){
        GRBLinExpr returnExpr = new GRBLinExpr();
        double[] fundMeanReturn = new double[efFunds.size()];

        for (int i =0; i<efFunds.size(); i++){
            //TODO IS THIS ANNUALIZED OR MONTHLY
            fundMeanReturn[i] = efFunds.get(i).getMean();
        }
        try {
            returnExpr.addTerms(fundMeanReturn,grbVars);
        } catch (GRBException e) {
            System.out.println("error building return expression | "+ e);
        }
        return returnExpr;
    }

    private GRBLinExpr buildSumOfWeightsExpression(){
        GRBLinExpr sumOfWeights = new GRBLinExpr();
        for (GRBVar var : grbVars){
            sumOfWeights.addTerm(1.0, var);
        }
        return sumOfWeights;
    }

    private void calculateIncrement(int numberOfPoints){
        increment = (maxReturnConstraint - minReturnConstraint) / numberOfPoints;
    }

    public ArrayList<Portfolio> getOptimizedPortfolios() {
        return optimizedPortfolios;
    }

   public static ArrayList<Fund> findEfFunds(){
       ArrayList<Fund> efFunds = new ArrayList<>();
        for (Fund fund : FundCollections.mutualFundUniverse){
            if (fund.getInclusionInEfficientFrontier()){
                efFunds.add(fund);
            }
        }
        return efFunds;
   }

   public void caclulateRiskAndReturnOfOptimizedPortfolios(){
        for( Portfolio optimizedPortfolio : optimizedPortfolios){
            optimizedPortfolio.calculateStandardDevPortfolio();
            optimizedPortfolio.calculateReturnPortfolio();
        }
   }

    public static void main(String[] args) {
        ArrayList<Fund> funds = new ArrayList<>();
        FundCollections fc = new FundCollections();
        fc.populateMutualFundUniverse();
        Importer importer = new Importer();
        importer.createCSVTickerArrayListFromDirectory();
        importer.populateFundDataFromCSV();

        for (Fund fund : FundCollections.mutualFundUniverse){
            if (fund.hasHistoricalData()){
                //TODO POPULATE FUND DATA
//                fund.calculateFundRiskReturn(fund.);
                funds.add(fund);
            }
        }
        Portfolio portfolio = new Portfolio(funds);
        try {
            portfolio.cutPortfolioStartDates();
            portfolio.calculateFundValues();
            portfolio.calculatePortfolioValues();
            portfolio.calculatePortfolioBeta();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println("done populating funds");
        EfficientFrontierCalculator efc = new EfficientFrontierCalculator(portfolio.getFundList());
        efc.generateEfficientFrontier();
    }


}
