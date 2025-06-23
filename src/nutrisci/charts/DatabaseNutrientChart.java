package nutrisci.charts;

import nutrisci.db.NutritionDataDAO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class DatabaseNutrientChart extends JFrame {
    private NutritionDataDAO nutritionDAO;
    private ChartPanel chartPanel;
    private JPanel mainPanel;
    private JComboBox<FoodItem> foodSelector;
    
    private static class FoodItem {
        private final int foodId;
        private final String foodName;
        private final String foodGroup;
        
        public FoodItem(int foodId, String foodName, String foodGroup) {
            this.foodId = foodId;
            this.foodName = foodName;
            this.foodGroup = foodGroup;
        }
        
        public int getFoodId() { return foodId; }
        public String getFoodName() { return foodName; }
        public String getFoodGroup() { return foodGroup; }
        
        @Override
        public String toString() {
            return foodName + " (" + foodGroup + ")";
        }
    }

    public DatabaseNutrientChart() {
        super("NutriSci - Real Database Analysis");
        this.nutritionDAO = NutritionDataDAO.getInstance();
        initializeUI();
        loadInitialData();
    }
    
    private void initializeUI() {
        mainPanel = new JPanel(new BorderLayout()) {
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
        
        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel titleLabel = new JLabel("NutriSci Database Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(41, 128, 185));

        JLabel subtitleLabel = new JLabel("Real-time Nutritional Analysis from Database", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(127, 140, 141));

        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

        return titlePanel;
    }
    
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setOpaque(false);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        JLabel selectorLabel = new JLabel("Select Food Item: ");
        selectorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        selectorLabel.setForeground(new Color(52, 73, 94));
        
        foodSelector = new JComboBox<FoodItem>();
        foodSelector.setPreferredSize(new Dimension(300, 30));
        foodSelector.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBorderPainted(false);
        refreshButton.setFocusPainted(false);
        refreshButton.setPreferredSize(new Dimension(130, 30));
        
        JButton sampleButton = new JButton("Load Sample");
        sampleButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sampleButton.setBackground(new Color(46, 204, 113));
        sampleButton.setForeground(Color.WHITE);
        sampleButton.setBorderPainted(false);
        sampleButton.setFocusPainted(false);
        sampleButton.setPreferredSize(new Dimension(130, 30));
        
        foodSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateChart();
            }
        });
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAvailableFoods();
            }
        });
        sampleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadSampleData();
            }
        });
        
        controlPanel.add(selectorLabel);
        controlPanel.add(foodSelector);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(refreshButton);
        controlPanel.add(sampleButton);
        
        return controlPanel;
    }
    
    private void loadInitialData() {
        loadSampleData();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                loadAvailableFoods();
            }
        });
    }
    
    private void loadSampleData() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, Object> sampleFood = nutritionDAO.getSampleFoodWithNutrients();
                    
                    if (!sampleFood.isEmpty()) {
                        String foodName = (String) sampleFood.get("foodName");
                        @SuppressWarnings("unchecked")
                        Map<String, Double> nutrients = (Map<String, Double>) sampleFood.get("nutrients");
                        
                        if (!nutrients.isEmpty()) {
                            updateChartWithData(nutrients, foodName);
                            showStatusMessage("Loaded sample data: " + foodName, false);
                        } else {
                            showStatusMessage("No nutritional data found for sample food", true);
                        }
                    } else {
                        showStatusMessage("No sample data available", true);
                    }
                } catch (Exception e) {
                    showStatusMessage("Error loading sample data: " + e.getMessage(), true);
                }
            }
        });
    }
    
    private void loadAvailableFoods() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Map<String, Object>> foods = nutritionDAO.getAvailableFoods(50);
                    
                    foodSelector.removeAllItems();
                    foodSelector.addItem(new FoodItem(0, "Select a food item", ""));
                    
                    for (Map<String, Object> food : foods) {
                        int foodId = (Integer) food.get("foodId");
                        String foodName = (String) food.get("foodName");
                        String foodGroup = (String) food.get("foodGroup");
                        
                        foodSelector.addItem(new FoodItem(foodId, foodName, foodGroup));
                    }
                    
                    showStatusMessage("Loaded " + foods.size() + " food items", false);
                } catch (Exception e) {
                    showStatusMessage("Error loading food list: " + e.getMessage(), true);
                }
            }
        });
    }
    
    private void updateChart() {
        FoodItem selectedFood = (FoodItem) foodSelector.getSelectedItem();
        
        if (selectedFood != null && selectedFood.getFoodId() > 0) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        Map<String, Double> nutrients = nutritionDAO.getFoodNutrients(selectedFood.getFoodId());
                        
                        if (!nutrients.isEmpty()) {
                            updateChartWithData(nutrients, selectedFood.getFoodName());
                            showStatusMessage("Updated chart for: " + selectedFood.getFoodName(), false);
                        } else {
                            showStatusMessage("No nutritional data found for: " + selectedFood.getFoodName(), true);
                        }
                    } catch (Exception e) {
                        showStatusMessage("Error loading data for: " + selectedFood.getFoodName(), true);
                    }
                }
            });
        }
    }
    
    private void updateChartWithData(Map<String, Double> nutrientData, String foodName) {
        Map<String, Double> filteredData = new java.util.HashMap<>();
        for (Map.Entry<String, Double> entry : nutrientData.entrySet()) {
            if (entry.getValue() > 0.1) {
                filteredData.put(entry.getKey(), entry.getValue());
            }
        }
        
        if (filteredData.isEmpty()) {
            showStatusMessage("No significant nutritional values to display", true);
            return;
        }
        
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Double> entry : filteredData.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart chart = ChartFactory.createPieChart(null, dataset, true, true, false);
        customizeChart(chart, foodName);

        if (chartPanel != null) {
            mainPanel.remove(chartPanel);
        }
        
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 500));
        chartPanel.setBackground(new Color(250, 250, 250));
        chartPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        mainPanel.add(chartPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    
    private void customizeChart(JFreeChart chart, String foodName) {
        chart.setBackgroundPaint(new Color(250, 250, 250));
        chart.setBorderVisible(false);

        TextTitle title = new TextTitle("Nutritional Breakdown: " + foodName, 
                new Font("Segoe UI", Font.BOLD, 18));
        title.setPaint(new Color(52, 73, 94));
        chart.setTitle(title);

        PiePlot plot = (PiePlot) chart.getPlot();

        Color[] colors = {
            new Color(46, 204, 113),  
            new Color(241, 196, 15),  
            new Color(231, 76, 60),   
            new Color(52, 152, 219),  
            new Color(155, 89, 182),  
            new Color(26, 188, 156),  
            new Color(230, 126, 34),  
            new Color(149, 165, 166)  
        };

        int colorIndex = 0;
        @SuppressWarnings("unchecked")
        java.util.List<Comparable> keys = plot.getDataset().getKeys();
        for (Comparable key : keys) {
            plot.setSectionPaint(key, colors[colorIndex % colors.length]);
            colorIndex++;
        }

        plot.setBackgroundPaint(new Color(250, 250, 250));
        plot.setOutlineVisible(false);
        plot.setShadowPaint(new Color(0, 0, 0, 50));
        plot.setLabelFont(new Font("Segoe UI", Font.BOLD, 11));
        plot.setLabelPaint(Color.WHITE);
        plot.setLabelBackgroundPaint(new Color(52, 73, 94, 180));
        plot.setLabelOutlinePaint(Color.WHITE);
        plot.setLabelShadowPaint(null);

        if (chart.getLegend() != null) {
            chart.getLegend().setItemFont(new Font("Segoe UI", Font.PLAIN, 11));
            chart.getLegend().setBackgroundPaint(new Color(250, 250, 250));
        }

        plot.setStartAngle(45);
        plot.setDirection(org.jfree.util.Rotation.CLOCKWISE);
    }
    
    private void showStatusMessage(String message, boolean isError) {
        System.out.println((isError ? "ERROR: " : "INFO: ") + message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DatabaseNutrientChart();
            }
        });
    }
} 