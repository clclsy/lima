package nutrisci.view;

import java.awt.*;
import javax.swing.*;
import nutrisci.model.MealType;
import nutrisci.model.UserProfile;
import nutrisci.template.Styles;

public class MealSelectionPanel extends JPanel {

    public MealSelectionPanel(JFrame frame, UserProfile profile) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        setBackground(Styles.background);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel selectLabel = new JLabel("Meal Type:");
        selectLabel.setFont(Styles.default_font);

        JComboBox<MealType> mealDropdown = new JComboBox<>(MealType.values());
        mealDropdown.setFont(Styles.default_font);

        JButton viewBtn = new JButton(" View Logged Meals");
        viewBtn.setFont(Styles.default_font);
        viewBtn.setBackground(Styles.lightorange);
        viewBtn.setFocusPainted(false);
        viewBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewBtn.addActionListener(e -> {
            frame.setContentPane(new ViewMealsPanel(frame, profile));
            frame.revalidate();
        });

        JButton logBtn = new JButton("+ Log Meal");
        logBtn.setFont(Styles.default_font);
        logBtn.setBackground(new Color(168, 209, 123));
        logBtn.setFocusPainted(false);
        logBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logBtn.addActionListener(e -> {
            frame.setContentPane(new LogMealPanel(frame, profile));
            frame.revalidate();
        });

        add(selectLabel);
        add(mealDropdown);
        add(viewBtn);
        add(logBtn);
    }
}