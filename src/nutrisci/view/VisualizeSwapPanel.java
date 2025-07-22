package nutrisci.view;

import nutrisci.charts.SwapEffectChart;
import nutrisci.controller.VisualizationController;
import nutrisci.model.UserProfile;
import nutrisci.template.Base;
import nutrisci.template.DatePicker;
import nutrisci.template.Styles;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Map;

//VisualizeSwapPanel
 //Displays two independent visualization sections:
 // Bar Chart (Before vs After comparison)
 // Line Chart (Nutrient trend over time)
 
public class VisualizeSwapPanel extends Base {

    private final VisualizationController controller;
    private JPanel barChartContainer, lineChartContainer;
    private JComboBox<String> barNutrientCombo, lineNutrientCombo;
    private DatePicker barStartPicker, barEndPicker, lineStartPicker, lineEndPicker;
    private final UserProfile profile;

    public VisualizeSwapPanel(JFrame frame, UserProfile profile) {
        super(frame);
        this.controller = new VisualizationController();
        this.profile = profile;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Styles.background);

        // Top panel with Back button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Styles.background);

        JButton backButton = new JButton("← Back");
        backButton.addActionListener(e -> {
            frame.setContentPane(new UserDashboardPanel(frame, profile));
            frame.revalidate();
            frame.repaint();
        });
        topPanel.add(backButton, BorderLayout.WEST);

        JLabel title = new JLabel("Visualize Swap Effects", SwingConstants.CENTER);
        title.setFont(Styles.dtitle_font);
        topPanel.add(title, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // Main panel with two sections
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(2, 1, 10, 10));
        mainPanel.setBackground(Styles.background);

        // Section 1: Bar Chart
        // -------------------
        JPanel barSection = new JPanel(new BorderLayout());
        barSection.setBackground(Color.WHITE);
        barSection.setBorder(BorderFactory.createTitledBorder("Before vs After Comparison"));

        JPanel barControls = new JPanel();
        barControls.setBackground(Color.WHITE);

        barNutrientCombo = new JComboBox<>(new String[]{
                "All", "Energy", "Protein", "Carbohydrate", "Fat", "Fiber"
        });
        barStartPicker = new DatePicker();
        barEndPicker = new DatePicker();
        JButton generateBarBtn = new JButton("Generate Bar Chart");

        barControls.add(new JLabel("Start Date:"));
        barControls.add(barStartPicker);
        barControls.add(new JLabel("End Date:"));
        barControls.add(barEndPicker);
        barControls.add(new JLabel("Nutrient:"));
        barControls.add(barNutrientCombo);
        barControls.add(generateBarBtn);

        barSection.add(barControls, BorderLayout.NORTH);

        barChartContainer = new JPanel(new BorderLayout());
        barSection.add(barChartContainer, BorderLayout.CENTER);

        generateBarBtn.addActionListener(e -> renderBarChart());

        // Section 2: Line Chart
        // -------------------
        JPanel lineSection = new JPanel(new BorderLayout());
        lineSection.setBackground(Color.WHITE);
        lineSection.setBorder(BorderFactory.createTitledBorder("Nutrient Trend Over Time"));

        JPanel lineControls = new JPanel();
        lineControls.setBackground(Color.WHITE);

        lineNutrientCombo = new JComboBox<>(new String[]{
                "Energy", "Protein", "Carbohydrate", "Fat", "Fiber"
        }); // No "All" here
        lineStartPicker = new DatePicker();
        lineEndPicker = new DatePicker();
        JButton generateLineBtn = new JButton("Generate Line Chart");

        lineControls.add(new JLabel("Start Date:"));
        lineControls.add(lineStartPicker);
        lineControls.add(new JLabel("End Date:"));
        lineControls.add(lineEndPicker);
        lineControls.add(new JLabel("Nutrient:"));
        lineControls.add(lineNutrientCombo);
        lineControls.add(generateLineBtn);

        lineSection.add(lineControls, BorderLayout.NORTH);

        lineChartContainer = new JPanel(new BorderLayout());
        lineSection.add(lineChartContainer, BorderLayout.CENTER);

        generateLineBtn.addActionListener(e -> renderLineChart());

        // Add sections to main panel
        mainPanel.add(barSection);
        mainPanel.add(lineSection);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void renderBarChart() {
        try {
            LocalDate startDate = barStartPicker.getDate();
            LocalDate endDate = barEndPicker.getDate();
            String nutrient = (String) barNutrientCombo.getSelectedItem();

            Map<String, Map<String, Double>> data =
                    controller.getBeforeAfterTotals(profile.getId(), nutrient, startDate, endDate);

            Map<String, Double> before = data.get("before");
            Map<String, Double> after = data.get("after");

            JPanel chartPanel = SwapEffectChart.generateBarChart(before, after);

            barChartContainer.removeAll();
            barChartContainer.add(chartPanel, BorderLayout.CENTER);
            barChartContainer.revalidate();
            barChartContainer.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error generating Bar Chart: " + ex.getMessage());
        }
    }

    private void renderLineChart() {
        try {
            LocalDate startDate = lineStartPicker.getDate();
            LocalDate endDate = lineEndPicker.getDate();
            String nutrient = (String) lineNutrientCombo.getSelectedItem();

            Map<String, Double> trendData =
                    controller.getTrendData(profile.getId(), nutrient, startDate, endDate);

            JPanel chartPanel = SwapEffectChart.generateLineChart(trendData, nutrient);

            lineChartContainer.removeAll();
            lineChartContainer.add(chartPanel, BorderLayout.CENTER);
            lineChartContainer.revalidate();
            lineChartContainer.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error generating Line Chart: " + ex.getMessage());
        }
    }
}
