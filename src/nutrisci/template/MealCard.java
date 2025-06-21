package nutrisci.template;

import nutrisci.gui.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class MealCard extends JPanel {

    public MealCard(UserMeals meal) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
        setMaximumSize(new Dimension(500, Integer.MAX_VALUE));
        setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel header = new JLabel(meal.getDate() + "    |    " + meal.getType());
        header.setFont(Styles.default_font);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        List<String> formattedIngredients = meal.getItems().stream()
                .map(item -> item.getIngredient() + " (" + item.getQuantity() + "g)")
                .collect(Collectors.toList());

        JTextArea igdList = new JTextArea("Ingredients:\n - " + String.join("\n - ", formattedIngredients));
        igdList.setFont(Styles.small_font);
        igdList.setEditable(false);
        igdList.setOpaque(false);
        igdList.setBorder(null);

        add(header);
        add(igdList);
    }
}
