package nutrisci.view;

import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import nutrisci.db.MealDAO;
import nutrisci.model.*;
import nutrisci.template.Base;
import nutrisci.template.Styles;

public class LogMealPanel extends Base {
    private final JComboBox<String> mealTypeDropdown;
    private final IngredientAdding ingredient_input;
    private final JTextField dateField;
    private final UserProfile profile;

    public LogMealPanel(JFrame frame, UserProfile profile) {
        super(frame);
        this.profile = profile;
        
        setLayout(new BorderLayout());
        add(createTopPanel(new DietDashboardPanel(frame, profile)), BorderLayout.NORTH);

        // Form
        JPanel formCard = createCardPanel();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel formTitle = new JLabel("Log a Meal");
        formTitle.setFont(Styles.dtitle_font);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formCard.add(formTitle, gbc);

        // Date
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        dateLabel.setFont(Styles.default_font);
        formCard.add(dateLabel, gbc);
        gbc.gridx = 1;
        dateField = new JTextField(12);
        dateField.setFont(Styles.default_font);
        formCard.add(dateField, gbc);

        // Meal Type
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel lbl = new JLabel("Meal Type:");
        lbl.setFont(Styles.default_font);
        formCard.add(lbl, gbc);

        gbc.gridx = 1;
        mealTypeDropdown = new JComboBox<>(new String[] { "Breakfast", "Lunch", "Dinner", "Snack" });
        mealTypeDropdown.setFont(Styles.default_font);
        formCard.add(mealTypeDropdown, gbc);

        // Ingredients label
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JLabel ingLabel = new JLabel("Ingredients (name, quantity):");
        ingLabel.setFont(Styles.default_font);
        formCard.add(ingLabel, gbc);

        // Ingredient input
        gbc.gridy++;
        ingredient_input = new IngredientAdding();
        JScrollPane ingScroll = new JScrollPane(ingredient_input);
        ingScroll.setPreferredSize(new Dimension(300, 120));
        formCard.add(ingScroll, gbc);

        // Add Ingredient Button
        gbc.gridy++;
        JButton addRowBtn = new JButton("+ Add Ingredient");
        addRowBtn.setBackground(Styles.lightorange);
        addRowBtn.setFont(Styles.default_font);
        addRowBtn.setFocusPainted(false);
        addRowBtn.addActionListener(e -> ingredient_input.addRow());
        formCard.add(addRowBtn, gbc);

        // Submit Button
        gbc.gridy++;
        JButton logButton = new JButton("✔ Log Meal");
        logButton.setFont(Styles.default_font);
        logButton.setBackground(new Color(168, 209, 123));
        logButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logButton.addActionListener(e -> {
            logMeal();
        });
        formCard.add(logButton, gbc);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(Styles.background);
        center.add(formCard);
        add(center, BorderLayout.CENTER);
    }

    private void logMeal() {
        try {
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            String type = mealTypeDropdown.getSelectedItem().toString();
            List<String[]> rows = ingredient_input.getInputRows();

            if (rows.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Add at least one ingredient.");
                return;
            }

            List<MealItem> items = new ArrayList<>();
            for (String[] row : rows) {
                String name = row[0];
                double quantity = Double.parseDouble(row[1]);
                items.add(new MealItem(name, quantity));
            }

            Meal meal = new Meal(profile.getId(), date, MealType.valueOf(type.toUpperCase()), items);

            // Save to DB
            MealDAO.insertMeal(profile.getId(),meal);

            JOptionPane.showMessageDialog(this, "Meal logged successfully.");

            // Switch to ViewMealsPanel
            ViewMealsPanel viewPanel = new ViewMealsPanel(frame, profile);
            frame.setContentPane(viewPanel);
            frame.revalidate();
            frame.repaint();
        } catch (HeadlessException | NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Invalid input: " + e.getMessage());
        }
    }
}
