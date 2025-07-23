
package nutrisci.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import nutrisci.model.UserProfile;
import nutrisci.template.DatePicker;
import nutrisci.template.Styles;
import nutrisci.controller.VisualizationController;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import nutrisci.db.NutritionDataDAO;

public class CumulativeEffectPanel extends JPanel {

    private JFrame frame;
    private UserProfile profile;
    private JComboBox<String> foodToReplaceDropdown;
    private JComboBox<String> replacementFoodDropdown;
    private DatePicker datePicker;
    private JButton previewButton;
    private JButton applySwapButton;
    private JRadioButton dateRangeScope;
    private JRadioButton allMealsScope;
    private JComboBox<String> viewModeCombo;
    private JPanel chartPanel;
    private VisualizationController controller;

    public CumulativeEffectPanel(JFrame frame, UserProfile profile) {
        this.frame = frame;
        this.profile = profile;
        this.controller = new VisualizationController();
        setLayout(new BorderLayout());
        setBackground(Styles.background);

        initializeComponents();
        layoutComponents();
        populateFoodDropdowns();
        setupEventListeners();
    }

    private void initializeComponents() {
        foodToReplaceDropdown = new JComboBox<>();
        replacementFoodDropdown = new JComboBox<>();
        datePicker = new DatePicker();
        previewButton = new JButton("Preview Effect");
        applySwapButton = new JButton("Apply Swap");
        
        dateRangeScope = new JRadioButton("Apply to selected date range", true);
        allMealsScope = new JRadioButton("Apply to all meals");
        ButtonGroup scopeGroup = new ButtonGroup();
        scopeGroup.add(dateRangeScope);
        scopeGroup.add(allMealsScope);
        
        viewModeCombo = new JComboBox<>(new String[]{"Before vs After", "Per Meal Trends", "Average Nutrients"});
        chartPanel = new JPanel();
        
        previewButton.setBackground(Styles.lightorange);
        previewButton.setFont(Styles.default_font);
        previewButton.setFocusPainted(false);
        
        applySwapButton.setBackground(new java.awt.Color(168, 209, 123));
        applySwapButton.setFont(Styles.default_font);
        applySwapButton.setFocusPainted(false);
    }

