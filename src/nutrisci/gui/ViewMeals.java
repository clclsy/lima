package nutrisci.gui;

import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.*;
import nutrisci.template.*;

public class ViewMeals extends Base {

    private final UserProfile profile;
    private final JPanel mealPanel = new JPanel();
    private final JLabel mealCount = new JLabel();
    private final JComboBox<String> dateSelector = new JComboBox<>();
    private List<UserMeals> allmeals;

    public ViewMeals(JFrame frame, UserProfile profile) {
        super(frame);
        this.profile = profile;
        this.allmeals = profile.getMeals();

        setLayout(new BorderLayout());
        setBackground(Styles.background);

        add(createTopPanel(frame), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);

        updateMealList(); // initial load
        SwingUtilities.invokeLater(() -> {
            revalidate();
            repaint();
        });
    }

    private JPanel createTopPanel(JFrame frame) {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setOpaque(false);
        topPanel.add(createBackButton(new DietDashboardPanel(frame, profile)));
        return topPanel;
    }

    private JPanel createMainPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Styles.background);

        JLabel title = new JLabel("Logged Meals for " + profile.getName(), JLabel.CENTER);
        title.setFont(Styles.title_font);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        contentPanel.add(title);

        //date filter
        createDateFilter();
        contentPanel.add(dateSelector);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        //count
        mealCount.setFont(Styles.default_font);
        mealCount.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(mealCount);

        //meal panel
        mealPanel.setBackground(Styles.background);
        mealPanel.setLayout(new BoxLayout(mealPanel, BoxLayout.Y_AXIS));
        mealPanel.setOpaque(false);
        mealPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JScrollPane scrollPane = new JScrollPane(mealPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Styles.background);
        contentPanel.add(scrollPane);

        return contentPanel;
    }

    private void createDateFilter() {
        Set<String> uniqueDates = allmeals.stream()
            .map(m -> m.getDate().toString())
            .collect(Collectors.toSet());

        dateSelector.addItem("Show All");
        uniqueDates.stream().sorted().forEach(dateSelector::addItem);
        dateSelector.setMaximumSize(new Dimension(200, 25));
        dateSelector.setAlignmentX(Component.CENTER_ALIGNMENT);

        dateSelector.addActionListener(e -> updateMealList());
    }

    private void updateMealList() {
        String selectedDate = (String) dateSelector.getSelectedItem();
        mealPanel.removeAll();
        int count = 0;

        for (UserMeals meal : allmeals) {
            if (!"Show All".equals(selectedDate) && !meal.getDate().toString().equals(selectedDate)) continue;
            mealPanel.add(new MealCard(meal));
            mealPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            count++;
        }

        mealCount.setText("Total Meals: " + count);
        mealPanel.revalidate();
        mealPanel.repaint();
    }

    public List<UserMeals> getAllMeals() {
        return allmeals;
    }

    public void setAllMeals(List<UserMeals> allMeals) {
        this.allmeals = allMeals;
    }
}