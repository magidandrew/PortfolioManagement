import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.geom.Rectangle2D;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.panel.Overlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;

public class CrosshairOverlayDemo1 extends JFrame {
    public CrosshairOverlayDemo1(String paramString) {
        super(paramString);
        setContentPane(createDemoPanel());
    }

    public static JPanel createDemoPanel() {
        return new MyDemoPanel();
    }

    public static void main(String[] paramArrayOfString) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                CrosshairOverlayDemo1 crosshairOverlayDemo1 = new CrosshairOverlayDemo1("JFreeChart: CrosshairOverlayDemo1.java");
                crosshairOverlayDemo1.pack();
                crosshairOverlayDemo1.setVisible(true);
            }
        });
    }

    static class MyDemoPanel extends JPanel implements ChartMouseListener {
        private ChartPanel chartPanel;

        private Crosshair xCrosshair;

        private Crosshair yCrosshair;

        public MyDemoPanel() {
            super(new BorderLayout());
            JFreeChart jFreeChart = createChart(createDataset());
            this.chartPanel = new ChartPanel(jFreeChart);
            this.chartPanel.addChartMouseListener(this);
            CrosshairOverlay crosshairOverlay = new CrosshairOverlay();
            this.xCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0.0F));
            this.xCrosshair.setLabelVisible(true);
            this.yCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0.0F));
            this.yCrosshair.setLabelVisible(true);
            crosshairOverlay.addDomainCrosshair(this.xCrosshair);
            crosshairOverlay.addRangeCrosshair(this.yCrosshair);
            this.chartPanel.addOverlay((Overlay)crosshairOverlay);
            add((Component)this.chartPanel);
        }

        private JFreeChart createChart(XYDataset param1XYDataset) {
            return ChartFactory.createScatterPlot("CrosshairOverlayDemo1", "X", "Y", param1XYDataset);
        }

        private XYDataset createDataset() {
            XYSeries xYSeries = new XYSeries("S1");
            for (byte b = 0; b < 10; b++)
                xYSeries.add(b, b + Math.random() * 4.0D);
            return (XYDataset)new XYSeriesCollection(xYSeries);
        }

        public void chartMouseClicked(ChartMouseEvent param1ChartMouseEvent) {}

        public void chartMouseMoved(ChartMouseEvent param1ChartMouseEvent) {
            Rectangle2D rectangle2D = this.chartPanel.getScreenDataArea();
            JFreeChart jFreeChart = param1ChartMouseEvent.getChart();
            XYPlot xYPlot = (XYPlot)jFreeChart.getPlot();
            ValueAxis valueAxis = xYPlot.getDomainAxis();
            double d1 = valueAxis.java2DToValue(param1ChartMouseEvent.getTrigger().getX(), rectangle2D, RectangleEdge.BOTTOM);
            if (!valueAxis.getRange().contains(d1))
                d1 = Double.NaN;
            double d2 = DatasetUtilities.findYValue(xYPlot.getDataset(), 0, d1);
            this.xCrosshair.setValue(d1);
            this.yCrosshair.setValue(d2);
        }
    }
}
