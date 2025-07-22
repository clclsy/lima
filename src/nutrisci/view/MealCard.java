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
        setPreferredSize(new Dimension(250, 200));
        setMaximumSize(new Dimension(250, 200));
        setMinimumSize(new Dimension(250, 200));
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

        add(header);
        add(listPanel);
    }
}
