package nutrisci.view;

import nutrisci.model.Meal;
import nutrisci.model.NutritionalGoal;
import nutrisci.template.Base;
import nutrisci.template.DatePicker;
import nutrisci.template.Styles;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class FoodSwapPanel extends Base {
    public FoodSwapPanel(JFrame frame) {
        super(frame);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Styles.background);

        JTextField nameField = new JTextField(15);
        DatePicker datePicker = new DatePicker();
        JTextField nutrientField = new JTextField("fiber");
        JTextField changeField = new JTextField("5");
        JCheckBox increase = new JCheckBox("Increase this nutrient");

        JButton swapBtn = new JButton("Suggest Swaps");
        JTextArea outputArea = new JTextArea(6, 30);
        outputArea.setEditable(false);

        swapBtn.addActionListener(e -> {
            try {
                String nutrient = nutrientField.getText();
                double amount = Double.parseDouble(changeField.getText());
                boolean isInc = increase.isSelected();
                String result = "Mock suggestion for " + nutrient + " by " + amount + (isInc ? " ↑" : " ↓");
                outputArea.setText(result);
            } catch (Exception ex) {
                outputArea.setText("Error: " + ex.getMessage());
            }
        });

        form.add(new JLabel("Name:")); form.add(nameField);
        form.add(Box.createVerticalStrut(10));
        form.add(new JLabel("Meal Date:")); form.add(datePicker);
        form.add(Box.createVerticalStrut(10));
        form.add(new JLabel("Nutrient:")); form.add(nutrientField);
        form.add(new JLabel("Amount:")); form.add(changeField);
        form.add(increase);
        form.add(swapBtn);
        form.add(new JScrollPane(outputArea));

        add(createTopPanel(new MainMenu(frame)), BorderLayout.NORTH);
        add(createCenterPanel(form), BorderLayout.CENTER);
    }
}
