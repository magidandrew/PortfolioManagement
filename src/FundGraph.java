import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JPanel;

import net.miginfocom.layout.UnitConverter;
import org.jfree.chart.*;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.CrosshairLabelGenerator;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

public class FundGraph extends ApplicationFrame{

    private static Fund fund;
    private Crosshair xCrosshair;

    private Crosshair yCrosshair;

    static {
        ChartFactory.setChartTheme(new StandardChartTheme("JFree/Shadow",
                true));
    }

    public FundGraph(String title, Fund fund) {
        super(title);
        //How to reference this fund?
        FundGraph.fund = fund;
        ChartPanel chartPanel = (ChartPanel) createDemoPanel();
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

        setContentPane(chartPanel);
    }

    private static JFreeChart createChart(XYDataset dataset) {

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                fund.getName(),  // title
                "Date",             // x-axis label
                "Price Per Unit",   // y-axis label
                dataset,            // data
                false,               // create legend?
                true,               // generate tooltips?
                false               // generate URLs?
        );


        chart.setBackgroundPaint(Color.white);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);

        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            renderer.setBaseShapesVisible(false);
            renderer.setBaseShapesFilled(false);
            renderer.setDrawSeriesLineAsPath(true);

        }

        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("MMM/yyyy"));

        return chart;

    }

    /**
     * Creates a dataset, consisting of two series of monthly data.
     *
     * @return The dataset.
     */
    private static XYDataset createDataset() {

        TimeSeries s1 = new TimeSeries(fund.getName());
        for (Map.Entry<String, Double> entry : fund.getRawHistoricalPrices().entrySet()){
            String date = entry.getKey();
            Double price = entry.getValue();

            //splitting the dates and passing them into an int array to be used in the graph
            //FROM CSV
            String[] splitDate = date.split("-",3);
            Integer[] intDate = new Integer[3];
            for (int i=0; i<splitDate.length; i++){
                intDate[i] = Integer.parseInt(splitDate[i]);
            }
            s1.add(new Month(intDate[1],intDate[0]), price);
        }


        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(s1);

        return dataset;

    }

    public JFreeChart getChart(){
        JFreeChart chart = createChart(createDataset());
        return chart;
    }



    public static JPanel createDemoPanel() {
        JFreeChart chart = createChart(createDataset());
        ChartPanel panel = new ChartPanel(chart);
        panel.setFillZoomRectangle(true);
        panel.setMouseWheelEnabled(true);
        return panel;
    }

    //driver
//    public static void main(String[] args) {
//
//        Importer importer = new Importer();
//        ArrayList<Fund> fundArr = importer.importFolder("/Users/andrewmagid/IdeaProjects/CapmModel/DadsFunds");
////        FundGraph demo = new FundGraph("Time Series Chart Demo 1", Main.fundList.get(0));
//        FundGraph demo = new FundGraph("Test Time Series", fundArr.get(0));
//
//        demo.pack();
//        RefineryUtilities.centerFrameOnScreen(demo);
//        demo.setVisible(true);
//
//    }

}


