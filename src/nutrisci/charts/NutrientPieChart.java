package nutrisci.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class NutrientPieChart extends JFrame {

    public NutrientPieChart(String title, Map<String, Double> nutrientData) {
        super(title);
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }

        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Double> entry : nutrientData.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart chart = ChartFactory.createPieChart(
                null,
                dataset,
                true,
                true,
                false
        );

        customizeChart(chart);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        chartPanel.setBackground(new Color(250, 250, 250));
        chartPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(240, 248, 255),
                    0, getHeight(), new Color(230, 240, 250)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        JPanel titlePanel = createTitlePanel();
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(chartPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel titleLabel = new JLabel("NutriSci Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(41, 128, 185));

        JLabel subtitleLabel = new JLabel("Nutritional Breakdown Analysis", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(127, 140, 141));

        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

        return titlePanel;
    }

    private void customizeChart(JFreeChart chart) {
        chart.setBackgroundPaint(new Color(250, 250, 250));
        chart.setBorderVisible(false);

        TextTitle title = new TextTitle("Meal Nutrient Breakdown", 
                new Font("Segoe UI", Font.BOLD, 20));
        title.setPaint(new Color(52, 73, 94));
        chart.setTitle(title);

        PiePlot plot = (PiePlot) chart.getPlot();

        Color[] colors = {
            new Color(46, 204, 113),
            new Color(241, 196, 15),
            new Color(231, 76, 60),
            new Color(52, 152, 219),
            new Color(155, 89, 182),
            new Color(26, 188, 156)
        };

        int colorIndex = 0;
        for (Object key : plot.getDataset().getKeys()) {
            plot.setSectionPaint((Comparable) key, colors[colorIndex % colors.length]);
            colorIndex++;
        }

        plot.setBackgroundPaint(new Color(250, 250, 250));
        plot.setOutlineVisible(false);
        plot.setShadowPaint(new Color(0, 0, 0, 50));
        plot.setExplodePercent("Protein", 0.1);

        plot.setLabelFont(new Font("Segoe UI", Font.BOLD, 12));
        plot.setLabelPaint(Color.WHITE);
        plot.setLabelBackgroundPaint(new Color(52, 73, 94, 180));
        plot.setLabelOutlinePaint(Color.WHITE);
        plot.setLabelShadowPaint(null);
        plot.setLabelPadding(new org.jfree.ui.RectangleInsets(5, 5, 5, 5));

        if (chart.getLegend() != null) {
            chart.getLegend().setItemFont(new Font("Segoe UI", Font.PLAIN, 12));
            chart.getLegend().setBackgroundPaint(new Color(250, 250, 250));
            chart.getLegend().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        }
        plot.setSectionDepth(0.35);
        plot.setStartAngle(45);
        plot.setDirection(org.jfree.util.Rotation.CLOCKWISE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Map<String, Double> nutrients = Map.of(
                    "Protein", 25.0,
                    "Carbohydrates", 45.0,
                    "Fats", 20.0,
                    "Vitamins", 6.0,
                    "Minerals", 4.0
            );

            new NutrientPieChart("NutriSci - Nutrition Analysis", nutrients);
        });
    }
}
