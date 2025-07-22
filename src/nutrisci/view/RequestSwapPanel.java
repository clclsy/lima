package nutrisci.view;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import nutrisci.controller.SwapController;
import nutrisci.model.*;
import nutrisci.template.*;

public class RequestSwapPanel extends Base {
    private JComboBox<String> nutrientDropdown;
    private JTextField targetField;
    private JCheckBox increaseCheck;
    private JTextArea outputArea;
    private SwapController controller;

    public RequestSwapPanel(JFrame frame, UserProfile profile) {
        super(frame);
        this.controller = new SwapController();
        init(frame, profile);
    }

    private void init(JFrame frame, UserProfile profile) {
        setLayout(new BorderLayout());
        setBackground(Styles.background);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Styles.background);
        top.add(createTopPanel(new MealSelectionPanel(frame, profile)), BorderLayout.WEST);

        JLabel greeting = new JLabel("Request Food Swap for " + profile.getName());
        greeting.setFont(Styles.dtitle_font);
        greeting.setHorizontalAlignment(SwingConstants.CENTER);
        greeting.setBorder(BorderFactory.createEmptyBorder(60, 0, 20, 0));

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        inputPanel.setBackground(Styles.background);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        nutrientDropdown = new JComboBox<>(new String[]{"Calories", "Fiber", "Protein", "Fat"});
        targetField = new JTextField();
        increaseCheck = new JCheckBox("Increase (uncheck to decrease)");
        increaseCheck.setSelected(true);

        inputPanel.add(new JLabel("Select Nutrient Goal:"));
        inputPanel.add(nutrientDropdown);
        inputPanel.add(new JLabel("Amount to Change (e.g., 5.0):"));
        inputPanel.add(targetField);
        inputPanel.add(new JLabel("Direction:"));
        inputPanel.add(increaseCheck);

        JButton submitBtn = new JButton("Generate Suggestions");
        submitBtn.setFont(Styles.default_font);
        submitBtn.setBackground(Styles.lightorange);
        submitBtn.setFocusPainted(false);
        submitBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        submitBtn.addActionListener(e -> {
            try {
                String nutrient = (String) nutrientDropdown.getSelectedItem();
                double amount = Double.parseDouble(targetField.getText());
                boolean isIncrease = increaseCheck.isSelected();

                NutritionalGoal goal = new NutritionalGoal(nutrient, amount, isIncrease);
                List<FoodSwap> suggestions = controller.getSwapSuggestions(profile.getId(), goal);

                if (suggestions.isEmpty()) {
                    outputArea.setText("No suitable swaps found.");
                } else {
                    StringBuilder sb = new StringBuilder("Suggested Swaps:\n\n");
                    for (FoodSwap swap : suggestions) {
                        sb.append("Meal ID: ").append(swap.getMealId()).append("\n");
                        sb.append("- Replace ").append(swap.getOriginal()).append(" with ")
                          .append(swap.getReplacement()).append("\n");
                        sb.append("  Reason: ").append(swap.getRationale()).append("\n\n");
                    }
                    outputArea.setText(sb.toString());
                }
            } catch (NumberFormatException ex) {
                outputArea.setText("Please enter a valid number for the amount.");
            }
        });

        inputPanel.add(new JLabel());
        inputPanel.add(submitBtn);

        outputArea = new JTextArea(10, 40);
        outputArea.setEditable(false);
        outputArea.setFont(Styles.default_font);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Swap Suggestions"));

        add(top, BorderLayout.NORTH);
        add(greeting, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.SOUTH);
    }

    public SwapController getController() {
        return controller;
    }

    public void setController(SwapController controller) {
        this.controller = controller;
    }
}
