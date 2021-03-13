import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class PortfolioRiskAndReturnDefaultTableModel {

    DefaultTableModel prardtm;
    private ArrayList<Fund> fundsInPortfolioRiskAndReturnTableModel;

    public PortfolioRiskAndReturnDefaultTableModel(){
        prardtm = new DefaultTableModel();
        createDownloadedFundArray();

        ArrayList<String> tickers = new ArrayList<>();
        fundsInPortfolioRiskAndReturnTableModel.forEach( fund -> tickers.add(fund.getTicker()));
        prardtm.addColumn("Ticker",tickers.toArray());
    }


    public ArrayList<Fund> createDownloadedFundArray(){
        fundsInPortfolioRiskAndReturnTableModel = new ArrayList<>();
        for (Fund fund : FundCollections.mutualFundUniverse){
            if (fund.hasHistoricalData()){
                fundsInPortfolioRiskAndReturnTableModel.add(fund);
            }
        }
        if (fundsInPortfolioRiskAndReturnTableModel.isEmpty()){
            fundsInPortfolioRiskAndReturnTableModel.add(null);
        }
        return fundsInPortfolioRiskAndReturnTableModel;
    }

}
