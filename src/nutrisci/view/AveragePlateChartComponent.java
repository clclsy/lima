package nutrisci.view;

import nutrisci.db.FoodGroupAggregator;
import nutrisci.model.UserProfile;
import nutrisci.template.Styles;
import nutrisci.util.FoodGuideConstants;
import nutrisci.util.FoodGuideConstants.GuideVersion;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class AveragePlateChartComponent extends JPanel {
    private final UserProfile profile;
    private final JComboBox<String> versionSelector;
    private final JPanel chartHolder;
    private final JLabel statusLabel;

    private static final Map<String, Color> FOOD_GROUP_COLORS = new HashMap<>();
    static {
        FOOD_GROUP_COLORS.put("Vegetables & Fruits", new Color(76, 175, 80));
        FOOD_GROUP_COLORS.put("Grain Products", new Color(255, 193, 7));
        FOOD_GROUP_COLORS.put("Whole Grains", new Color(255, 193, 7));
        FOOD_GROUP_COLORS.put("Milk & Alternatives", new Color(33, 150, 243));
        FOOD_GROUP_COLORS.put("Meat & Alternatives", new Color(244, 67, 54));
        FOOD_GROUP_COLORS.put("Protein Foods", new Color(244, 67, 54));
        FOOD_GROUP_COLORS.put("Other", new Color(158, 158, 158));
    }

    public AveragePlateChartComponent(UserProfile profile){
        this.profile = profile;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(350, 300));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel header = new JLabel("Canada Food Guide Alignment", JLabel.CENTER);
        header.setFont(Styles.default_font);
        add(header, BorderLayout.NORTH);

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBackground(Color.WHITE);
        versionSelector = new JComboBox<>(new String[]{"CFG2019", "CFG2007"});
        versionSelector.addActionListener(e -> renderCharts());
        controlPanel.add(new JLabel("Guide: "));
        controlPanel.add(versionSelector);

        statusLabel = new JLabel("Analyzing last 7 days...", JLabel.CENTER);
        statusLabel.setFont(Styles.ismall_font);
        statusLabel.setForeground(Color.GRAY);
        controlPanel.add(statusLabel);

        add(controlPanel, BorderLayout.SOUTH);

        chartHolder = new JPanel(new GridLayout(1, 2, 10, 0));
        chartHolder.setBackground(Color.WHITE);
        add(chartHolder, BorderLayout.CENTER);

        renderCharts();
    }

    private void renderCharts(){
        chartHolder.removeAll();
        GuideVersion version = versionSelector.getSelectedItem().equals("CFG2007") ? 
            GuideVersion.CFG2007 : GuideVersion.CFG2019;
        
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(6);
        Map<String, Double> grams = FoodGroupAggregator.aggregate(profile.getId(), start, end);
        
        Map<String, Double> userPct = toPercentages(grams, version);
        Map<String, Double> guidePct = FoodGuideConstants.getGuide(version);

        if(grams.isEmpty() || grams.values().stream().mapToDouble(Double::doubleValue).sum() == 0) {
            chartHolder.add(createEmptyChart("Your Plate", "Log some meals to see your data"));
            statusLabel.setText("No meals logged in last 7 days");
        } else {
            chartHolder.add(createChart(userPct, "Your Plate", true));
            statusLabel.setText("Based on " + grams.values().stream().mapToDouble(Double::doubleValue).sum() + "g total food");
        }
        
        chartHolder.add(createChart(guidePct, "Recommended", false));
        chartHolder.revalidate();
        chartHolder.repaint();
    }

    private JPanel createEmptyChart(String title, String message) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(title));
        
        JLabel label = new JLabel("<html><center>" + message + "</center></html>", JLabel.CENTER);
        label.setFont(Styles.ismall_font);
        label.setForeground(Color.GRAY);
        panel.add(label, BorderLayout.CENTER);
        
        return panel;
    }

    private ChartPanel createChart(Map<String, Double> data, String title, boolean showPercentages){
        DefaultPieDataset dataset = new DefaultPieDataset();
        for(Map.Entry<String, Double> entry : data.entrySet()) {
            if(entry.getValue() > 0) {
                dataset.setValue(entry.getKey(), entry.getValue());
            }
        }

        JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, true, false);
        
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setShadowPaint(null);
        
        for(String foodGroup : FOOD_GROUP_COLORS.keySet()) {
            if(data.containsKey(foodGroup)) {
                plot.setSectionPaint(foodGroup, FOOD_GROUP_COLORS.get(foodGroup));
            }
        }

        if(showPercentages) {
            plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0}: {2}", new DecimalFormat("0"), new DecimalFormat("0%")));
        } else {
            plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0}: {1}%", new DecimalFormat("0"), new DecimalFormat("0")));
        }
        
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 10));
        plot.setLabelPaint(Color.BLACK);
        plot.setLabelBackgroundPaint(new Color(255, 255, 255, 200));
        
        chart.getLegend().setItemFont(new Font("SansSerif", Font.PLAIN, 10));
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(150, 180));
        return chartPanel;
    }

    private Map<String, Double> toPercentages(Map<String, Double> grams, GuideVersion version){
        Map<String, Double> pct = new HashMap<>();
        
        if(version == GuideVersion.CFG2019){
            double veg = grams.getOrDefault("Vegetables & Fruits", 0.0);
            double grain = grams.getOrDefault("Grain Products", 0.0);
            double protein = grams.getOrDefault("Meat & Alternatives", 0.0) + 
                           grams.getOrDefault("Milk & Alternatives", 0.0);
            double other = grams.getOrDefault("Other", 0.0);
            double total = veg + grain + protein + other;
            
            if(total > 0) {
                pct.put("Vegetables & Fruits", (veg / total) * 100);
                pct.put("Whole Grains", (grain / total) * 100);
                pct.put("Protein Foods", (protein / total) * 100);
                if(other > 0) pct.put("Other", (other / total) * 100);
            }
        } else {
            double veg = grams.getOrDefault("Vegetables & Fruits", 0.0);
            double grain = grams.getOrDefault("Grain Products", 0.0);
            double milk = grams.getOrDefault("Milk & Alternatives", 0.0);
            double meat = grams.getOrDefault("Meat & Alternatives", 0.0);
            double other = grams.getOrDefault("Other", 0.0);
            double total = veg + grain + milk + meat + other;
            
            if(total > 0) {
                pct.put("Vegetables & Fruits", (veg / total) * 100);
                pct.put("Grain Products", (grain / total) * 100);
                pct.put("Milk & Alternatives", (milk / total) * 100);
                pct.put("Meat & Alternatives", (meat / total) * 100);
                if(other > 0) pct.put("Other", (other / total) * 100);
            }
        }
        return pct;
    }
} 