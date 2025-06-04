package nutrisci.gui;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class LogMealPanel extends JPanel {
    private final JTextField dateField;
    private final JComboBox<String> mealTypeBox;
    private final JPanel ingredientPanel;
    private final ArrayList<JTextField> ingredientFields = new ArrayList<>();
    private final ArrayList<JTextField> quantityFields = new ArrayList<>();
    private final UserProfile profile;

    public LogMealPanel(JFrame frame, UserProfile profile) {
        this.profile = profile;
        setLayout(new BorderLayout());
        setBackground(new Color(255, 251, 245));

        // === Top Bar with Back Button ===
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 231, 216));
        JButton backBtn = new JButton("← Back");
        backBtn.setFont(new Font("Helvetica", Font.BOLD, 16));
        backBtn.setFocusPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            frame.setContentPane(new DietDashboardPanel(frame, profile));
            frame.revalidate();
        });
        topPanel.add(backBtn, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // === Form Card ===
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel formTitle = new JLabel("Log a New Meal");
        formTitle.setFont(new Font("Georgia", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formCard.add(formTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        formCard.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        dateField = new JTextField(12);
        formCard.add(dateField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formCard.add(new JLabel("Meal Type:"), gbc);
        gbc.gridx = 1;
        mealTypeBox = new JComboBox<>(new String[] {"Breakfast", "Lunch", "Dinner", "Snack"});
        formCard.add(mealTypeBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JLabel ingLabel = new JLabel("Ingredients (name + quantity):");
        formCard.add(ingLabel, gbc);

        gbc.gridy++;
        ingredientPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JScrollPane ingScroll = new JScrollPane(ingredientPanel);
        ingScroll.setPreferredSize(new Dimension(300, 120));
        formCard.add(ingScroll, gbc);

        // Add default 3 rows
        for (int i = 0; i < 3; i++) addIngredientRow();

        gbc.gridy++;
        JButton addRowBtn = new JButton("+ Add Ingredient");
        addRowBtn.setBackground(new Color(200, 255, 200));
        addRowBtn.setFont(new Font("Helvetica", Font.PLAIN, 14));
        addRowBtn.setFocusPainted(false);
        addRowBtn.addActionListener(e -> addIngredientRow());
        formCard.add(addRowBtn, gbc);

        gbc.gridy++;
        JButton logBtn = new JButton("✔ Log Meal");
        logBtn.setFont(new Font("Helvetica", Font.PLAIN, 14));
        logBtn.setBackground(new Color(168, 209, 123));
        logBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logBtn.addActionListener(e -> logMeal());
        formCard.add(logBtn, gbc);

        // Center Wrapper
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(new Color(255, 251, 245));
        center.add(formCard);
        add(center, BorderLayout.CENTER);
    }

    private void addIngredientRow() {
        JTextField nameField = new JTextField("e.g. Chicken");
        JTextField qtyField = new JTextField("e.g. 100g");
        ingredientFields.add(nameField);
        quantityFields.add(qtyField);
        ingredientPanel.add(nameField);
        ingredientPanel.add(qtyField);
        ingredientPanel.revalidate();
        ingredientPanel.repaint();
    }

    private void logMeal() {
        String date = dateField.getText().trim();
        String mealType = mealTypeBox.getSelectedItem().toString();
        ArrayList<String> ingredients = new ArrayList<>();

        for (int i = 0; i < ingredientFields.size(); i++) {
            String ing = ingredientFields.get(i).getText().trim();
            String qty = quantityFields.get(i).getText().trim();
            if (!ing.isEmpty() && !qty.isEmpty()) {
                ingredients.add(ing + " (" + qty + ")");
            }
        }

        if (date.isEmpty() || ingredients.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a date and at least one valid ingredient.",
                    "Missing Data", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UserProfile.Meal meal = new UserProfile.Meal(date, mealType, ingredients);
        profile.addMeal(meal);

        JOptionPane.showMessageDialog(this, "Meal logged successfully!", "Meal Saved", JOptionPane.INFORMATION_MESSAGE);
    }
}
