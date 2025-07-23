package nutrisci.view;

import nutrisci.model.Meal;
import nutrisci.model.MealItem;
import nutrisci.template.Base;
import nutrisci.template.Styles;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * CompareSwapPanel
 * - Shows side-by-side original and swapped meals' food lists with highlights & tooltips
 * - Shows nutrient comparison table below
 */
public class CompareSwapPanel extends Base {

    private final Meal originalMeal;
    private final Meal swappedMeal;

    private final DefaultTableModel nutrientTableModel;

    public CompareSwapPanel(JFrame frame, Meal originalMeal, Meal swappedMeal) {
        super(frame);
        this.originalMeal = originalMeal;
        this.swappedMeal = swappedMeal;

        setLayout(new BorderLayout());
        setBackground(Styles.background);

        // Split pane: left original meal, right swapped meal
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                createMealPanel(originalMeal, "Original Meal", swappedMeal),
                createMealPanel(swappedMeal, "Swapped Meal", originalMeal));
        split.setResizeWeight(0.5);
        add(split, BorderLayout.CENTER);

        // Nutrient comparison table at bottom
        nutrientTableModel = new DefaultTableModel(new String[]{"Nutrient", "Original", "Swapped", "Difference"}, 0);
        JTable nutrientTable = new JTable(nutrientTableModel);

        nutrientTable.getColumnModel().getColumn(3).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JLabel label = new JLabel(String.valueOf(value));
            label.setOpaque(true);
            double diff = 0;
            if (value instanceof Number) {
                diff = ((Number) value).doubleValue();
            } else {
                try {
                    diff = Double.parseDouble(value.toString());
                } catch (Exception ignored) {
                }
            }
            label.setForeground(diff > 0 ? new Color(0, 128, 0) : (diff < 0 ? Color.RED : Color.BLACK));
            return label;
        });

        JScrollPane scroll = new JScrollPane(nutrientTable);
        scroll.setPreferredSize(new Dimension(0, 180));
        add(scroll, BorderLayout.SOUTH);

        showNutrientComparison(originalMeal, swappedMeal);
    }

    private JPanel createMealPanel(Meal meal, String title, Meal otherMeal) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(title));

        DefaultListModel<String> listModel = new DefaultListModel<>();

        Map<String, Double> mealFoodMap = mealItemsToMap(meal);
        Map<String, Double> otherMealFoodMap = mealItemsToMap(otherMeal);

        // Populate list model: "FoodName (qty g)"
        for (Map.Entry<String, Double> entry : mealFoodMap.entrySet()) {
            listModel.addElement(entry.getKey() + " (" + entry.getValue() + "g)");
        }

        JList<String> list = new JList<>(listModel);

        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                String item = (String) value;
                String foodName = item.split(" \\(")[0];

                if (!otherMealFoodMap.containsKey(foodName)) {
                    c.setBackground(new Color(255, 255, 150)); // yellow for added/removed
                } else {
                    double origQty = otherMealFoodMap.get(foodName);
                    double swappedQty = mealFoodMap.get(foodName);
                    if (origQty == 0) {
                        c.setBackground(new Color(255, 255, 150));
                    } else if (Math.abs(origQty - swappedQty) / origQty > 0.05) {
                        c.setBackground(new Color(200, 255, 200)); // green for changed qty
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }

                if (isSelected) {
                    c.setBackground(list.getSelectionBackground());
                    c.setForeground(list.getSelectionForeground());
                }
                return c;
            }
        });

        list.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int idx = list.locationToIndex(e.getPoint());
                if (idx > -1) {
                    String item = list.getModel().getElementAt(idx);
                    String foodName = item.split(" \\(")[0];

                    double origQty = mealItemsToMap(originalMeal).getOrDefault(foodName, 0.0);
                    double swappedQty = mealItemsToMap(swappedMeal).getOrDefault(foodName, 0.0);

                    if (origQty == 0.0) {
                        list.setToolTipText(foodName + " was added in swapped meal");
                    } else if (swappedQty == 0.0) {
                        list.setToolTipText(foodName + " was removed in swapped meal");
                    } else {
                        list.setToolTipText(foodName + ": quantity changed from " + origQty + "g to " + swappedQty + "g");
                    }
                } else {
                    list.setToolTipText(null);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(list);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void showNutrientComparison(Meal original, Meal swapped) {
        nutrientTableModel.setRowCount(0);

        Map<String, Double> origNutrients = mealNutrientsMap(original);
        Map<String, Double> swappedNutrients = mealNutrientsMap(swapped);

        TreeSet<String> nutrients = new TreeSet<>(origNutrients.keySet());
        nutrients.addAll(swappedNutrients.keySet());

        for (String nut : nutrients) {
            double origVal = origNutrients.getOrDefault(nut, 0.0);
            double swappedVal = swappedNutrients.getOrDefault(nut, 0.0);
            double diff = swappedVal - origVal;

            nutrientTableModel.addRow(new Object[]{
                    nut,
                    String.format("%.2f", origVal),
                    String.format("%.2f", swappedVal),
                    diff
            });
        }
    }

    // Convert MealItem list to Map<String foodName, Double quantity>
    private Map<String, Double> mealItemsToMap(Meal meal) {
        Map<String, Double> map = new HashMap<>();
        if (meal == null || meal.getItems() == null) return map;
        for (MealItem item : meal.getItems()) {
            map.put(item.getIngredient(), item.getQuantity());
        }
        return map;
    }

    // Assuming Meal class has a getNutrients method, else you implement as needed
    private Map<String, Double> mealNutrientsMap(Meal meal) {
        // You need to implement this to extract nutrients totals from Meal or MealItems
        // For example:
        // Aggregate nutrients from all MealItems and return as map.
        // Here's a stub example returning empty map:
        return new HashMap<>();
    }
}

