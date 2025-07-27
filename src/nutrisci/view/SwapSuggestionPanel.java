package nutrisci.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;
import javax.swing.*;
import nutrisci.controller.SwapController;
import nutrisci.db.*;
import nutrisci.model.*;
import nutrisci.template.*;

public class SwapSuggestionPanel extends Base {
    private JComboBox<String> nutrient1Dropdown, unit1Dropdown, direction1Dropdown;
    private JTextField amount1Field;
    private JComboBox<String> nutrient2Dropdown, unit2Dropdown, direction2Dropdown;
    private JTextField amount2Field;
    private JComboBox<String> mealDropdown;
    private JTextArea outputArea;
    private SwapController controller;
    private UserProfile profile;

    public SwapSuggestionPanel(JFrame frame, UserProfile profile) {
        super(frame);
        this.profile = profile;
        this.controller = new SwapController();
        init(frame, profile);
    }

    private void init(JFrame frame, UserProfile profile) {
        setLayout(new BorderLayout());
        setBackground(Styles.background);

        // Top bar
        JPanel top = createTopPanel(frame, profile);
        add(top, BorderLayout.NORTH);

        // Main content area
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createTopPanel(JFrame frame, UserProfile profile) {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Styles.background);
        top.add(createTopPanel(new UserDashboardPanel(frame, profile)), BorderLayout.WEST);

        JLabel header = new JLabel("Food Swap Suggestions for " + profile.getName());
        header.setFont(Styles.dtitle_font);
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setBorder(BorderFactory.createEmptyBorder(60, 0, 20, 0));
        top.add(header, BorderLayout.CENTER);
        return top;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Styles.background);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 60, 60));

        // Meal selector
        centerPanel.add(createMealSelectorPanel());

        // Nutrient goals
        centerPanel.add(createGoalPanel(1, "Nutrient Goal 1:"));
        centerPanel.add(createGoalPanel(2, "Nutrient Goal 2 (optional):"));

        // Submit button
        centerPanel.add(createSubmitButtonPanel());

        // Results area
        centerPanel.add(createResultsLabel());
        centerPanel.add(createOutputArea());

        return centerPanel;
    }

    private JPanel createMealSelectorPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(Styles.background);
        panel.add(new JLabel("Select a Meal to Analyze:"));

        List<Meal> meals = MealDAO.getMealsByUserId(profile.getId());
        String[] mealOptions = meals.stream()
                .map(m -> m.getDate() + " - " + m.getType())
                .toArray(String[]::new);
        mealDropdown = new JComboBox<>(mealOptions);
        panel.add(mealDropdown);
        return panel;
    }

    private JPanel createGoalPanel(int goalNumber, String label) {
        String[] nutrients = { "Calories", "Fiber", "Protein", "Fat", "Carbohydrates" };
        String[] units = { "g", "%" };
        String[] directions = { "Increase", "Decrease" };

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(Styles.background);
        panel.add(new JLabel(label));

        JComboBox<String> nutrientDropdown = new JComboBox<>(nutrients);
        JComboBox<String> directionDropdown = new JComboBox<>(directions);
        JTextField amountField = new JTextField(5);
        JComboBox<String> unitDropdown = new JComboBox<>(units);

        if (goalNumber == 1) {
            this.nutrient1Dropdown = nutrientDropdown;
            this.direction1Dropdown = directionDropdown;
            this.amount1Field = amountField;
            this.unit1Dropdown = unitDropdown;
        } else {
            this.nutrient2Dropdown = nutrientDropdown;
            this.direction2Dropdown = directionDropdown;
            this.amount2Field = amountField;
            this.unit2Dropdown = unitDropdown;
        }

        panel.add(nutrientDropdown);
        panel.add(directionDropdown);
        panel.add(amountField);
        panel.add(unitDropdown);
        return panel;
    }

    private JPanel createSubmitButtonPanel() {
        JButton submitBtn = new JButton("Generate Suggestions");
        submitBtn.setFont(Styles.default_font);
        submitBtn.setBackground(Styles.lightorange);
        submitBtn.setFocusPainted(false);
        submitBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        submitBtn.setPreferredSize(new Dimension(240, 35));
        submitBtn.addActionListener(this::handleSubmit);

        JPanel panel = new JPanel();
        panel.setBackground(Styles.background);
        panel.add(submitBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        return panel;
    }

    private Component createResultsLabel() {
        JLabel label = new JLabel("Food Suggestions");
        label.setFont(Styles.dtitle_font);
        label.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private Component createOutputArea() {
        outputArea = new JTextArea(10, 60);
        outputArea.setFont(Styles.default_font);
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        scrollPane.setMaximumSize(new Dimension(600, 200));
        return scrollPane;
    }

    private void handleSubmit(ActionEvent e) {
        try {
            List<NutritionalGoal> goals = createNutritionalGoals();
            if (goals.isEmpty()) {
                showError("Please enter at least one nutritional goal.", "Missing Input");
                return;
            }

            Meal selectedMeal = getSelectedMeal();
            if (selectedMeal == null) {
                showError("Please select a meal to analyze.", "Missing Meal");
                return;
            }

            List<FoodSwap> suggestions = controller.getSwapSuggestionsForMeal(selectedMeal, goals);
            displayResults(goals, suggestions, selectedMeal);

        } catch (NumberFormatException ex) {
            showError("Invalid number format: " + ex.getMessage(), "Input Error");
        }
    }

    private List<NutritionalGoal> createNutritionalGoals() {
        List<NutritionalGoal> goals = new ArrayList<>();
        addGoalIfValid(goals, nutrient1Dropdown, direction1Dropdown, amount1Field, unit1Dropdown);
        addGoalIfValid(goals, nutrient2Dropdown, direction2Dropdown, amount2Field, unit2Dropdown);
        return goals;
    }

    private void addGoalIfValid(List<NutritionalGoal> goals,
            JComboBox<String> nutrientDropdown,
            JComboBox<String> directionDropdown,
            JTextField amountField,
            JComboBox<String> unitDropdown) {

        String amountText = amountField.getText().trim();
        if (!amountText.isEmpty()) {
            goals.add(new NutritionalGoal(
                    (String) nutrientDropdown.getSelectedItem(),
                    parseAmount(amountText),
                    directionDropdown.getSelectedItem().equals("Increase"),
                    (String) unitDropdown.getSelectedItem()));
        }
    }

    private Meal getSelectedMeal() {
        List<Meal> meals = MealDAO.getMealsByUserId(profile.getId());
        int selectedIndex = mealDropdown.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < meals.size()) {
            return meals.get(selectedIndex);
        }
        return null;
    }

    private void displayResults(List<NutritionalGoal> goals, List<FoodSwap> suggestions, Meal selectedMeal) {
        StringBuilder summary = new StringBuilder();
        appendGoals(summary, goals);
        appendSwapResults(summary, suggestions, selectedMeal);
        outputArea.setText(summary.toString());
    }

    private void appendGoals(StringBuilder summary, List<NutritionalGoal> goals) {
        summary.append("Your selected goals:\n");
        for (NutritionalGoal g : goals) {
            summary.append("- ").append(g.getDescription()).append("\n");
        }
        summary.append("\nSwap Results:\n\n");
    }

    private void appendSwapResults(StringBuilder summary, List<FoodSwap> suggestions, Meal selectedMeal) {
        if (suggestions.isEmpty()) {
            summary.append("No suitable swaps found. Debug information:\n\n");

            // Show why swaps weren't found for each item
            for (MealItem item : selectedMeal.getItems()) {
                summary.append("Item: ").append(item.getIngredient()).append("\n");
                FoodDAO foodDao = new FoodDAO();
                Integer foodId = foodDao.getFoodIdByName(item.getIngredient());
                if (foodId == null) {
                    summary.append("  - Could not find in database\n");
                    continue;
                }

                Map<String, Double> nutrients = NutritionDataDAO.getInstance().getFoodNutrients(foodId);
                summary.append("  - Nutrients: ").append(nutrients).append("\n");

                // Add more debug info as needed
            }

            summary.append("\nTry these tips:\n");
            summary.append("- Use more specific food names (e.g., 'skinless chicken breast')\n");
            summary.append("- Increase the allowed calorie variance in settings\n");
            summary.append("- Try with just one nutrient goal at a time\n");
        } else {
            suggestions.forEach(swap -> appendSwapDetails(summary, swap));
            appendNutritionalComparison(summary, selectedMeal, suggestions);
        }
    }

    private void appendSwapDetails(StringBuilder summary, FoodSwap swap) {
        summary.append(String.format(
                "Replace %-20s → %-20s\n  ↳ %s\n\n",
                swap.getOriginal(),
                swap.getReplacement(),
                swap.getRationale()));
    }

    private void appendNutritionalComparison(StringBuilder summary, Meal meal, List<FoodSwap> swaps) {
        summary.append("\nNutritional Comparison:\n");
        Map<String, Double> original = calculateMealNutrients(meal);
        Map<String, Double> swapped = calculateSwappedMealNutrients(meal, swaps);

        summary.append(String.format("%-15s %-10s %-10s %-10s\n",
                "Nutrient", "Original", "Swapped", "Change"));

        original.forEach((k, origVal) -> {
            Double swappedVal = swapped.get(k);
            if (swappedVal != null) {
                double change = (swappedVal - origVal) / origVal * 100;
                String changeStr = String.format("%+.1f%%", change);
                summary.append(String.format("%-15s %-10.1f %-10.1f %-10s\n",
                        k, origVal, swappedVal, changeStr));
            }
        });
    }

    private Map<String, Double> calculateMealNutrients(Meal meal) {
        Map<String, Double> totals = new HashMap<>();
        NutritionDataDAO nutritionDAO = NutritionDataDAO.getInstance();

        for (MealItem item : meal.getItems()) {
            FoodDAO foodDao = new FoodDAO();
            Integer foodId = foodDao.getFoodIdByName(item.getIngredient());
            if (foodId != null) {
                Map<String, Double> nutrients = nutritionDAO.getFoodNutrients(foodId);
                nutrients.forEach((k, v) -> totals.merge(k, v * item.getQuantity(), Double::sum));
            }
        }
        return totals;
    }

    private Map<String, Double> calculateSwappedMealNutrients(Meal meal, List<FoodSwap> swaps) {
        Map<String, String> replacements = new HashMap<>();
        swaps.forEach(swap -> replacements.put(swap.getOriginal(), swap.getReplacement()));

        Map<String, Double> totals = new HashMap<>();
        NutritionDataDAO nutritionDAO = NutritionDataDAO.getInstance();

        for (MealItem item : meal.getItems()) {
            String foodName = replacements.getOrDefault(item.getIngredient(), item.getIngredient());
            FoodDAO foodDao = new FoodDAO();
            Integer foodId = foodDao.getFoodIdByName(item.getIngredient());

            if (foodId != null) {
                Map<String, Double> nutrients = nutritionDAO.getFoodNutrients(foodId);
                nutrients.forEach((k, v) -> totals.merge(k, v * item.getQuantity(), Double::sum));
            }
        }
        return totals;
    }

    private void showError(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private double parseAmount(String value) throws NumberFormatException {
        return Double.parseDouble(value.trim());
    }

    public SwapController getController() {
        return controller;
    }

    public void setController(SwapController controller) {
        this.controller = controller;
    }
}