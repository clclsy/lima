package nutrisci.view;

import java.awt.*;
import java.time.LocalDate;
import javax.swing.*;
import nutrisci.MainApp;
import nutrisci.logic.MealLogger;
import nutrisci.model.*;

public class LogMealPanel extends JPanel {
    public LogMealPanel(JFrame frame) {
        setLayout(new GridLayout(9, 2));

        JTextField nameField = new JTextField(); // user name
        JTextField dateField = new JTextField(LocalDate.now().toString());
        JComboBox<MealType> mealTypeBox = new JComboBox<>(MealType.values());
        JTextField foodNameField = new JTextField();
        JTextField quantityField = new JTextField();

        JButton addEntryBtn = new JButton("Add Entry");
        JButton logMealBtn = new JButton("Log Meal");
        JTextArea entriesArea = new JTextArea();
        JLabel statusLabel = new JLabel();

        Meal[] currentMeal = new Meal[1]; // to accumulate entries

        add(new JLabel("User Name:"));
        add(nameField);
        add(new JLabel("Date (yyyy-mm-dd):"));
        add(dateField);
        add(new JLabel("Meal Type:"));
        add(mealTypeBox);
        add(new JLabel("Food Item:"));
        add(foodNameField);
        add(new JLabel("Quantity (grams):"));
        add(quantityField);
        add(addEntryBtn);
        add(logMealBtn);
        add(new JLabel("Entries so far:"));
        add(new JScrollPane(entriesArea));
        add(statusLabel);
        add(new JLabel(""));

        addEntryBtn.addActionListener(e -> {
            try {
                if (currentMeal[0] == null) {
                    currentMeal[0] = new Meal(LocalDate.parse(dateField.getText()),
                            (MealType) mealTypeBox.getSelectedItem());
                }
                String food = foodNameField.getText();
                double qty = Double.parseDouble(quantityField.getText());
                MealEntry entry = new MealEntry(food, qty);
                currentMeal[0].addMealEntry(entry);
                entriesArea.append(food + " (" + qty + "g)\n");
                foodNameField.setText("");
                quantityField.setText("");
            } catch (Exception ex) {
                statusLabel.setText("Invalid input: " + ex.getMessage());
            }
        });

        logMealBtn.addActionListener(e -> {
            if (currentMeal[0] != null && nameField.getText().length() > 0) {
                MealLogger.logMeal(nameField.getText(), currentMeal[0]);
                statusLabel.setText("Meal logged for " + nameField.getText());
                currentMeal[0] = null;
                entriesArea.setText("");
            } else {
                statusLabel.setText("Please complete meal entry and user name.");
            }
        });

        JButton backBtn = new JButton("Back to Menu");
        backBtn.addActionListener(e -> MainApp.showMainMenu(frame));
        add(backBtn);

    }
}
