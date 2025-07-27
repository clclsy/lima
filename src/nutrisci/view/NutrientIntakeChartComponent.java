package nutrisci.view;

import nutrisci.db.*;
import nutrisci.model.*;
import org.jfree.chart.*;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import nutrisci.template.Styles;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class NutrientIntakeChartComponent extends JPanel {
    private final UserProfile profile;
    private final JPanel chartHolder;

    public NutrientIntakeChartComponent(UserProfile profile) {
        this.profile = profile;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(300, 250));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Header
        JLabel title = new JLabel("Today's Nutrient Intake", JLabel.CENTER);
        title.setFont(Styles.default_font);
        add(title, BorderLayout.NORTH);

        chartHolder = new JPanel(new BorderLayout());
        chartHolder.setBackground(Color.WHITE);
        add(chartHolder, BorderLayout.CENTER);

        renderChart();
    }

    private void renderChart() {
        chartHolder.removeAll();

        LocalDate today = LocalDate.now();
        java.util.List<Meal> meals = MealDAO.getMealsByUserId(profile.getId());

        Map<String, Double> totals = new HashMap<>();
        NutritionDataDAO nutritionDAO = NutritionDataDAO.getInstance();

        for (Meal m : meals) {
            if (!m.getDate().equals(today))
                continue;

            for (MealItem i : m.getItems()) {
                FoodDAO foodDao = new FoodDAO();
                Integer foodId = foodDao.getFoodIdByName(i.getIngredient());

                if (foodId == null)
                    continue;

                Map<String, Double> nutrients = nutritionDAO.getFoodNutrients(foodId);
                for (Map.Entry<String, Double> entry : nutrients.entrySet()) {
                    totals.merge(entry.getKey(), entry.getValue() * i.getQuantity(), Double::sum);
                }
            }
        }

        if (totals.isEmpty()) {
            JLabel label = new JLabel("No meals logged today", JLabel.CENTER);
            label.setFont(Styles.ismall_font);
            chartHolder.add(label, BorderLayout.CENTER);
        } else {
            createNutrientChart(totals);
        }
        chartHolder.revalidate();
        chartHolder.repaint();
    }

    private void createNutrientChart(Map<String, Double> totals) {
        java.util.List<Map.Entry<String, Double>> sorted = new ArrayList<>(totals.entrySet());
        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        DefaultPieDataset dataset = new DefaultPieDataset();
        double other = 0;
        for (int i = 0; i < sorted.size(); i++) {
            if (i < 5) {
                dataset.setValue(sorted.get(i).getKey(), sorted.get(i).getValue());
            } else {
                other += sorted.get(i).getValue();
            }
        }
        if (other > 0)
            dataset.setValue("Other", other);

        JFreeChart chart = ChartFactory.createPieChart(
                null,
                dataset,
                false,
                true, // Include legend
                false);

        // Add custom labels
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0}: {1} ({2})",
                new DecimalFormat("0.0"),
                new DecimalFormat("0%")));

        // Customize colors
        plot.setSectionPaint("Protein", new Color(65, 105, 225));
        plot.setSectionPaint("Carbohydrate, total", new Color(34, 139, 34));
        plot.setSectionPaint("Fat, total", new Color(220, 20, 60));
        plot.setSectionPaint("Fibre, total dietary", new Color(255, 140, 0));
        plot.setSectionPaint("Energy", new Color(148, 0, 211));

        // Create chart panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(280, 180));
        chartHolder.add(chartPanel, BorderLayout.CENTER);
    }

    public void refreshChart() {
        renderChart();
    }
}