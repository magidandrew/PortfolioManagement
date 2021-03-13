import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.ParseException;
import java.util.ArrayList;

public class PortfolioManagementEditor extends JFrame {

    public JPanel mainPanel;
    private JPanel navigator;
    private JTable downloadTable;
    private JTree navigatorTree;
    private JPanel cardManager;
    private JPanel downloadCard;
    private JPanel efficientFrontierCard;
    private JPanel fundInfoCard;
    private JPanel downloadCardTablePanel;
    private JTextField searchBar;
    private JButton investmentCompaniesFilterButton;
    private JButton downloadButton;
    private JProgressBar progressBar1;
    private JPanel downloadInterface;
    private JPanel filterInterface;
    private JButton filterBySearchBarButton;
    private JButton deleteSelectedButton;
    private JPanel calculatePortfolioRiskReturnCard;
    private JTable portfolioRiskAndReturnTable;
    private JTextField portfolioAnnualReturnTextfield;
    private JTextField portfolioAnnaulRiskTextfield;
    private JTextField portfolioBetaTextfield;
    private JComboBox marketBenchmarkCombobox;
    private JButton calculatePortfolioRiskAndReturnButton;
    private JTextField portfolioStartDateTextfield;
    private JTextField portfolioEndDateTextfield;
    private JTextField totalWeightTextfield;
    private JComboBox comboBox1;
    private JTable efficientFrontierTable;
    private JPanel efficientFrontierGraphJPanel;
    private JPanel efficientFrontierTableJPanel;
    private JButton generateEfficientFrontierButton;
    private JButton savePortfolioButton;
    private JPanel panelContainer;
    private double portfolioTextboxWeight;

    private String searchBarText;

    private String downloadCardString = "Download Historical Prices";
    private String calculatePortfolioRiskReturnCardString = "Portfolio Risk & Return";
    private String efficientFrontierCardString = "Efficient Frontier";

    private Dimension preferredWindowDimension = new Dimension(1300,500);
    FundCollections fundCollections;
    PortfolioRiskAndReturnTableModel prartm;
    DownloadTableModel dtm;
    EfficientFrontierTableModel eftm;
    Navigator navigatorObj;

    Portfolio currentPortfolio;

    public PortfolioManagementEditor(String title){
        super(title);
        //initialize our table models
        prartm = new PortfolioRiskAndReturnTableModel();
        dtm = new DownloadTableModel();
        eftm = new EfficientFrontierTableModel();
        navigatorObj = new Navigator();

        //initializing fundCollections object for 'universe of funds'
        fundCollections = new FundCollections();

        //currentPortfolio object holds selected funds and calculation methods
        currentPortfolio = new Portfolio(FundCollections.getSelectedPortfolioFundArray());

        //initializing portfolio before TableModelEvent
        try {
            currentPortfolio.calculatePortfolioValues();
        } catch (ParseException e) {
            System.out.println("Error calculating portfolio values in constructor | ");
            e.printStackTrace();
        }

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);

        //setting table parameters through table models
        dtm.setDownloadTableParameters(downloadTable);
        prartm.setPortfolioRiskAndReturnTableParameters(portfolioRiskAndReturnTable);
        eftm.setEfficientFrontierTableParameters(efficientFrontierTable);

        //setting app in middle of screen
        setPreferredSize(preferredWindowDimension);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) screenSize.getWidth()/2;
        int y = (int) screenSize.getHeight()/2;
        setLocation(x-preferredWindowDimension.width/2,y-preferredWindowDimension.height/2);

        navigatorObj.populateTree(navigatorTree);

        navigatorTree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        navigatorTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) navigatorTree.getLastSelectedPathComponent();

                if (node == null){
                    return;
                }

                Object nodeInfo = node.toString();
                if (nodeInfo.equals(downloadCardString)){
                    showNewCard(downloadCard);
                }
                else if (nodeInfo.equals(efficientFrontierCardString)){
                    showNewCard(efficientFrontierCard);
                }
                else if (nodeInfo.equals(calculatePortfolioRiskReturnCardString)){
                    showNewCard(calculatePortfolioRiskReturnCard);

                }
                //TODO If the name is equal to any of funds that are contained in our portfolio
