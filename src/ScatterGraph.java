import org.jfree.chart.*;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.panel.Overlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

public class ScatterGraph extends ApplicationFrame{

    private static Fund fund;
    private static ArrayList<Portfolio> optimizedPortfolios;
//    private static ChartPanel panel;
//    private static Crosshair xCrosshair;
//    private static Crosshair yCrosshair;

    static {
        ChartFactory.setChartTheme(new StandardChartTheme("JFree/Shadow",
                true));
    }

    public ScatterGraph(String title, ArrayList<Portfolio> optimizedPortfolios, JPanel panel) {
        super(title);
        //How to reference this fund?
        ScatterGraph.optimizedPortfolios = optimizedPortfolios;
//        ChartPanel chartPanel = (ChartPanel) createDemoPanel();
//        chartPanel.setPreferredSize(new Dimension(500, 270));
//
        setContentPane(createPanel(panel));
    }
    public static JPanel createPanel(JPanel panel){
        return new MyPanel(panel);
    }

    static class MyPanel extends JPanel implements ChartMouseListener{
        private ChartPanel chartPanel;
        private Crosshair xCrosshair;
        private Crosshair yCrosshair;
        private JFreeChart jFreeChart;

        public MyPanel(JPanel panel){
            jFreeChart = createChart(createDataset());
            this.chartPanel = new ChartPanel(jFreeChart);
            this.chartPanel.addChartMouseListener(this); //IS THIS RIGHT??

            CrosshairOverlay crosshairOverlay = new CrosshairOverlay();
            xCrosshair = new Crosshair(Double.NaN, Color.GRAY,new BasicStroke(0.0F));
            xCrosshair.setLabelVisible(true);
            yCrosshair = new Crosshair(Double.NaN, Color.GRAY,new BasicStroke(0.0F));
            yCrosshair.setLabelVisible(true);

            crosshairOverlay.addDomainCrosshair(xCrosshair);
            crosshairOverlay.addRangeCrosshair(yCrosshair);
            chartPanel.addOverlay((Overlay) crosshairOverlay);

            XYPlot plot = (XYPlot) jFreeChart.getPlot();
//            plot.setBackgroundPaint(Color.lightGray);
//            plot.setDomainGridlinePaint(Color.white);
//            plot.setRangeGridlinePaint(Color.white);
//            plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
            plot.setDomainCrosshairVisible(true);
            plot.setRangeCrosshairVisible(true);


            XYDotRenderer xyDotRenderer = new XYDotRenderer();
            xyDotRenderer.setDotHeight(4);
            xyDotRenderer.setDotWidth(4);

            plot.setRenderer((XYItemRenderer)xyDotRenderer);
            panel.add(chartPanel,BorderLayout.CENTER);

        }

