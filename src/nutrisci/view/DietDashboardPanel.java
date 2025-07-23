package nutrisci.view;

import java.awt.*;
import javax.swing.*;
import nutrisci.model.MealType;
import nutrisci.model.UserProfile;
import nutrisci.template.Base;
import nutrisci.template.ChartPanel;
import nutrisci.template.Styles;

public class DietDashboardPanel extends Base {

    public DietDashboardPanel(JFrame frame, UserProfile profile) {
        super(frame);
        init(frame, profile);
    }

    private void init(JFrame frame, UserProfile profile) {
        setLayout(new BorderLayout());
        setBackground(Styles.background);

        // Top section with back button and greeting
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        // Back button
        top.add(createTopPanel(new UserDashboardPanel(frame, profile)), BorderLayout.WEST);

        // Greeting title
        JLabel greeting = new JLabel(profile.getName() + "'s Diet Dashboard");
        greeting.setFont(Styles.dtitle_font);
        greeting.setHorizontalAlignment(SwingConstants.CENTER);
        greeting.setBorder(BorderFactory.createEmptyBorder(60, 0, 10, 0));
        top.add(greeting, BorderLayout.CENTER);

        // Row of meal selector and action buttons
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        actionRow.setOpaque(false);

        // Meal dropdown label
        JLabel mealLabel = new JLabel("Selected Meal:");
        mealLabel.setFont(Styles.default_font);
        actionRow.add(mealLabel);

        // MealType dropdown
        JComboBox<MealType> mealDropdown = new JComboBox<>(MealType.values());
        mealDropdown.setFont(Styles.default_font);
        actionRow.add(mealDropdown);

        // Dropdown listener (ready for future chart filtering)
        mealDropdown.addActionListener(e -> {
            MealType selected = (MealType) mealDropdown.getSelectedItem();
            System.out.println("Selected meal type: " + selected);
            // TODO: update charts based on meal type
        });

        // View Meals button
        JButton viewBtn = new JButton("View Logged Meals");
        viewBtn.setFont(Styles.default_font);
        viewBtn.setBackground(Styles.lightorange);
        viewBtn.setFocusPainted(false);
        viewBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewBtn.addActionListener(e -> {
            frame.setContentPane(new ViewMealsPanel(frame, profile));
            frame.revalidate();
            frame.repaint(); });
        actionRow.add(viewBtn);

        // Log Meal button
        JButton logBtn = new JButton("+ Log Meal");
        logBtn.setFont(Styles.default_font);
        logBtn.setBackground(new Color(168, 209, 123));
        logBtn.setFocusPainted(false);
        logBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logBtn.addActionListener(e -> {
            frame.setContentPane(new LogMealPanel(frame, profile));
            frame.revalidate();
            frame.repaint();
        });
        actionRow.add(logBtn);

        // Add the action row below greeting
        top.add(actionRow, BorderLayout.SOUTH);

        // Add top section to layout
        add(top, BorderLayout.NORTH);

        // Space for charts

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Styles.background);

        // Chart Grid
        JPanel chartGrid = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        chartGrid.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        chartGrid.setBackground(Styles.background);

        chartGrid.add(ChartPanel.createPlaceholder("Daily Calories per Meal"));
        chartGrid.add(new NutrientIntakeChartComponent(profile));
        chartGrid.add(ChartPanel.createPlaceholder("Swap Effects (Before vs After)"));
        chartGrid.add(ChartPanel.createPlaceholder("Meal Comparison: Original vs Swapped"));
        chartGrid.add(ChartPanel.createPlaceholder("Canada Food Guide Alignment"));

        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(chartGrid);

        // Add content to main layout
        add(contentPanel, BorderLayout.CENTER);
    }
}
