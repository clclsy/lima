package nutrisci.template;

import java.awt.*;
import javax.swing.*;

public class MealCard extends JPanel {
    public MealCard(String title, String nutrients) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        setBackground(Color.WHITE);
        setMaximumSize(new Dimension(220, 150));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Styles.default_font);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea nutrientText = new JTextArea(nutrients);
        nutrientText.setFont(Styles.small_font);
        nutrientText.setEditable(false);
        nutrientText.setWrapStyleWord(true);
        nutrientText.setLineWrap(true);
        nutrientText.setOpaque(false);

        add(titleLabel);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(nutrientText);
    }
}
