import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

public class EfficientFrontierTableModel extends AbstractTableModel{

    String[] columnNames = {" ", "Ticker", "Name","Min Weight (%)",
            "Max Weight (%)", /*"Optimal Weights"*/};
    private static final int BOOLEAN_COLUMN = 0;
    private static final int MIN_WEIGHT_COLUMN = 3;
    private static final int MAX_WEIGHT_COLUMN = 4;


    private ArrayList<Fund> fundsInEfficientFrontierTableModel;

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
        boolean toggled = fundsInEfficientFrontierTableModel.get(rowIndex).getInclusionInEfficientFrontier();
        switch(columnIndex){
            case 0:
                return fundsInEfficientFrontierTableModel.get(rowIndex).getInclusionInEfficientFrontier();
            case 1:
                return fundsInEfficientFrontierTableModel.get(rowIndex).getTicker();
            case 2:
                return fundsInEfficientFrontierTableModel.get(rowIndex).getName();
            case 3:
                if (toggled){
                    return fundsInEfficientFrontierTableModel.get(rowIndex).getMinWeight();
                }
                else{
                    return 0.0;
                }
            case 4:
                if (toggled){
                    return fundsInEfficientFrontierTableModel.get(rowIndex).getMaxWeight();
                }
                else{
                    return 0.0;
                }
//            case 5:
//                if (toggled) {
//                    return fundsInPortfolioRiskAndReturnTableModel.get(rowIndex).getOptimalWeight();
//                }
//                else{
//                    return "";
//                }
            default:
                return "";
        }
    }

    public Class getColumnClass(int c) {
        //todo: what to do when no downloaded funds are selected?
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
        boolean toggled = fundsInEfficientFrontierTableModel.get(rowIndex).getInclusionInEfficientFrontier();
        if (toggled){
            return columnIndex == BOOLEAN_COLUMN || columnIndex == MIN_WEIGHT_COLUMN || columnIndex == MAX_WEIGHT_COLUMN;
        }
        else{
            return columnIndex == BOOLEAN_COLUMN;
        }
    }

    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (columnIndex == BOOLEAN_COLUMN) {
            fundsInEfficientFrontierTableModel.get(rowIndex).toggleInclusionInEfficientFrontier();
            fireTableCellUpdated(rowIndex,columnIndex);
        }
        if (columnIndex == MIN_WEIGHT_COLUMN){
            fundsInEfficientFrontierTableModel.get(rowIndex).setMinWeight((Double) value);
            fireTableCellUpdated(rowIndex,columnIndex);
        }
        if (columnIndex == MAX_WEIGHT_COLUMN){
            fundsInEfficientFrontierTableModel.get(rowIndex).setMaxWeight((Double) value);
            fireTableCellUpdated(rowIndex,columnIndex);
        }

    }

    public ArrayList<Fund> createDownloadedFundArray(){
        fundsInEfficientFrontierTableModel = new ArrayList<>();
        for (Fund fund : FundCollections.mutualFundUniverse){
            if (fund.hasHistoricalData()){
                fundsInEfficientFrontierTableModel.add(fund);
            }
        }
        if (fundsInEfficientFrontierTableModel.isEmpty()){
            fundsInEfficientFrontierTableModel.add(null);
        }
        return fundsInEfficientFrontierTableModel;
    }



    public void setEfficientFrontierTableParameters(JTable efficientFrontierTable){
        int defaultInclusionPortfolioWidth = 20;
        int defaultTickerWidth = 65;
        int defaultNameWidth = 450;
        int defaultWeightWidth = 125;
        Color greyedOutColor = new Color(242,242,242);

        EfficientFrontierTableModel efficientFrontierTableModel = new EfficientFrontierTableModel();

        DefaultTableCellRenderer greyColumnRenderer = new DefaultTableCellRenderer();
        greyColumnRenderer.setBackground(greyedOutColor);

        DefaultTableCellRenderer greyColumnRightRenderer = new DefaultTableCellRenderer();
        greyColumnRightRenderer.setBackground(greyedOutColor);
        greyColumnRightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        efficientFrontierTable.setModel(efficientFrontierTableModel);

        //column widths
        efficientFrontierTable.getColumnModel().getColumn(0).setMaxWidth(defaultInclusionPortfolioWidth);
        efficientFrontierTable.getColumnModel().getColumn(0).setMinWidth(defaultInclusionPortfolioWidth);
        efficientFrontierTable.getColumnModel().getColumn(1).setMinWidth(defaultTickerWidth);
        efficientFrontierTable.getColumnModel().getColumn(1).setMaxWidth(defaultTickerWidth);
        efficientFrontierTable.getColumnModel().getColumn(2).setPreferredWidth(defaultNameWidth);
//        efficientFrontierTable.getColumnModel().getColumn(2).setMaxWidth(defaultNameWidth+100);
        efficientFrontierTable.getColumnModel().getColumn(3).setMaxWidth(defaultWeightWidth);
        efficientFrontierTable.getColumnModel().getColumn(3).setMinWidth(defaultWeightWidth);
        efficientFrontierTable.getColumnModel().getColumn(4).setMaxWidth(defaultWeightWidth);
        efficientFrontierTable.getColumnModel().getColumn(4).setMinWidth(defaultWeightWidth);


        //greyed out columns
        efficientFrontierTable.getColumnModel().getColumn(1).setCellRenderer(greyColumnRenderer);
        efficientFrontierTable.getColumnModel().getColumn(2).setCellRenderer(greyColumnRenderer);
//        portfolioRiskAndReturnTable.getColumnModel().getColumn(5).setCellRenderer(greyColumnRightRenderer);


    }

}
