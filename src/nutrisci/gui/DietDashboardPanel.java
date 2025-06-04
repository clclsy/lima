package nutrisci.gui;

import java.awt.*;
import javax.swing.*;
import nutrisci.MainMenu;

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
            frame.setContentPane(new MainMenu(frame));
            frame.revalidate();
        });
        topPanel.add(backBtn);
        add(topPanel, BorderLayout.NORTH);

        // === Greeting ===
        JLabel greeting = new JLabel("Hello, " + profile.getName() + "!");
        greeting.setFont(new Font("Georgia", Font.BOLD, 28));
        greeting.setHorizontalAlignment(SwingConstants.CENTER);
        greeting.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // === Center Panel ===
        JPanel centerWrapper = new JPanel();
centerWrapper.setLayout(new BoxLayout(centerWrapper, BoxLayout.Y_AXIS));
centerWrapper.setBackground(new Color(255, 251, 245));

// Add greeting at top
centerWrapper.add(greeting);

        // === Meal Selection Row ===
        JPanel mealSelectionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        mealSelectionPanel.setBackground(new Color(255, 251, 245));

        JLabel selectMealLabel = new JLabel("Selected Meal:");
        selectMealLabel.setFont(new Font("Helvetica", Font.PLAIN, 14));

        JComboBox<String> mealDropdown = new JComboBox<>(new String[]{"Breakfast", "Lunch", "Dinner", "Snack"});
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

        mealSelectionPanel.add(selectMealLabel);
        mealSelectionPanel.add(mealDropdown);
        mealSelectionPanel.add(logMealBtn);

        // Center Wrapper
        centerWrapper.setLayout(new BoxLayout(centerWrapper, BoxLayout.Y_AXIS));
        centerWrapper.setBackground(new Color(255, 251, 245));
        centerWrapper.add(mealSelectionPanel);

        // === Dashboard Charts ===
        JPanel chartGrid = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        chartGrid.setBackground(new Color(255, 251, 245));
        chartGrid.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        chartGrid.add(createChartPlaceholder("Daily Calories per Meal"));
        chartGrid.add(createChartPlaceholder("Top 5 Nutrients Intake"));
        chartGrid.add(createChartPlaceholder("Swap Effects (Before vs After)"));
        chartGrid.add(createChartPlaceholder("Meal Comparison: Original vs Swapped"));
        chartGrid.add(createChartPlaceholder("Canada Food Guide Alignment"));

        centerWrapper.add(chartGrid);
        add(centerWrapper, BorderLayout.CENTER);
    }

    private JPanel createChartPlaceholder(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(250, 200));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel chartTitle = new JLabel(title);
        chartTitle.setFont(new Font("Helvetica", Font.BOLD, 14));
        chartTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel chartMock = new JLabel("[ Chart Placeholder ]", SwingConstants.CENTER);
        chartMock.setFont(new Font("Helvetica", Font.ITALIC, 12));
        chartMock.setForeground(Color.GRAY);

        panel.add(chartTitle, BorderLayout.NORTH);
        panel.add(chartMock, BorderLayout.CENTER);

        return panel;
    }
}
