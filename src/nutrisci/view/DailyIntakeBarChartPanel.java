package nutrisci.view;

import nutrisci.db.FoodDAO;
import nutrisci.db.MealDAO;
import nutrisci.db.NutritionDataDAO;
import nutrisci.model.Meal;
import nutrisci.model.MealItem;
import nutrisci.model.UserProfile;
import nutrisci.template.Styles;
import nutrisci.template.DatePicker;
import nutrisci.template.Base;
import org.jfree.chart.*;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class DailyIntakeBarChartPanel extends Base {
    private final UserProfile profile;
    private final DatePicker datePicker;
    private final JPanel chartContainer;

    private static final Map<String, Double> RDV = Map.of(
            "Protein", 50.0,
            "Carbohydrates", 275.0,
            "Fats", 70.0,
            "Fiber", 25.0,
            "Calcium", 1000.0,
            "Iron", 18.0,
            "Vitamin C", 90.0,
            "Vitamin A", 900.0,
            "Calories", 2000.0);

    public DailyIntakeBarChartPanel(JFrame frame, UserProfile profile) {
        super(frame);
        this.profile = profile;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top section with back button
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(createTopPanel(new UserDashboardPanel(frame, profile)), BorderLayout.WEST);

        JLabel title = new JLabel("Compare Your Nutrient Intake to RDVs", JLabel.CENTER);
        title.setFont(Styles.dtitle_font);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        top.add(title, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);

        // Date picker and generate button
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER));
        datePicker = new DatePicker();
        JButton generateBtn = new JButton("Generate Chart");
        generateBtn.addActionListener(e -> updateChart());

        controls.add(datePicker);
        controls.add(generateBtn);
        add(controls, BorderLayout.SOUTH);

        // Chart container
        chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(Color.WHITE);
        add(chartContainer, BorderLayout.CENTER);

        updateChart(); // render chart initially
    }

    private void updateChart() {
        chartContainer.removeAll();

        LocalDate start, end;
        try {
            start = LocalDate.parse(datePicker.getStartDate());
            end = LocalDate.parse(datePicker.getEndDate());
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
            return;
        }

        if (start.isAfter(end)) {
            JOptionPane.showMessageDialog(this, "Start date must be before end date.");
            return;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Double> averages = computeAverageNutrients(start, end);

        for (String nutrient : RDV.keySet()) {
            double avg = averages.getOrDefault(nutrient, 0.0);
            double target = RDV.get(nutrient);
            double percent = (avg / target) * 100;
            dataset.addValue(percent, "You", nutrient);
            dataset.addValue(100.0, "Recommended", nutrient);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Avg Daily Intake vs RDV",
                "Nutrient",
                "% of RDV",
                dataset);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.GRAY);

        BarRenderer renderer = new BarRenderer();
        renderer.setSeriesPaint(0, new Color(72, 118, 255)); // You
        renderer.setSeriesPaint(1, new Color(34, 139, 34)); // RDV
        plot.setRenderer(renderer);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(0, 200); // 0–200%
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                org.jfree.chart.axis.CategoryLabelPositions.UP_45);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 400));
        chartContainer.add(chartPanel, BorderLayout.CENTER);

        chartContainer.revalidate();
        chartContainer.repaint();
    }

    private Map<String, Double> computeAverageNutrients(LocalDate start, LocalDate end) {
        Map<String, Double> totals = new HashMap<>();
        List<Meal> meals = MealDAO.getMealsByUserId(profile.getId());
        long days = ChronoUnit.DAYS.between(start, end) + 1;

        for (Meal m : meals) {
            if (!m.getDate().isBefore(start) && !m.getDate().isAfter(end)) {
                for (MealItem item : m.getItems()) {
                    Integer foodId = new FoodDAO().getFoodIdByName(item.getIngredient());
                    if (foodId == null)
                        continue;

                    Map<String, Double> nutrients = NutritionDataDAO.getInstance().getFoodNutrients(foodId);
                    for (Map.Entry<String, Double> entry : nutrients.entrySet()) {
                        double totalAmount = entry.getValue() * item.getQuantity();
                        totals.merge(entry.getKey(), totalAmount / days, Double::sum);
                    }
                }
            }
        }

        return totals;
    }
}
