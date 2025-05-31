package nutrisci.gui;

import java.awt.*;
import javax.swing.*;

public class MealLogging extends JPanel {

    private JTextField dateField, ingredientsField;
    private JComboBox<String> mealTypeBox;

    public MealLogging() {
        setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Log a Meal"));

        // Create fields
        dateField = new JTextField();
        ingredientsField = new JTextField();
        mealTypeBox = new JComboBox<>(new String[]{"Breakfast", "Lunch", "Dinner", "Snack"});

        // Add components
        formPanel.add(new JLabel("Date (YYYY-MM-DD):")); formPanel.add(dateField);
        formPanel.add(new JLabel("Meal Type:")); formPanel.add(mealTypeBox);
        formPanel.add(new JLabel("Ingredients (comma-separated):")); formPanel.add(ingredientsField);

        add(formPanel, BorderLayout.CENTER);
        JButton logBtn = new JButton("Log Meal");
        add(logBtn, BorderLayout.SOUTH);
    }
}