//                else if (nodeInfo.equals("Efficient Frontier")){
//                    cardManager.removeAll();
//                    cardManager.add(efficientFrontierCard);
//                    cardManager.repaint();
//                    cardManager.revalidate();
//                }

            }
        });
        searchBar.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                searchBar.selectAll();
            }
        });
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PythonDownloader.downloadSelectedFunds();
                downloadTable.grabFocus();
                revalidateMarketBenchmarkCombobox();
            }
        });


        filterBySearchBarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterBySearchBar();
            }
        });
        searchBar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterBySearchBar();
            }
        });

        deleteSelectedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fundCollections.deleteSelectedFunds();
                downloadTable.grabFocus();
                revalidateMarketBenchmarkCombobox();

                PortfolioRiskAndReturnTableModel prartm = (PortfolioRiskAndReturnTableModel) portfolioRiskAndReturnTable.getModel();
                prartm.fireTableDataChanged();
            }
        });

        generateEfficientFrontierButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Fund> foundEfFunds = EfficientFrontierCalculator.findEfFunds();
                if (foundEfFunds.size() == 0) {
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(
                            JOptionPane.getRootFrame(),
                            "Please select funds to generate an efficient frontier.",
                            "No funds selected",
                            JOptionPane.ERROR_MESSAGE);
                }
                else {
                    Portfolio efPortfolio = new Portfolio(foundEfFunds);

                    EfficientFrontierCalculator efc = new EfficientFrontierCalculator(efPortfolio.getFundList());
//                    EfficientFrontierCalculator efc = new EfficientFrontierCalculator(foundEfFunds);
                    efc.generateEfficientFrontier();
                    efc.caclulateRiskAndReturnOfOptimizedPortfolios();
                    ArrayList<Portfolio> optimizedPortfolios = efc.getOptimizedPortfolios();

                    for( Portfolio portfolio : optimizedPortfolios) {
                        try {
                            portfolio.cutPortfolioStartDates();
                            portfolio.calculateFundValues();
                            portfolio.calculatePortfolioValues();
                            portfolio.calculatePortfolioBeta();
                        } catch (ParseException i) {
                            i.printStackTrace();
                        }
                    }
//                    efc.caclulateRiskAndReturnOfOptimizedPortfolios();


                    ScatterGraph graph = new ScatterGraph("test", optimizedPortfolios, efficientFrontierGraphJPanel);
//                    ChartPanel cp = new ChartPanel(graph.getChart());
//                    efficientFrontierGraphJPanel.add(graph.,BorderLayout.CENTER);
                    graph.pack();
                    graph.setVisible(true);
                    efficientFrontierCard.validate();
                    efficientFrontierCard.repaint();
                }
            }
        });

        portfolioRiskAndReturnTable.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                //overwriting portfolio object with changed array
                currentPortfolio = new Portfolio(FundCollections.getSelectedPortfolioFundArray());
                //add the benchmark fund for cut dates to work for beta
                if (marketBenchmarkCombobox.getSelectedItem() != null) {
                    currentPortfolio.setMarketBenchmark((Fund) marketBenchmarkCombobox.getSelectedItem());
                }

                if (!currentPortfolio.isPortfolioEmpty()) {
                    //recalculate portfolio values on model change
                    try {
//                        for (Fund fund : FundCollections.downloadedPortfolioRiskAndReturnFunds()){
//                            fund.calculateFundRiskReturn(fund.);
//                        }
                        currentPortfolio.calculateFundValues();
                    }
                    catch(Exception i){
                        System.out.println("Error calculating portfolio values | " + i);
                    }

                    if (marketBenchmarkCombobox.getSelectedItem() != null){
                        for (Fund fund : FundCollections.getSelectedPortfolioFundArray()) {
                            fund.calculateBeta((Fund) marketBenchmarkCombobox.getSelectedItem());
                        }
                    }

//                        currentPortfolio.initializeFundsInPortfolio();
                    portfolioStartDateTextfield.setText(currentPortfolio.getPortfolioStartDateString());
                    portfolioEndDateTextfield.setText(currentPortfolio.getPorfolioEndDateString());

                    portfolioRiskAndReturnTable.validate();
                    portfolioRiskAndReturnTable.repaint();
                }
                else{
                    portfolioStartDateTextfield.setText("");
                    portfolioEndDateTextfield.setText("");
                }
                portfolioTextboxWeight = currentPortfolio.calculatePortfolioTotalWeight();
                totalWeightTextfield.setText(String.valueOf(Round.round(portfolioTextboxWeight *100,4)) +"%");

                //weight textbox color
                if (portfolioTextboxWeight == 1.0){
                    totalWeightTextfield.setBackground(Color.GREEN);
                }
                else{
                    totalWeightTextfield.setBackground(Color.RED);
                }

            }
        });

        calculatePortfolioRiskAndReturnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //will only work if weights add to 100%
                if (portfolioTextboxWeight == 1.0){
                    try {
                        //clear fund annualized risk/return so old data gets cleared in table
                        for (Fund fund : FundCollections.downloadedPortfolioRiskAndReturnFunds()){
                            fund.clearRiskReturnBetaWeight();
                        }
                        //cuts dates, individual fund calculation, annualized return, annualized risk, fund pairing, covariance array, stdev of portfolio
                        //return of portfolio, portfolio beta
                        currentPortfolio.calculatePortfolioValues();
                    } catch (ParseException parseException) {
                        parseException.printStackTrace();
                    }

                    //populating text fields
                    portfolioAnnualReturnTextfield.setText(Round.roundToString(currentPortfolio.getReturnOfPortfolio()*100) + "%");
                    portfolioAnnaulRiskTextfield.setText((Round.roundToString(currentPortfolio.getStandardDevOfPortfolio()*100) + "%"));
                    portfolioBetaTextfield.setText(Round.roundToString(currentPortfolio.getPortfolioBeta()));
                    portfolioRiskAndReturnTable.grabFocus();
                }
                else{
                    //error message if weights don't sum to 100%
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(
                            JOptionPane.getRootFrame(),
                            "Portfolio weights don't sum to 100%. Please reallocate your portfoio.",
                            "Portfolio Weight Allocation Incomplete",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //use case: user selects benchmark AFTER selecting portfolio funds
        marketBenchmarkCombobox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //overwriting portfolio object with changed array
                currentPortfolio = new Portfolio(FundCollections.getSelectedPortfolioFundArray());
                //add the benchmark fund for cut dates to work for beta
                if (marketBenchmarkCombobox.getSelectedItem() != null) {
                    currentPortfolio.setMarketBenchmark((Fund) marketBenchmarkCombobox.getSelectedItem());
                }

                if (!currentPortfolio.isPortfolioEmpty()) {
                    //recalculate portfolio values on model change
                    try {
//                        for (Fund fund : FundCollections.downloadedPortfolioRiskAndReturnFunds()){
//                            fund.calculateFundRiskReturn(fund.);
//                        }
                        currentPortfolio.calculateFundValues();

                        //update cut dates with selection of the market benchmark
                        portfolioStartDateTextfield.setText(currentPortfolio.getPortfolioStartDateString());
                        portfolioEndDateTextfield.setText(currentPortfolio.getPorfolioEndDateString());
                    } catch (Exception i) {
                        System.out.println("Error calculating portfolio values | " + i);
                    }

                    if (marketBenchmarkCombobox.getSelectedItem() != null) {
                        for (Fund fund : FundCollections.getSelectedPortfolioFundArray()) {
                            fund.calculateBeta((Fund) marketBenchmarkCombobox.getSelectedItem());
                        }
                    }
                }

                portfolioRiskAndReturnTable.validate();
                portfolioRiskAndReturnTable.repaint();
            }
        });

    }

    public PortfolioManagementEditor() {
        //accessing methods

    }

    private void createUIComponents() {
        searchBar = new HintTextField("Search:");
//        marketBenchmarkCombobox = new JComboBox(FundCollections.downloadedPortfolioRiskAndReturnFunds().toArray());
        marketBenchmarkCombobox = new JComboBox(FundCollections.fundsForMarketBenchmark().toArray());
        revalidateMarketBenchmarkCombobox();
        marketBenchmarkCombobox.setSelectedIndex(-1);
    }

    public String getSearchBarText(){
        return searchBarText;
    }

    private void filterBySearchBar(){
        //confusing why this is here. why am i setting a private variable in this method
        //to call a method outside of this one that will return this same private variable?
        searchBarText = searchBar.getText();

        fundCollections.populateFilteredMutualFundUniverseBySearchBar(getSearchBarText());
        if (FundCollections.filteredMutualFundUniverse.size() == 0){
            dtm = (DownloadTableModel) downloadTable.getModel();
            try {
                dtm.fireTableRowsDeleted(0,0);
            }
            catch(IndexOutOfBoundsException ignored){
                System.out.println("Fire Table Rows Deleted. Error: " + ignored);
            }
        }
        else{
            dtm = (DownloadTableModel) downloadTable.getModel();
            dtm.fireTableDataChanged();
        }
    }

    private void showNewCard(JPanel thisCard){
        cardManager.removeAll();
        cardManager.add(thisCard);
        cardManager.repaint();
        cardManager.revalidate();
    }

    private void revalidateMarketBenchmarkCombobox(){
//        DefaultComboBoxModel defaultComboBoxModel = new DefaultComboBoxModel(FundCollections.downloadedPortfolioRiskAndReturnFunds().toArray());
        DefaultComboBoxModel defaultComboBoxModel = new DefaultComboBoxModel(FundCollections.fundsForMarketBenchmark().toArray());

        marketBenchmarkCombobox.setModel(defaultComboBoxModel);
    }



    //getter methods
    public String getDownloadCardString() {
        return downloadCardString;
    }

    public String getCalculatePortfolioRiskReturnCardString() {
        return calculatePortfolioRiskReturnCardString;
    }

    public String getEfficientFrontierCardString() {
        return efficientFrontierCardString;
    }


}
