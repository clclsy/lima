package nutrisci.gui;

import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.*;
import nutrisci.template.*;

public class ViewMeals extends Base {

    public ViewMeals(JFrame frame, UserProfile profile) {
        super(frame);
        setLayout(new BorderLayout());
        setBackground(Styles.background);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setOpaque(false);
        topPanel.add(createBackButton(new DietDashboardPanel(frame, profile)));
        add(topPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Styles.background);

        // title
        JLabel title = new JLabel("Logged Meals for " + profile.getName(), JLabel.CENTER);
        title.setFont(Styles.title_font);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        contentPanel.add(title);

        //filter by date
        List<UserProfile.Meal> allmeals = profile.getMeals();
        Set<String> dateexist = allmeals.stream().map(UserProfile.Meal::getDate).collect(Collectors.toSet());
        JComboBox<String> date = new JComboBox<>();
        date.addItem("Show All");
        dateexist.stream().sorted().forEach(date::addItem);
        date.setMaximumSize(new Dimension(200, 25));
        date.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(date);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        //number of meals
        JLabel no = new JLabel();
        no.setFont(Styles.default_font);
        no.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(no);

        // meals list
        JPanel meal_panel = new JPanel();
        meal_panel.setBackground(Styles.background);
        meal_panel.setLayout(new BoxLayout(meal_panel, BoxLayout.Y_AXIS));
        meal_panel.setOpaque(false);
        meal_panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JScrollPane scroll_pane = new JScrollPane(meal_panel);
        scroll_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll_pane.setBorder(BorderFactory.createEmptyBorder());
        scroll_pane.getViewport().setBackground(Styles.background);
        contentPanel.add(scroll_pane);

        Runnable update_meallist = () -> {
            String selectedDate = (String) date.getSelectedItem();
            meal_panel.removeAll();
            int count = 0;
            for (UserProfile.Meal meal : allmeals) {
                if (!"Show All".equals(selectedDate) && !meal.getDate().equals(selectedDate)) continue;

                JPanel card = new JPanel();
                card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                    BorderFactory.createEmptyBorder(12, 14, 12, 14)
                ));
                card.setMaximumSize(new Dimension(500, Integer.MAX_VALUE));
                card.setAlignmentX(Component.CENTER_ALIGNMENT);

                JLabel header = new JLabel(meal.getDate() + "    |    " + meal.getType());
                header.setFont(Styles.default_font);
                header.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

                JTextArea igdlist = new JTextArea("Ingredients:\n - " + String.join("\n - ", meal.getIngredients()));
                igdlist.setFont(Styles.small_font);
                igdlist.setEditable(false);
                igdlist.setOpaque(false);
                igdlist.setBorder(null);

                card.add(header);
                card.add(igdlist);
                meal_panel.add(card);
                meal_panel.add(Box.createRigidArea(new Dimension(0, 10)));
                count++;
            }
            no.setText("Total Meals: " + count);
            meal_panel.revalidate();
            meal_panel.repaint();
        };

        //initial run
        update_meallist.run();

        //different day
        date.addActionListener(e -> update_meallist.run());

        add(contentPanel, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            revalidate();
            repaint();
        });

    }

}
