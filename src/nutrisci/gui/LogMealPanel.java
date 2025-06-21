package nutrisci.gui;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import nutrisci.MainMenu;
import nutrisci.gui.UserProfile.Meal;
import nutrisci.template.*;

public class LogMealPanel extends Base {
    private final JTextField date;
    private final JComboBox<String> mealtype;
    private final IngredientAdding ingredient_input;
    private final UserProfile profile;

    public LogMealPanel(JFrame frame, UserProfile profile) {
        super(frame);
        this.profile = profile;
        setLayout(new BorderLayout());
        setBackground(Styles.background);

        // back button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setOpaque(false);
        topPanel.add(createBackButton(new MainMenu(frame)));
        add(topPanel, BorderLayout.NORTH);

        // form to log meal
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
        date = new JTextField(12);
        formCard.add(date, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formCard.add(new JLabel("Meal Type:"), gbc);
        gbc.gridx = 1;
        mealtype = new JComboBox<>(new String[] { "Breakfast", "Lunch", "Dinner", "Snack" });
        formCard.add(mealtype, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JLabel ingLabel = new JLabel("Ingredients (name + quantity):");
        formCard.add(ingLabel, gbc);

        gbc.gridy++;
        ingredient_input = new IngredientAdding();
        JScrollPane ingScroll = new JScrollPane(ingredient_input);
        ingScroll.setPreferredSize(new Dimension(300, 120));
        formCard.add(ingScroll, gbc);

        gbc.gridy++;
        JButton addRowBtn = new JButton("+ Add Ingredient");
        addRowBtn.setBackground(new Color(200, 255, 200));
        addRowBtn.setFont(new Font("Helvetica", Font.PLAIN, 14));
        addRowBtn.setFocusPainted(false);
        addRowBtn.addActionListener(e -> ingredient_input.addRow());
        formCard.add(addRowBtn, gbc);

        gbc.gridy++;
        JButton log_button = new JButton("✔ Log Meal");
        log_button.setFont(new Font("Helvetica", Font.PLAIN, 14));
        log_button.setBackground(new Color(168, 209, 123));
        log_button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // 👇 Add this
        System.out.println("🔘 Setting up log button");

        log_button.addActionListener(e -> {
            System.out.println("🟢 Log button clicked!");
            logMeal();
        });
        formCard.add(log_button, gbc);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(new Color(255, 251, 245));
        center.add(formCard);
        add(center, BorderLayout.CENTER);
    }

    private void logMeal() {
        System.out.println("📥 Log button clicked");

        String date_text = date.getText().trim();
        String meal_type = mealtype.getSelectedItem().toString();
        ArrayList<String> ingredients = ingredient_input.getFormattedIngredients();

        if (date_text.isEmpty() || ingredients.isEmpty()) {
            System.out.println("⚠ Missing data: date or ingredients");
            JOptionPane.showMessageDialog(this, "Please enter a date and at least one valid ingredient.",
                    "Missing Data", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Meal meal = new Meal(date_text, meal_type, ingredients);
        profile.addMeal(meal);

        System.out.println("✅ Meal added to profile: " + profile.getName());

        UserProfile.saveProfilesToFile(); // ← should trigger terminal print
        System.out.println("💾 Attempted to save");

        JOptionPane.showMessageDialog(this, "Meal logged successfully!", "Meal Saved", JOptionPane.INFORMATION_MESSAGE);

    }
}