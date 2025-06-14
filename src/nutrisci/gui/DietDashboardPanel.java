package nutrisci.gui;

import java.awt.*;
import javax.swing.*;
import nutrisci.template.*;

public class DietDashboardPanel extends Base {

    public DietDashboardPanel(JFrame frame, UserProfile profile) {

        super(frame);
        setLayout(new BorderLayout());
        setBackground(new Color(255, 251, 245));

        //back button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setOpaque(false);
        topPanel.add(createBackButton(new UserDashboardPanel(frame, profile)));
        add(topPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(255, 251, 245));

        add(contentPanel, BorderLayout.CENTER);

        //greeting
        JLabel greeting = new JLabel("Hello, " + profile.getName() + "!", JLabel.CENTER);
        greeting.setFont(new Font("Georgia", Font.BOLD, 28));
        greeting.setAlignmentX(Component.CENTER_ALIGNMENT);
        greeting.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        contentPanel.add(greeting);

        //meal selection
        contentPanel.add(new MealSelection(frame, profile));

        //charts
        JPanel chartGrid = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        chartGrid.setBackground(new Color(255, 251, 245));
        chartGrid.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        chartGrid.add(ChartPanel.createPlaceholder("Daily Calories per Meal"));
        chartGrid.add(ChartPanel.createPlaceholder("Top 5 Nutrients Intake"));
        chartGrid.add(ChartPanel.createPlaceholder("Swap Effects (Before vs After)"));
        chartGrid.add(ChartPanel.createPlaceholder("Meal Comparison: Original vs Swapped"));
        chartGrid.add(ChartPanel.createPlaceholder("Canada Food Guide Alignment"));

        contentPanel.add(Box.createVerticalStrut(2));
        contentPanel.add(chartGrid);
        add(contentPanel);

    }
}
