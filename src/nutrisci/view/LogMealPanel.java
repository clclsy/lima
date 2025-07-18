package nutrisci.view;

import nutrisci.model.Meal;
import nutrisci.model.MealEntry;
import nutrisci.model.MealType;
import nutrisci.template.Base;
import nutrisci.template.Styles;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class LogMealPanel extends Base {
    public LogMealPanel(JFrame frame) {
        super(frame);

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));
        form.setBackground(Styles.background);
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField userField = new JTextField();
        JTextField dateField = new JTextField(LocalDate.now().toString());
        JComboBox<MealType> mealTypeBox = new JComboBox<>(MealType.values());
        JTextField foodField = new JTextField();
        JTextField quantityField = new JTextField();
        JLabel status = new JLabel("");

        form.add(new JLabel("User Name:")); form.add(userField);
        form.add(new JLabel("Date:")); form.add(dateField);
        form.add(new JLabel("Meal Type:")); form.add(mealTypeBox);
        form.add(new JLabel("Food:")); form.add(foodField);
        form.add(new JLabel("Quantity (g):")); form.add(quantityField);

        JButton submitBtn = new JButton("Log Meal");
        submitBtn.addActionListener(e -> {
            try {
                String food = foodField.getText();
                double qty = Double.parseDouble(quantityField.getText());
                Meal meal = new Meal(LocalDate.parse(dateField.getText()), (MealType) mealTypeBox.getSelectedItem());
                meal.addMealEntry(new MealEntry(food, qty));
                status.setText("Meal logged successfully!");
            } catch (Exception ex) {
                status.setText("Error: " + ex.getMessage());
            }
        });

        form.add(submitBtn); form.add(status);
        add(createTopPanel(new MainMenu(frame)), BorderLayout.NORTH);
        add(createCenterPanel(form), BorderLayout.CENTER);
    }
}
