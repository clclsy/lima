// UI for UC8: extends Base for styling, uses DatePicker

package nutrisci.view;
import nutrisci.charts.SwapEffectChart;
import nutrisci.controller.VisualizationController;
import nutrisci.template.Base;
import nutrisci.template.DatePicker;
import nutrisci.template.Styles;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Map;

/**
 * VisualizeSwapPanel:
 * - Lets user pick date range and chart type
 * - Calls VisualizationController
 * - Displays chart for Before vs After swaps
 */
public class VisualizeSwapPanel extends Base {

    private final VisualizationController controller;
    private JPanel chartContainer;
    private JComboBox<String> chartTypeCombo;
    private DatePicker startDatePicker, endDatePicker;

    public VisualizeSwapPanel(JFrame frame) {
        super(frame);
        this.controller = new VisualizationController();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Styles.background);

        // Top controls
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(Styles.background);

        startDatePicker = new DatePicker();
        endDatePicker = new DatePicker();
        chartTypeCombo = new JComboBox<>(new String[]{"Bar Chart", "Line Chart"});
        JButton generateButton = new JButton("Generate Visualization");

        controlPanel.add(new JLabel("Start Date:"));
        controlPanel.add(startDatePicker);
        controlPanel.add(new JLabel("End Date:"));
        controlPanel.add(endDatePicker);
        controlPanel.add(new JLabel("Chart Type:"));
        controlPanel.add(chartTypeCombo);
        controlPanel.add(generateButton);

        add(controlPanel, BorderLayout.NORTH);

        // Chart container
        chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(Color.WHITE);
        add(chartContainer, BorderLayout.CENTER);

        // Button action
        generateButton.addActionListener(e -> renderChart());
    }

    private void renderChart() {
        try {
            LocalDate startDate = startDatePicker.getDate();
            LocalDate endDate = endDatePicker.getDate();

            Map<String, Map<String, Double>> data = controller.getBeforeAfterTotals(1, startDate, endDate);
            Map<String, Double> before = data.get("Before");
            Map<String, Double> after = data.get("After");

            JPanel chartPanel;
            if ("Bar Chart".equals(chartTypeCombo.getSelectedItem())) {
                chartPanel = SwapEffectChart.generateBarChart(before, after);
            } else {
                chartPanel = SwapEffectChart.generateLineChart(before, after);
            }

            chartContainer.removeAll();
            chartContainer.add(chartPanel, BorderLayout.CENTER);
            chartContainer.revalidate();
            chartContainer.repaint();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error generating chart: " + ex.getMessage());
        }
    }

}
