import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class Navigator {
    PortfolioManagementEditor pme;
    public Navigator(){
        pme = new PortfolioManagementEditor();
    }
    public void populateTree(JTree navigatorTree){
        DefaultTreeModel model = (DefaultTreeModel) navigatorTree.getModel();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("HIDDEN ROOT");
        DefaultMutableTreeNode downloadDataNode = new DefaultMutableTreeNode(pme.getDownloadCardString());
        DefaultMutableTreeNode riskAndReturn = new DefaultMutableTreeNode(pme.getCalculatePortfolioRiskReturnCardString());
        DefaultMutableTreeNode efficientFrontierNode = new DefaultMutableTreeNode(pme.getEfficientFrontierCardString());
//        DefaultMutableTreeNode myPortfolioNode = new DefaultMutableTreeNode("My Portfolios");
//        //TODO NEED CODE TO POPULATE ALL THE FUNDS
//        DefaultMutableTreeNode fundA = new DefaultMutableTreeNode("hello punk");

        root.add(downloadDataNode);
        root.add(riskAndReturn);
        root.add(efficientFrontierNode);
//        root.add(myPortfolioNode);
//
//        myPortfolioNode.add(fundA);

        model.setRoot(root);


    }
}
