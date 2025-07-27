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

        // Center panel with form card
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(Styles.background);

        JPanel card = createCardPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;

        // Title
        JLabel title = new JLabel("Log a Meal");
        title.setFont(Styles.dtitle_font);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        card.add(title, gbc);

        // Date
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;
        card.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        dateField = new JTextField(12);
        dateField.setFont(Styles.default_font);
        card.add(dateField, gbc);

        // Meal
        gbc.gridy++;
        gbc.gridx = 0;
        card.add(new JLabel("Meal Type:"), gbc);
        gbc.gridx = 1;
        mealTypeDropdown = new JComboBox<>(new String[] { "Breakfast", "Lunch", "Dinner", "Snack" });
        mealTypeDropdown.setFont(Styles.default_font);
        card.add(mealTypeDropdown, gbc);

        // Ingredients
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        card.add(new JLabel("Ingredients (name, quantity in grams):"), gbc);
        gbc.gridy++;
        ingredient_input = new IngredientAdding(frame);
        JScrollPane ingScroll = new JScrollPane(ingredient_input);
        ingScroll.setPreferredSize(new Dimension(300, 120));
        card.add(ingScroll, gbc);

        // Add Ingredient Button
        gbc.gridy++;
        JButton addRowBtn = new JButton("+ Add Ingredient");
        addRowBtn.setBackground(Styles.lightorange);
        addRowBtn.setFont(Styles.default_font);
        addRowBtn.setFocusPainted(false);
        addRowBtn.addActionListener(e -> ingredient_input.addRow());
        card.add(addRowBtn, gbc);

        // Submit Button
        gbc.gridy++;
        JButton logButton = new JButton("✔ Log Meal");
        logButton.setFont(Styles.default_font);
        logButton.setBackground(new Color(168, 209, 123));
        logButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logButton.addActionListener(e -> {
            try {
                logMeal();
            } catch (IllegalArgumentException iae) {
                JOptionPane.showMessageDialog(this, iae.getMessage(), "Duplicate Meal", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "An unexpected error occurred:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        card.add(logButton, gbc);

        center.add(card);
        add(center, BorderLayout.CENTER);
    }

    private void logMeal() {
        LocalDate date = LocalDate.parse(dateField.getText().trim());
        String type = mealTypeDropdown.getSelectedItem().toString();
        List<String[]> rows = ingredient_input.getInputRows();

        if (rows.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill at least one ingredient.",
                    "Missing Ingredient", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<MealItem> items = new ArrayList<>();
        for (String[] row : rows) {
            try {
                String name = row[0];
                double quantity = Double.parseDouble(row[1]);
                items.add(new MealItem(name, quantity));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid quantity entered for: " + row[0], "Quantity Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        Meal meal = new Meal(profile.getId(), date, MealType.valueOf(type.toUpperCase()), items);

        // Save to DB
        MealDAO.insertMeal(profile.getId(), meal);

        JOptionPane.showMessageDialog(this, "Meal logged successfully.");

        // Switch to User Dashboard
        DietDashboardPanel diet = new DietDashboardPanel(frame, profile);
        frame.setContentPane(diet);
        frame.revalidate();
        frame.repaint();

    }
}
