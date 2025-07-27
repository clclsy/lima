package nutrisci.view;

import java.awt.*;
import javax.swing.*;
import nutrisci.model.UserProfile;
import nutrisci.template.Base;
import nutrisci.template.ChartPanel;
import nutrisci.template.DatePicker;
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
        DatePicker datePicker = new DatePicker();

        JPanel dateRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 10));
        dateRow.setOpaque(false);
        dateRow.add(new JLabel("From:"));
        dateRow.add(new JTextField(datePicker.getStartDate(), 10));
        dateRow.add(new JLabel("To:"));
        dateRow.add(new JTextField(datePicker.getEndDate(), 10));
        
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

        chartGrid.add(ChartPanel.createPlaceholder("Swap Effects (Before vs After)"));
        chartGrid.add(ChartPanel.createPlaceholder("Meal Comparison: Original vs Swapped"));
        chartGrid.add(new AveragePlateChartComponent(profile));

        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(chartGrid);

        // Add content to main layout
        add(contentPanel, BorderLayout.CENTER);
    }
}
