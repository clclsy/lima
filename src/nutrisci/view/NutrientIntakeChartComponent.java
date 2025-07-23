package nutrisci.view;

import nutrisci.db.*;
import nutrisci.model.*;
import org.jfree.chart.*;
import org.jfree.chart.panel.*;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import nutrisci.template.Styles;

public class NutrientIntakeChartComponent extends JPanel {

    private final JComboBox<String> timeDropdown;
    private final UserProfile profile;
    private final JPanel chartHolder;

    public NutrientIntakeChartComponent(UserProfile profile) {
        this.profile = profile;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(250, 200));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Title and time range selector
        JPanel top = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Top 5 Nutrients", JLabel.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 14));

        timeDropdown = new JComboBox<>(new String[]{"7 days", "14 days", "30 days"});
        timeDropdown.setFont(new Font("SansSerif", Font.PLAIN, 11));
        timeDropdown.addActionListener(e -> renderChart());

        top.add(title, BorderLayout.CENTER);
        top.add(timeDropdown, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        chartHolder = new JPanel(new BorderLayout());
        chartHolder.setBackground(Color.WHITE);
        add(chartHolder, BorderLayout.CENTER);

        renderChart();
    }

    private void renderChart() {
        chartHolder.removeAll();

        int days = switch ((String) timeDropdown.getSelectedItem()) {
            case "14 days" -> 14;
            case "30 days" -> 30;
            default -> 7;
        };

        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(days - 1);
        List<Meal> meals = MealDAO.getMealsByUserId(profile.getId());

        Map<String, Double> totals = new HashMap<>();
        NutritionDataDAO nutritionDAO = NutritionDataDAO.getInstance();

        for (Meal m : meals) {
            if (m.getDate().isBefore(start) || m.getDate().isAfter(end)) continue;

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
            JLabel label = new JLabel("No meals logged during this period", JLabel.CENTER);
            label.setFont(Styles.ismall_font);
            chartHolder.add(label, BorderLayout.CENTER);
        } else {
            long dayCount = ChronoUnit.DAYS.between(start, end) + 1;
            totals.replaceAll((k, v) -> v / dayCount); // average daily intake

            List<Map.Entry<String, Double>> sorted = new ArrayList<>(totals.entrySet());
            sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

            DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
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

