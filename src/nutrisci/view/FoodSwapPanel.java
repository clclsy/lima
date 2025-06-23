package nutrisci.view;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import nutrisci.logic.MealLogger;
import nutrisci.logic.SmartSwapEngine;
import nutrisci.model.*;

public class FoodSwapPanel extends JPanel {
    public FoodSwapPanel(JFrame frame) {
        setLayout(new GridLayout(8, 2));

        JTextField nameField = new JTextField();  // user
        JTextField dateField = new JTextField(LocalDate.now().toString());

        JComboBox<String> nutrientBox = new JComboBox<>(new String[]{"fiber"});
        JTextField targetField = new JTextField("5");
        JCheckBox increaseBox = new JCheckBox("Increase");

        JButton suggestBtn = new JButton("Suggest Swaps");
        JTextArea outputArea = new JTextArea();
        JButton backBtn = new JButton("Back");

        add(new JLabel("User Name:")); add(nameField);
        add(new JLabel("Date (yyyy-mm-dd):")); add(dateField);
        add(new JLabel("Nutrient:")); add(nutrientBox);
        add(new JLabel("Target Change:")); add(targetField);
        add(increaseBox); add(new JLabel());
        add(suggestBtn); add(backBtn);
        add(new JLabel("Suggestions:")); add(new JScrollPane(outputArea));

        suggestBtn.addActionListener(e -> {
            try {
                String user = nameField.getText();
                LocalDate date = LocalDate.parse(dateField.getText());

                List<Meal> meals = MealLogger.getMealsForDate(user, date);
                if (meals.isEmpty()) {
                    outputArea.setText("No meals found for that date.");
                    return;
                }

                NutritionalGoal goal = new NutritionalGoal(
                        (String) nutrientBox.getSelectedItem(),
                        Double.parseDouble(targetField.getText()),
                        increaseBox.isSelected()
                );

                List<String> suggestions = SmartSwapEngine.suggestSwaps(meals.get(0), List.of(goal));
                outputArea.setText(String.join("\n", suggestions));

            } catch (NumberFormatException ex) {
                outputArea.setText("Error: " + ex.getMessage());
            }
        });

        backBtn.addActionListener(e -> nutrisci.MainApp.showMainMenu(frame));
        
    }
}