        @Override
        public void chartMouseClicked(ChartMouseEvent chartMouseEvent){

            Rectangle2D rectangle2D = chartPanel.getScreenDataArea();
            JFreeChart jFreeChart = chartMouseEvent.getChart();
            XYPlot plot = (XYPlot) jFreeChart.getPlot();
            ValueAxis valueAxis = plot.getDomainAxis();
            xCrosshair.setValue(plot.getDomainCrosshairValue());
            yCrosshair.setValue(plot.getRangeCrosshairValue());
            System.out.println(plot.getDomainCrosshairValue());
            System.out.println(plot.getRangeCrosshairValue());
            System.out.println("------------");
            try {
                Robot bot = new Robot();
                bot.keyPress(InputEvent.BUTTON1_DOWN_MASK);
                bot.keyRelease(InputEvent.BUTTON1_DOWN_MASK);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void chartMouseMoved(ChartMouseEvent chartMouseEvent) {
        }

        private JFreeChart createChart(XYDataset xyDataset){
            return ChartFactory.createScatterPlot("Efficient Frontier","Risk","Return", xyDataset);
        }

        private static XYDataset createDataset() {

            XYSeriesCollection dataset = new XYSeriesCollection();
            XYSeries s1 = new XYSeries("portfolios");

//            for (Map.Entry<String, Double> entry : fund.getRawHistoricalPrices().entrySet()){
//                String date = entry.getKey();
//                Double price = entry.getValue();
//
//                //splitting the dates and passing them into an int array to be used in the graph
//                //FROM CSV
//                String[] splitDate = date.split("-",3);
//                Integer[] intDate = new Integer[3];
//                for (int i=0; i<splitDate.length; i++){
//                    intDate[i] = Integer.parseInt(splitDate[i]);
//                }
//                s1.add(intDate[1], price);
//            }

            for (Portfolio portfolio : optimizedPortfolios){
                s1.add(portfolio.getStandardDevOfPortfolio(),portfolio.getReturnOfPortfolio());
            }

                dataset.addSeries(s1);

            return dataset;

        }
    }
}


//    private static JFreeChart createChart(XYDataset dataset) {
//
//        JFreeChart chart = ChartFactory.createScatterPlot(
//                fund.getName(),  // title
//                "Date",             // x-axis label
//                "Price Per Unit",   // y-axis label
//                dataset
//        );
//
//
//        chart.setBackgroundPaint(Color.white);
//
//        XYPlot plot = (XYPlot) chart.getPlot();
//        plot.setBackgroundPaint(Color.lightGray);
//        plot.setDomainGridlinePaint(Color.white);
//        plot.setRangeGridlinePaint(Color.white);
//        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
//        plot.setDomainCrosshairVisible(true);
//        plot.setRangeCrosshairVisible(true);
//
//        XYDotRenderer xyDotRenderer = new XYDotRenderer();
//        xyDotRenderer.setDotHeight(4);
//        xyDotRenderer.setDotWidth(4);
//
//        plot.setRenderer((XYItemRenderer)xyDotRenderer);
//
//
//        CrosshairOverlay crosshairOverlay = new CrosshairOverlay();
//        xCrosshair = new Crosshair(Double.NaN, Color.GRAY,new BasicStroke(0.0F));
//        xCrosshair.setLabelVisible(true);
//        yCrosshair = new Crosshair(Double.NaN, Color.GRAY,new BasicStroke(0.0F));
//        yCrosshair.setLabelVisible(true);
//
//        crosshairOverlay.addDomainCrosshair(xCrosshair);
//        crosshairOverlay.addRangeCrosshair(yCrosshair);
//        panel.addOverlay((Overlay) crosshairOverlay);
//
//        NumberAxis numberAxis = (NumberAxis)plot.getDomainAxis();
//        numberAxis.setAutoRangeIncludesZero(false);
//
////        XYItemRenderer r = plot.getRenderer();
////        if (r instanceof XYLineAndShapeRenderer) {
////            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
////            renderer.setBaseShapesVisible(false);
////            renderer.setBaseShapesFilled(false);
////            renderer.setDrawSeriesLineAsPath(true);
////
////        }
//
////        DateAxis axis = (DateAxis) plot.getDomainAxis();
////        axis.setDateFormatOverride(new SimpleDateFormat("MMM/yyyy"));
//
//        return chart;
//
//    }
//
//    /**
//     * Creates a dataset, consisting of two series of monthly data.
//     *
//     * @return The dataset.
//     */
//    private static XYDataset createDataset() {
//
//        XYSeriesCollection dataset = new XYSeriesCollection();
//        XYSeries s1 = new XYSeries(fund.getName());
//
//        for (Map.Entry<String, Double> entry : fund.getRawHistoricalPrices().entrySet()){
//            String date = entry.getKey();
//            Double price = entry.getValue();
//
//            //splitting the dates and passing them into an int array to be used in the graph
//            //FROM CSV
//            String[] splitDate = date.split("-",3);
//            Integer[] intDate = new Integer[3];
//            for (int i=0; i<splitDate.length; i++){
//                intDate[i] = Integer.parseInt(splitDate[i]);
//            }
//            s1.add(intDate[1], price);
//        }
//
//        dataset.addSeries(s1);
//
//        return dataset;
//
//    }
//
//    public JFreeChart getChart(){
//        JFreeChart chart = createChart(createDataset());
//        return chart;
//    }
//
//
//    public JPanel createDemoPanel() {
//
//
//        JFreeChart chart = createChart(createDataset());
//        panel = new ChartPanel(chart);
//        panel.setFillZoomRectangle(true);
//        panel.setMouseWheelEnabled(true);
//
//
//        return panel;
//    }
//
//
//    @Override
//    public void chartMouseClicked(ChartMouseEvent chartMouseEvent) {
//
//    }
//
//    @Override
//    public void chartMouseMoved(ChartMouseEvent chartMouseEvent) {
//        Rectangle2D rectangle2D = panel.getScreenDataArea();
//        JFreeChart jFreeChart = chartMouseEvent.getChart();
//        XYPlot xyPlot = (XYPlot) jFreeChart.getPlot();
//        ValueAxis valueAxis = xyPlot.getDomainAxis();
//        double d1 = valueAxis.java2DToValue(chartMouseEvent.getTrigger().getX(), rectangle2D, RectangleEdge.BOTTOM);
//        if (!valueAxis.getRange().contains(d1))
//            d1 = Double.NaN;
//        double d2 = DatasetUtilities.findYValue(xyPlot.getDataset(),0, d1);
//        xCrosshair.setValue(d1);
//        yCrosshair.setValue(d2);
//
//    }
//}


