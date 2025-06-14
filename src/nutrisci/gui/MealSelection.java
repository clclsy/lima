package nutrisci.gui;

import java.awt.*;
import javax.swing.*;

public class MealSelection extends JPanel {

    public MealSelection(JFrame frame, UserProfile profile) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        setBackground(new Color(255, 251, 245));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel selectMealLabel = new JLabel("Selected Meal:");
        selectMealLabel.setFont(new Font("Helvetica", Font.PLAIN, 14));

        JComboBox<String> mealdropdown = new JComboBox<>(new String[] { "Breakfast", "Lunch", "Dinner", "Snack" });
        mealdropdown.setFont(new Font("Helvetica", Font.PLAIN, 14));

        JButton viewmeals = new JButton("📋 View Logged Meals");
        viewmeals.setFont(new Font("Helvetica", Font.PLAIN, 14));
        viewmeals.setBackground(new Color(255, 150, 120));
        viewmeals.setFocusPainted(false);
        viewmeals.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewmeals.addActionListener(e -> frame.setContentPane(new ViewMeals(frame, profile)));

        JButton logmeal = new JButton("+ Log Meal");
        logmeal.setFont(new Font("Helvetica", Font.PLAIN, 14));
        logmeal.setBackground(new Color(168, 209, 123));
        logmeal.setFocusPainted(false);
        logmeal.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logmeal.addActionListener(e -> {
            frame.setContentPane(new LogMealPanel(frame, profile));
            frame.revalidate();
        });

        add(selectMealLabel);
        add(mealdropdown);
        add(viewmeals);
        add(logmeal);
    }

}
