import javax.swing.*;
import javax.swing.table.AbstractTableModel;

public class DownloadTableModel extends AbstractTableModel {
    String[] columnNames = {" ", "Ticker", "Name","Start Date","End Date"};
    private static final int BOOLEAN_COLUMN = 0;

    @Override
    //dependent on number of funds in 'universe'
    //FOR NOW, universe will be fundArr from Main class
    //TODO change Main.fundList to FundCollections.mutualFundUniverse
    public int getRowCount() {

//        return Main.fundList.size();
        return FundCollections.filteredMutualFundUniverse.size();
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
        switch(columnIndex){
            case 0:
//                return FundCollections.filteredMutualFundUniverse.get(rowIndex).getInclusionInDownload();
                return FundCollections.tickersToDownload.contains(FundCollections.filteredMutualFundUniverse.get(rowIndex).getTicker());
            case 1:
                return FundCollections.filteredMutualFundUniverse.get(rowIndex).getTicker();
            case 2:
                return FundCollections.filteredMutualFundUniverse.get(rowIndex).getName();
            case 3:
                return FundCollections.filteredMutualFundUniverse.get(rowIndex).getStartDateString();
            case 4:
                return FundCollections.filteredMutualFundUniverse.get(rowIndex).getEndDateString();
            default:
                return null;
        }
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        //true only if bool in 1st column or weight in 2nd column
        return columnIndex == BOOLEAN_COLUMN;
    }


    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        //value = getValueAt(rowIndex,columnIndex);
        //fireTableCellUpdated(rowIndex, columnIndex);
        if (columnIndex == BOOLEAN_COLUMN) {
//            Main.fundList.get(rowIndex).toggleInclusionInPortfolio();
            if (!FundCollections.tickersToDownload.contains((String)getValueAt(rowIndex,1))) {
                FundCollections.tickersToDownload.add((String) getValueAt(rowIndex, 1));
            }
            else{
                FundCollections.tickersToDownload.remove((String) getValueAt(rowIndex,1));
            }

        }


//            if (columnIndex == TICKER_COLUMN) {
//                Main.fundList.get(rowIndex).setWeight((double) value);
//            }
    }


    public void setDownloadTableParameters(JTable downloadTable){
        int defaultInclusionPortfolioWidth = 20;
        int defaultTickerWidth = 65;
        int defaultNameWidth = 450;

        DownloadTableModel downloadTableModel = new DownloadTableModel();
        downloadTable.setModel(downloadTableModel);
        downloadTable.getColumnModel().getColumn(0).setMaxWidth(defaultInclusionPortfolioWidth);
        downloadTable.getColumnModel().getColumn(0).setMinWidth(defaultInclusionPortfolioWidth);
        downloadTable.getColumnModel().getColumn(1).setMinWidth(defaultTickerWidth);
        downloadTable.getColumnModel().getColumn(1).setMaxWidth(defaultTickerWidth);
        downloadTable.getColumnModel().getColumn(2).setPreferredWidth(defaultNameWidth);
//        downloadTable.getColumnModel().getColumn(2).setMinWidth(defaultNameWidth);

    }




}

