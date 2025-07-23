package nutrisci.view;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import nutrisci.controller.SwapController;
import nutrisci.db.MealDAO;
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

    public SwapSuggestionPanel(JFrame frame, UserProfile profile) {
        super(frame);
        this.controller = new SwapController();
        init(frame, profile);
    }

    private void init(JFrame frame, UserProfile profile) {
        setLayout(new BorderLayout());
        setBackground(Styles.background);

        // Top bar
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Styles.background);
        top.add(createTopPanel(new UserDashboardPanel(frame, profile)), BorderLayout.WEST);

        JLabel header = new JLabel("Food Swap Suggestions for " + profile.getName());
        header.setFont(Styles.dtitle_font);
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setBorder(BorderFactory.createEmptyBorder(60, 0, 20, 0));
        top.add(header, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        // Main content area (vertical stack)
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Styles.background);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 60, 60));

        String[] nutrients = { "Calories", "Fiber", "Protein", "Fat" };
        String[] units = { "g", "%" };
        String[] directions = { "Increase", "Decrease" };

        // Meal selector
        JPanel mealRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        mealRow.setBackground(Styles.background);
        mealRow.add(new JLabel("Select a Meal to Analyze:"));

        List<Meal> meals = MealDAO.getMealsByUserId(profile.getId());
        String[] mealOptions = meals.stream()
                .map(m -> m.getDate() + " - " + m.getType())
                .toArray(String[]::new);
        mealDropdown = new JComboBox<>(mealOptions);
        mealRow.add(mealDropdown);
        centerPanel.add(mealRow);

        // Goal 1 row
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        row1.setBackground(Styles.background);
        row1.add(new JLabel("Nutrient Goal 1:"));
        nutrient1Dropdown = new JComboBox<>(nutrients);
        direction1Dropdown = new JComboBox<>(directions);
        amount1Field = new JTextField(5);
        unit1Dropdown = new JComboBox<>(units);
        row1.add(nutrient1Dropdown);
        row1.add(direction1Dropdown);
        row1.add(amount1Field);
        row1.add(unit1Dropdown);
        centerPanel.add(row1);

        // Goal 2 row
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        row2.setBackground(Styles.background);
        row2.add(new JLabel("Nutrient Goal 2 (optional):"));
        nutrient2Dropdown = new JComboBox<>(nutrients);
        direction2Dropdown = new JComboBox<>(directions);
        amount2Field = new JTextField(5);
        unit2Dropdown = new JComboBox<>(units);
        row2.add(nutrient2Dropdown);
        row2.add(direction2Dropdown);
        row2.add(amount2Field);
        row2.add(unit2Dropdown);
        centerPanel.add(row2);

        // Submit Button
        JButton submitBtn = new JButton("Generate Suggestions");
        submitBtn.setFont(Styles.default_font);
        submitBtn.setBackground(Styles.lightorange);
        submitBtn.setFocusPainted(false);
        submitBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        submitBtn.setPreferredSize(new Dimension(240, 35));
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Styles.background);
        btnPanel.add(submitBtn);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(btnPanel);

        // Suggestions label
        JLabel resultsLabel = new JLabel("Food Suggestions");
        resultsLabel.setFont(Styles.dtitle_font);
        resultsLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        resultsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(resultsLabel);

        // Output text area
        outputArea = new JTextArea(10, 60);
        outputArea.setFont(Styles.default_font);
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        scrollPane.setMaximumSize(new Dimension(600, 200));
        centerPanel.add(scrollPane);

        add(centerPanel, BorderLayout.CENTER);

        // Action logic
        submitBtn.addActionListener(e -> {
            try {
                List<NutritionalGoal> goals = new ArrayList<>();

                String a1 = amount1Field.getText();
                if (!a1.isBlank()) {
                    goals.add(new NutritionalGoal(
                            (String) nutrient1Dropdown.getSelectedItem(),
                            parseAmount(a1),
                            direction1Dropdown.getSelectedItem().equals("Increase"),
                            (String) unit1Dropdown.getSelectedItem()));
                }

                String a2 = amount2Field.getText();
                if (!a2.isBlank()) {
                    goals.add(new NutritionalGoal(
                            (String) nutrient2Dropdown.getSelectedItem(),
                            parseAmount(a2),
                            direction2Dropdown.getSelectedItem().equals("Increase"),
                            (String) unit2Dropdown.getSelectedItem()));
                }

                if (goals.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter at least one nutritional goal.",
                            "Missing Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int selectedIndex = mealDropdown.getSelectedIndex();
                if (selectedIndex < 0 || selectedIndex >= meals.size()) {
                    JOptionPane.showMessageDialog(this, "Please select a meal to analyze.",
                            "Missing Meal", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Meal selectedMeal = meals.get(selectedIndex);
                List<FoodSwap> suggestions = controller.getSwapSuggestionsForMeal(selectedMeal, goals);

                StringBuilder summary = new StringBuilder("Your selected goals:\n");
                for (NutritionalGoal g : goals) {
                    summary.append("- ").append(g.getDescription()).append("\n");
                }

                summary.append("\nSwap Results:\n\n");
                if (suggestions.isEmpty()) {
                    summary.append("No suitable swaps found.");
                } else {
                    for (FoodSwap swap : suggestions) {
                        summary.append("~ Replace ").append(swap.getOriginal())
                                .append(" → ").append(swap.getReplacement()).append("\n");
                        summary.append("  ↳ ").append(swap.getRationale()).append("\n\n");
                    }
                }

                outputArea.setText(summary.toString());

            } catch (HeadlessException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(),
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

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
