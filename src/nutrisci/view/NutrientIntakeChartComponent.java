package nutrisci.view;

import nutrisci.db.*;
import nutrisci.model.*;
import org.jfree.chart.*;
import org.jfree.chart.panel.*;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import nutrisci.template.Styles;

public class NutrientIntakeChartComponent extends JPanel {

    private final UserProfile profile;
    private final JPanel chartHolder;

    public NutrientIntakeChartComponent(UserProfile profile) {
        this.profile = profile;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(250, 200));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Header
        JLabel title = new JLabel("Daily Nutrient Intake", JLabel.CENTER);
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
        List<Meal> meals = MealDAO.getMealsByUserId(profile.getId());

        Map<String, Double> totals = new HashMap<>();
        NutritionDataDAO nutritionDAO = NutritionDataDAO.getInstance();

        for (Meal m : meals) {
            if (!m.getDate().equals(today)) continue;

            for (MealItem i : m.getItems()) {
                Integer foodId = FoodDAO.getFoodIdByName(i.getIngredient());
                if (foodId == null) continue;

                Map<String, Double> nutrients = nutritionDAO.getFoodNutrients(foodId);
                for (var entry : nutrients.entrySet()) {
                    totals.merge(entry.getKey(), entry.getValue() * i.getQuantity(), Double::sum);
                }
            }
        }

        if (totals.isEmpty()) {
            JLabel label = new JLabel("No meals logged today", JLabel.CENTER);
            label.setFont(Styles.ismall_font);
            chartHolder.add(label, BorderLayout.CENTER);
        } else {
            List<Map.Entry<String, Double>> sorted = new ArrayList<>(totals.entrySet());
            sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

            DefaultPieDataset dataset = new DefaultPieDataset();
            double other = 0;
            for (int i = 0; i < sorted.size(); i++) {
                if (i < 5) dataset.setValue(sorted.get(i).getKey(), sorted.get(i).getValue());
                else other += sorted.get(i).getValue();
            }
            if (other > 0) dataset.setValue("Other", other);

            JFreeChart chart = ChartFactory.createPieChart(null, dataset, false, false, false);
            ChartPanel pie = new ChartPanel(chart);
            pie.setPopupMenu(null);
            pie.setMouseWheelEnabled(false);
            pie.setPreferredSize(new Dimension(240, 150));
            chartHolder.add(pie, BorderLayout.CENTER);
        }

        chartHolder.revalidate();
        chartHolder.repaint();
    }
}
