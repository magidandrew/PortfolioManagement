import javax.swing.table.AbstractTableModel;

public class FundFamilyFilterTableModel extends AbstractTableModel {
    String[] columnNames = {" ", "Investment Company"};
    private static final int BOOLEAN_COLUMN = 0;

    @Override
    //dependent on number of funds in 'universe'
    //FOR NOW, universe will be fundArr from Main class
    public int getRowCount() {
        return Main.fundList.size();
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
                return Main.fundList.get(rowIndex).getInclusionInDownload();
            case 1:
                return Main.fundList.get(rowIndex).getTicker();
            case 2:
                return Main.fundList.get(rowIndex).getName();
            case 3:
                return Main.fundList.get(rowIndex).getStartDateString();
            case 4:
                return Main.fundList.get(rowIndex).getEndDateString();
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
            Main.fundList.get(rowIndex).toggleInclusionInPortfolio();
        }

    }





}