    private void layoutComponents() {
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(Styles.background);
        topPanel.setBorder(BorderFactory.createTitledBorder("Food Substitution Settings"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Food to Replace:"), gbc);
        gbc.gridx = 1;
        topPanel.add(foodToReplaceDropdown, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        topPanel.add(new JLabel("Replacement Food:"), gbc);
        gbc.gridx = 3;
        topPanel.add(replacementFoodDropdown, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        topPanel.add(new JLabel("Date Range:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        topPanel.add(datePicker, gbc);
        
        gbc.gridx = 3; gbc.gridwidth = 1;
        topPanel.add(new JLabel("View Mode:"), gbc);
        gbc.gridx = 4;
        topPanel.add(viewModeCombo, gbc);
        
        JPanel scopePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        scopePanel.setBackground(Styles.background);
        scopePanel.setBorder(BorderFactory.createTitledBorder("Application Scope"));
        scopePanel.add(dateRangeScope);
        scopePanel.add(allMealsScope);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Styles.background);
        buttonPanel.add(previewButton);
        buttonPanel.add(applySwapButton);
        
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(Styles.background);
        controlPanel.add(topPanel, BorderLayout.NORTH);
        controlPanel.add(scopePanel, BorderLayout.CENTER);
        controlPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(controlPanel, BorderLayout.NORTH);
        add(new JScrollPane(chartPanel), BorderLayout.CENTER);
    }

    private void setupEventListeners() {
        previewButton.addActionListener(this::previewEffect);
        applySwapButton.addActionListener(this::applySwap);
        viewModeCombo.addActionListener(e -> {
            if (hasValidSelection()) {
                previewEffect(null);
            }
        });
    }

    private void populateFoodDropdowns() {
        foodToReplaceDropdown.addItem("Loading foods...");
        replacementFoodDropdown.addItem("Loading foods...");
    
        new Thread(() -> {
            NutritionDataDAO nutritionDataDAO = NutritionDataDAO.getInstance();
            List<String> foodNames = nutritionDataDAO.getAllFoodNames();
    
            javax.swing.SwingUtilities.invokeLater(() -> {
                foodToReplaceDropdown.removeAllItems();
                replacementFoodDropdown.removeAllItems();
                if (foodNames != null && !foodNames.isEmpty()) {
                    for (String foodName : foodNames) {
                        foodToReplaceDropdown.addItem(foodName);
                        replacementFoodDropdown.addItem(foodName);
                    }
                } else {
                    foodToReplaceDropdown.addItem("No foods found");
                    replacementFoodDropdown.addItem("No foods found");
                }
            });
        }).start();
    }

    private boolean hasValidSelection() {
        return foodToReplaceDropdown.getSelectedItem() != null &&
               replacementFoodDropdown.getSelectedItem() != null &&
               !foodToReplaceDropdown.getSelectedItem().equals("Loading foods...") &&
               !foodToReplaceDropdown.getSelectedItem().equals("No foods found");
    }

    private void previewEffect(ActionEvent e) {
        if (!hasValidSelection()) {
            JOptionPane.showMessageDialog(this, "Please wait for foods to load or check food selection.", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String foodToReplace = (String) foodToReplaceDropdown.getSelectedItem();
        String replacementFood = (String) replacementFoodDropdown.getSelectedItem();
        
        if (foodToReplace.equals(replacementFood)) {
            JOptionPane.showMessageDialog(this, "Please select different foods for replacement.", "Invalid Selection", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LocalDate startDate = LocalDate.parse(datePicker.getStartDate());
            LocalDate endDate = LocalDate.parse(datePicker.getEndDate());
            
            String viewMode = (String) viewModeCombo.getSelectedItem();
            
            switch (viewMode) {
                case "Before vs After":
                    showBeforeAfterChart(startDate, endDate, foodToReplace, replacementFood);
                    break;
                case "Per Meal Trends":
                    showPerMealTrends(startDate, endDate);
                    break;
                case "Average Nutrients":
                    showAverageNutrients(startDate, endDate);
                    break;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error parsing dates: " + ex.getMessage(), "Date Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showBeforeAfterChart(LocalDate startDate, LocalDate endDate, String foodToReplace, String replacementFood) {
        Map<String, Map<String, Double>> nutrientTotals = controller.getBeforeAfterTotals(profile.getId(), startDate, endDate, foodToReplace, replacementFood);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Double> beforeTotals = nutrientTotals.get("before");
        Map<String, Double> afterTotals = nutrientTotals.get("after");

        if (beforeTotals != null && afterTotals != null) {
            for (String nutrient : beforeTotals.keySet()) {
                dataset.addValue(beforeTotals.get(nutrient), "Before", nutrient);
                dataset.addValue(afterTotals.get(nutrient), "After", nutrient);
            }
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Nutrient Intake Before vs. After Substitution",
                "Nutrient",
                "Total Amount",
                dataset);

        updateChartPanel(new ChartPanel(barChart));
    }

    private void showPerMealTrends(LocalDate startDate, LocalDate endDate) {
        List<String> keyNutrients = Arrays.asList("Energy", "Protein", "Fat", "Carbohydrate", "Fibre");
        Map<String, List<Double>> trends = controller.getNutrientTrendsPerMeal(profile.getId(), startDate, endDate, keyNutrients);
        
        XYSeriesCollection dataset = new XYSeriesCollection();
        
        for (String nutrient : keyNutrients) {
            XYSeries series = new XYSeries(nutrient);
            List<Double> values = trends.get(nutrient);
            if (values != null) {
                for (int i = 0; i < values.size(); i++) {
                    series.add(i + 1, values.get(i));
                }
            }
            dataset.addSeries(series);
        }
        
        JFreeChart lineChart = ChartFactory.createXYLineChart(
                "Nutrient Trends Per Meal",
                "Meal Number",
                "Nutrient Amount",
                dataset);
        
        updateChartPanel(new ChartPanel(lineChart));
    }

    private void showAverageNutrients(LocalDate startDate, LocalDate endDate) {
        Map<String, Double> averages = controller.getAverageNutrients(profile.getId(), startDate, endDate);
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Double> entry : averages.entrySet()) {
            dataset.addValue(entry.getValue(), "Average", entry.getKey());
        }
        
        JFreeChart barChart = ChartFactory.createBarChart(
                "Average Nutrient Intake",
                "Nutrient",
                "Average Amount",
                dataset);
        
        updateChartPanel(new ChartPanel(barChart));
    }

    private void updateChartPanel(ChartPanel newChart) {
        chartPanel.removeAll();
        chartPanel.setLayout(new BorderLayout());
        chartPanel.add(newChart, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    private void applySwap(ActionEvent e) {
        if (!hasValidSelection()) {
            JOptionPane.showMessageDialog(this, "Please wait for foods to load or check food selection.", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String foodToReplace = (String) foodToReplaceDropdown.getSelectedItem();
        String replacementFood = (String) replacementFoodDropdown.getSelectedItem();
        
        if (foodToReplace.equals(replacementFood)) {
            JOptionPane.showMessageDialog(this, "Please select different foods for replacement.", "Invalid Selection", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to apply this swap? This will permanently modify your meal records.\n" +
                "Food to replace: " + foodToReplace + "\n" +
                "Replacement: " + replacementFood + "\n" +
                "Scope: " + (dateRangeScope.isSelected() ? "Selected date range" : "All meals"),
                "Confirm Swap Application",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            int mealsAffected;
            
            if (dateRangeScope.isSelected()) {
                LocalDate startDate = LocalDate.parse(datePicker.getStartDate());
                LocalDate endDate = LocalDate.parse(datePicker.getEndDate());
                mealsAffected = controller.applySwapToDateRange(profile.getId(), startDate, endDate, foodToReplace, replacementFood);
            } else {
                mealsAffected = controller.applySwapToAllMeals(profile.getId(), foodToReplace, replacementFood);
            }

            if (mealsAffected > 0) {
                JOptionPane.showMessageDialog(this,
                        "Swap applied successfully!\n" +
                        "Meals affected: " + mealsAffected,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                
                previewEffect(null);
            } else {
                JOptionPane.showMessageDialog(this,
                        "No meals were affected. The food '" + foodToReplace + "' was not found in any meals.",
                        "No Changes Made",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error applying swap: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
} 