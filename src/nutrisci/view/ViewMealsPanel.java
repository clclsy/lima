package nutrisci.view;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import nutrisci.db.MealDAO;
import nutrisci.model.*;
import nutrisci.template.*;

public class ViewMealsPanel extends Base {
    private final UserProfile profile;
    private final JPanel mealContainer = new JPanel();
    private final JLabel countLabel = new JLabel();
    private final DatePicker datePicker = new DatePicker();
    private final JLabel noMealMsg = new JLabel("No meals logged yet.");

    public ViewMealsPanel(JFrame frame, UserProfile profile) {
        super(frame);
        this.profile = profile;

        setLayout(new BorderLayout());
        add(createTopPanel(new DietDashboardPanel(frame, profile)), BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Styles.background);

        // Title
        JLabel title = new JLabel("Logged Meals for " + profile.getName());
        title.setFont(Styles.dtitle_font);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 10, 0));
        content.add(title);

        // Total meal count
        countLabel.setFont(Styles.default_font);
        countLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(countLabel);

        // Date filter row
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        filterRow.setOpaque(false);
        filterRow.add(datePicker);

        JButton filterBtn = new JButton("Apply");
        filterBtn.setFont(Styles.small_font);
        filterBtn.setBackground(Styles.lightorange);
        filterBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        filterBtn.addActionListener(e -> refreshMeals());
        filterRow.add(filterBtn);
        content.add(filterRow);

        content.add(Box.createVerticalStrut(10));

        // Meal container
        mealContainer.setLayout(new BoxLayout(mealContainer, BoxLayout.Y_AXIS));
        mealContainer.setBackground(Styles.background);

        // "No meals" message
        noMealMsg.setFont(Styles.ismall_font);
        noMealMsg.setForeground(Color.GRAY);
        noMealMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
        noMealMsg.setVisible(false);
        content.add(noMealMsg);

        JScrollPane scroll = new JScrollPane(mealContainer);
        scroll.getViewport().setBackground(Styles.background);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setPreferredSize(new Dimension(500, 400));
        content.add(centered(scroll));

        add(content, BorderLayout.CENTER);
        refreshMeals();
    }

    private void refreshMeals() {
        mealContainer.removeAll();
        noMealMsg.setVisible(false);

        LocalDate parsedStart = null;
        LocalDate parsedEnd = null;
        try {
            parsedStart = LocalDate.parse(datePicker.getStartDate());
            parsedEnd = LocalDate.parse(datePicker.getEndDate());
        } catch (Exception e) {
            System.out.println("Invalid date input. Skipping range filter.");
        }
        final LocalDate start = parsedStart;
        final LocalDate end = parsedEnd;
        int userId = profile.getId();
        List<Meal> allMeals = MealDAO.getMealsByUserId(userId);
        List<Meal> filtered = allMeals.stream()
                .filter(m -> {
                    LocalDate date = m.getDate();
                    return start == null || end == null || (!date.isBefore(start) && !date.isAfter(end));
                })
                .collect(Collectors.toList());

        countLabel.setText("Total Meals: " + filtered.size());

        if (filtered.isEmpty()) {
            noMealMsg.setVisible(true);
        } else {
            for (Meal meal : filtered) {
                mealContainer.add(new MealCard(meal));
                mealContainer.add(Box.createVerticalStrut(10));
            }
        }

        mealContainer.revalidate();
        mealContainer.repaint();
    }
}
