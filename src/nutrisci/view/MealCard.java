package nutrisci.view;

import java.awt.*;
import javax.swing.*;
import nutrisci.model.*;
import nutrisci.template.Styles;

public class MealCard extends JPanel {
    public MealCard(Meal meal) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel header = new JLabel(meal.getDate() + " | " + meal.getType());
        header.setFont(Styles.bdefault_font);
        add(header);

        for (MealItem item : meal.getItems()) {
            JLabel igd = new JLabel("• " + item.getIngredient() + " (" + item.getQuantity() + "g)");
            igd.setFont(Styles.default_font);
            add(igd);
        }

    }
}
