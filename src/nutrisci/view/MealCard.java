package nutrisci.view;

import java.awt.*;
import javax.swing.*;
import nutrisci.model.Meal;
import nutrisci.model.MealItem;
import nutrisci.template.Styles;

public class MealCard extends JPanel {

    public MealCard(Meal meal) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        // Set all size constraints to be identical
        Dimension cardSize = new Dimension(250, 200);
        setPreferredSize(cardSize);
        setMaximumSize(cardSize);
        setMinimumSize(cardSize);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));

        JLabel header = new JLabel(meal.getDate() + " | " + meal.getType());
        header.setFont(Styles.bdefault_font);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        for (MealItem item : meal.getItems()) {
            JLabel label = new JLabel("~ " + item.getIngredient() + " (" + item.getQuantity() + "g)");
            label.setFont(Styles.default_font);
            listPanel.add(label);
        }
        
        double calories = 0.0;
        try {
            var nutrients = nutrisci.util.NutrientCalculator.calculateMealNutrients(meal);
            calories = nutrients.getOrDefault("Calories", 0.0);
        } catch (Exception ignored) {
        }

        JLabel kcal = new JLabel("Calories: " + Math.round(calories) + " kcal");
        kcal.setFont(Styles.default_font);
        kcal.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        add(header);
        add(kcal);
        add(listPanel);
    }
}
