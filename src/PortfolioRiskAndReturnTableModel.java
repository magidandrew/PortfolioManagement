import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

public class PortfolioRiskAndReturnTableModel extends AbstractTableModel{

    String[] columnNames = {" ", "Ticker", "Name","Weight (%)", "Annual Return (%)",
            "Annual Risk (%)" , "Beta", "Start Date","End Date"};
    private static final int BOOLEAN_COLUMN = 0;
    private static final int WEIGHT_COLUMN = 3;

    private ArrayList<Fund> fundsInPortfolioRiskAndReturnTableModel;

    @Override
    public int getRowCount() {
        return createDownloadedFundArray().size();
    }

    @Override
    //bool, ticker, name, weight
    public int getColumnCount() {
        return columnNames.length;
    }

    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        createDownloadedFundArray();
        boolean toggled = false;
        try{
            toggled = fundsInPortfolioRiskAndReturnTableModel.get(rowIndex).getInclusionInPortfolio();
        }
        catch(Exception e) {
            System.err.println(e);
            System.err.println("Toggled boolean not selected since no funds downloaded");
            return "";
        }
        switch(columnIndex){
            case 0:
                return fundsInPortfolioRiskAndReturnTableModel.get(rowIndex).getInclusionInPortfolio();
            case 1:
                return fundsInPortfolioRiskAndReturnTableModel.get(rowIndex).getTicker();
            case 2:
                return fundsInPortfolioRiskAndReturnTableModel.get(rowIndex).getName();
            case 3:
                if (toggled){
                    return fundsInPortfolioRiskAndReturnTableModel.get(rowIndex).getWeight();
                }
                else{
                    return 0.0;
                }
            case 4:
                if (toggled){
                    return Round.roundToString(fundsInPortfolioRiskAndReturnTableModel.get(rowIndex).getAnnualizedReturn() * 100);
                }
                else{
                    return "";
                }
            case 5:
                if (toggled) {
                    return Round.roundToString(fundsInPortfolioRiskAndReturnTableModel.get(rowIndex).getAnnualizedRisk() * 100);
                }
                else{
                    return "";
                }
            case 6:
                if (toggled) {
                    return Round.roundToString(fundsInPortfolioRiskAndReturnTableModel.get(rowIndex).getBeta());
                }
                else{
                    return "";
                }
            case 7:
                return fundsInPortfolioRiskAndReturnTableModel.get(rowIndex).getStartDateString();
            case 8:
                return fundsInPortfolioRiskAndReturnTableModel.get(rowIndex).getEndDateString();
            default:
                return "";
        }
    }

    public Class getColumnClass(int c) {
        //check if any funds are downloaded (ie. there is data to search)
        if(FundCollections.downloadedPortfolioRiskAndReturnFunds().size() == 0){
            return String.class;
        }
        else{
            return getValueAt(0, c).getClass();
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        //true only if bool in 1st column or weight in 3rd column
        boolean toggled = false;
        try{
            toggled = fundsInPortfolioRiskAndReturnTableModel.get(rowIndex).getInclusionInPortfolio();
        }
        //this is a very messy solution. todo: find a way to display an empty table when no funds downloaded
        catch(Exception e){
            System.err.println(e);
            System.err.println("Toggled boolean not selected since no funds downloaded");
            return false;
        }
        if (toggled){
            return columnIndex == BOOLEAN_COLUMN || columnIndex == WEIGHT_COLUMN;
        }
        else{
            return columnIndex == BOOLEAN_COLUMN;
        }
    }

    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (columnIndex == BOOLEAN_COLUMN) {
            fundsInPortfolioRiskAndReturnTableModel.get(rowIndex).toggleInclusionInPortfolio();
            //todo: do i need this updating?
//            fireTableCellUpdated(rowIndex,columnIndex);
            fireTableDataChanged();
        }
        if (columnIndex == WEIGHT_COLUMN){
            fundsInPortfolioRiskAndReturnTableModel.get(rowIndex).setWeight((Double) value);
            //todo: do i need this updating?
//            fireTableCellUpdated(rowIndex,columnIndex);
            fireTableDataChanged();

        }

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



    public void setPortfolioRiskAndReturnTableParameters(JTable portfolioRiskAndReturnTable){
        int defaultInclusionPortfolioWidth = 20;
        int defaultTickerWidth = 65;
        int defaultNameWidth = 450;
        int defaultWeightWidth = 65;
        int defaultAnnualWidth = 100;
        Color greyedOutColor = new Color(242,242,242);

        PortfolioRiskAndReturnTableModel portfolioRiskAndReturnTableModel = new PortfolioRiskAndReturnTableModel();
        DefaultTableCellRenderer greyColumnRenderer = new DefaultTableCellRenderer();
        greyColumnRenderer.setBackground(greyedOutColor);

        DefaultTableCellRenderer greyColumnRightRenderer = new DefaultTableCellRenderer();
        greyColumnRightRenderer.setBackground(greyedOutColor);
        greyColumnRightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        portfolioRiskAndReturnTable.setModel(portfolioRiskAndReturnTableModel);

        //column widths
        portfolioRiskAndReturnTable.getColumnModel().getColumn(0).setMaxWidth(defaultInclusionPortfolioWidth);
        portfolioRiskAndReturnTable.getColumnModel().getColumn(0).setMinWidth(defaultInclusionPortfolioWidth);
        portfolioRiskAndReturnTable.getColumnModel().getColumn(1).setMinWidth(defaultTickerWidth);
        portfolioRiskAndReturnTable.getColumnModel().getColumn(1).setMaxWidth(defaultTickerWidth);
        portfolioRiskAndReturnTable.getColumnModel().getColumn(2).setPreferredWidth(defaultNameWidth);
        portfolioRiskAndReturnTable.getColumnModel().getColumn(2).setMaxWidth(defaultNameWidth+100);
        portfolioRiskAndReturnTable.getColumnModel().getColumn(3).setMaxWidth(defaultWeightWidth);

        portfolioRiskAndReturnTable.getColumnModel().getColumn(4).setPreferredWidth(defaultAnnualWidth);
        portfolioRiskAndReturnTable.getColumnModel().getColumn(4).setMinWidth(defaultAnnualWidth-50);
//        portfolioRiskAndReturnTable.getColumnModel().getColumn(4).setMaxWidth(defaultAnnualWidth+20);

        portfolioRiskAndReturnTable.getColumnModel().getColumn(5).setPreferredWidth(defaultAnnualWidth);
        portfolioRiskAndReturnTable.getColumnModel().getColumn(5).setMinWidth(defaultAnnualWidth-50);
//        portfolioRiskAndReturnTable.getColumnModel().getColumn(5).setMaxWidth(defaultAnnualWidth+20);

        portfolioRiskAndReturnTable.getColumnModel().getColumn(6).setMaxWidth(defaultAnnualWidth);

        //horizontal alignment
        portfolioRiskAndReturnTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        //greyed out columns
        portfolioRiskAndReturnTable.getColumnModel().getColumn(1).setCellRenderer(greyColumnRenderer);
        portfolioRiskAndReturnTable.getColumnModel().getColumn(2).setCellRenderer(greyColumnRenderer);
        portfolioRiskAndReturnTable.getColumnModel().getColumn(4).setCellRenderer(greyColumnRightRenderer);
        portfolioRiskAndReturnTable.getColumnModel().getColumn(5).setCellRenderer(greyColumnRightRenderer);
        portfolioRiskAndReturnTable.getColumnModel().getColumn(6).setCellRenderer(greyColumnRightRenderer);
        portfolioRiskAndReturnTable.getColumnModel().getColumn(7).setCellRenderer(greyColumnRenderer);
        portfolioRiskAndReturnTable.getColumnModel().getColumn(8).setCellRenderer(greyColumnRenderer);

    }

}
