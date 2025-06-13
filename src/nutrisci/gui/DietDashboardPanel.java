package nutrisci.gui;

import java.awt.*;
import javax.swing.*;

public class DietDashboardPanel extends JPanel {
    public DietDashboardPanel(JFrame frame, UserProfile profile) {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 251, 245));

        // === Top Bar with Back Button ===
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setOpaque(false);
        JButton backBtn = new JButton("←");
        backBtn.setFont(new Font("Helvetica", Font.BOLD, 24));
        backBtn.setForeground(new Color(0x564C4D));
        backBtn.setFocusPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            frame.setContentPane(new UserDashboardPanel(frame, profile));
            frame.revalidate();
        });
        topPanel.add(backBtn);
        add(topPanel, BorderLayout.NORTH);

        // === Center Content Panel ===
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(255, 251, 245));

        // Greeting
        JLabel greeting = new JLabel("Hello, " + profile.getName() + "!", JLabel.CENTER);
        greeting.setFont(new Font("Georgia", Font.BOLD, 28));
        greeting.setAlignmentX(Component.CENTER_ALIGNMENT);
        greeting.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        contentPanel.add(greeting);

        // Meal Selection
        JPanel mealPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        mealPanel.setBackground(new Color(255, 251, 245));
        mealPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // Explicitly clear any margin

        mealPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // restrict height

        JLabel selectMealLabel = new JLabel("Selected Meal:");
        selectMealLabel.setFont(new Font("Helvetica", Font.PLAIN, 14));

        JComboBox<String> mealDropdown = new JComboBox<>(new String[] { "Breakfast", "Lunch", "Dinner", "Snack" });
        mealDropdown.setFont(new Font("Helvetica", Font.PLAIN, 14));

        JButton logMealBtn = new JButton("+ Log Meal");
        logMealBtn.setFont(new Font("Helvetica", Font.PLAIN, 14));
        logMealBtn.setBackground(new Color(168, 209, 123));
        logMealBtn.setFocusPainted(false);
        logMealBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logMealBtn.addActionListener(e -> {
            frame.setContentPane(new LogMealPanel(frame, profile));
            frame.revalidate();
        });

        mealPanel.add(selectMealLabel);
        mealPanel.add(mealDropdown);
        mealPanel.add(logMealBtn);
        contentPanel.add(mealPanel);
        // === Chart Grid ===
        JPanel chartGrid = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        chartGrid.setBackground(new Color(255, 251, 245));
        chartGrid.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        chartGrid.add(createChartPlaceholder("Daily Calories per Meal"));
        chartGrid.add(createChartPlaceholder("Top 5 Nutrients Intake"));
        chartGrid.add(createChartPlaceholder("Swap Effects (Before vs After)"));
        chartGrid.add(createChartPlaceholder("Meal Comparison: Original vs Swapped"));
        chartGrid.add(createChartPlaceholder("Canada Food Guide Alignment"));

        contentPanel.add(Box.createVerticalStrut(2)); // small spacing between meal selector and charts

        contentPanel.add(chartGrid);
        add(contentPanel);
    }

    private JPanel createChartPlaceholder(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(250, 200));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Helvetica", Font.BOLD, 14));

        JLabel placeholder = new JLabel("[ Chart Placeholder ]", JLabel.CENTER);
        placeholder.setFont(new Font("Helvetica", Font.ITALIC, 12));
        placeholder.setForeground(Color.GRAY);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(placeholder, BorderLayout.CENTER);
        return panel;
    }
}
