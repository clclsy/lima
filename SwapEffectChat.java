package nutrisci.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

//SwapEffectChart:
 //Generates charts for UC8:
 // Bar Chart: Before vs After nutrient comparison
 // Line Chart: Nutrient trend over time
 
public class SwapEffectChart {

    /**
     * Bar Chart comparing Before vs After for each nutrient.
     */
    public static JPanel generateBarChart(Map<String, Double> beforeData, Map<String, Double> afterData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (String nutrient : beforeData.keySet()) {
            dataset.addValue(beforeData.get(nutrient), "Before Swap", nutrient);
            dataset.addValue(afterData.getOrDefault(nutrient, 0.0), "After Swap", nutrient);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Before vs After Comparison",
                "Nutrient", "Amount",
                dataset
        );

        // Styling
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(66, 135, 245));  // Blue for Before
        renderer.setSeriesPaint(1, new Color(255, 99, 71));   // Red for After
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.GRAY);
        plot.setRangeGridlinePaint(Color.GRAY);

        return new ChartPanel(chart);
    }

    /**
     * Line Chart showing nutrient trend by date (or index).
     */
    public static JPanel generateLineChart(Map<String, Double> trendData, String nutrient) {
        XYSeries series = new XYSeries(nutrient + " Trend");

        int index = 1;
        for (Map.Entry<String, Double> entry : trendData.entrySet()) {
            series.add(index++, entry.getValue());
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createXYLineChart(
                nutrient + " Trend Over Time",
                "Time (chronological)", "Amount",
                dataset
        );

        // Styling
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);
        renderer.setSeriesPaint(0, new Color(66, 135, 245)); // Blue line
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.GRAY);
        plot.setRangeGridlinePaint(Color.GRAY);

        return new ChartPanel(chart);
    }
}
