package nutrisci.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.util.Map;

// SwapEffectChart: Handles chart rendering for UC8.
 // Generates Bar and Line charts comparing "Before" and "After" nutrient totals.
 
public class SwapEffectChart {

    public static JPanel generateBarChart(Map<String, Double> beforeData, Map<String, Double> afterData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (String nutrient : beforeData.keySet()) {
            dataset.addValue(beforeData.get(nutrient), "Before Swap", nutrient);
            dataset.addValue(afterData.getOrDefault(nutrient, 0.0), "After Swap", nutrient);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Before vs After Swap - Nutrient Comparison",
                "Nutrient", "Amount",
                dataset
        );

        return new ChartPanel(chart);
    }

    public static JPanel generateLineChart(Map<String, Double> beforeData, Map<String, Double> afterData) {
        XYSeries beforeSeries = new XYSeries("Before Swap");
        XYSeries afterSeries = new XYSeries("After Swap");

        int index = 1;
        for (String nutrient : beforeData.keySet()) {
            beforeSeries.add(index, beforeData.get(nutrient));
            afterSeries.add(index, afterData.getOrDefault(nutrient, 0.0));
            index++;
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(beforeSeries);
        dataset.addSeries(afterSeries);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Before vs After Swap - Nutrient Trend",
                "Nutrient Index", "Amount",
                dataset
        );

        return new ChartPanel(chart);
    }
}
