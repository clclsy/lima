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
    private JScrollPane scrollPane;

    public ViewMealsPanel(JFrame frame, UserProfile profile) {
        super(frame);
        this.profile = profile;
        init();
        refreshMeals();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBackground(Styles.background);

        // Top section with back button, title, filter row
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        // Back button
        top.add(createTopPanel(new DietDashboardPanel(frame, profile)), BorderLayout.WEST);

        // Title
        JLabel title = new JLabel("Logged Meals for " + profile.getName());
        title.setFont(Styles.dtitle_font);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(60, 0, 10, 0));
        top.add(title, BorderLayout.CENTER);

        // Filter row
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        filterRow.setOpaque(false);
        filterRow.add(datePicker);

        JButton filterBtn = new JButton("Apply Filter");
        filterBtn.setFont(Styles.default_font);
        filterBtn.setBackground(Styles.lightorange);
        filterBtn.setFocusPainted(false);
        filterBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        filterBtn.addActionListener(e -> refreshMeals());
        filterRow.add(filterBtn);

        top.add(filterRow, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        // Main content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Styles.background);

        // Meal count
        countLabel.setFont(Styles.default_font);
        countLabel.setHorizontalAlignment(SwingConstants.CENTER);
        countLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        contentPanel.add(countLabel, BorderLayout.NORTH);

        // Meal container
        mealContainer.setBackground(Styles.background);
        mealContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20) {
            @Override
            public Dimension preferredLayoutSize(Container target) {
                synchronized (target.getTreeLock()) {
                    Insets insets = target.getInsets();
                    int maxWidth = target.getSize().width - insets.left - insets.right;

                    if (maxWidth <= 0) {
                        maxWidth = Integer.MAX_VALUE;
                    }

                    int width = 0;
                    int height = insets.top + insets.bottom;
                    int rowWidth = insets.left;
                    int rowHeight = 0;

                    for (Component comp : target.getComponents()) {
                        if (comp.isVisible()) {
                            Dimension d = comp.getPreferredSize();
                            if (rowWidth + d.width + getHgap() > maxWidth) {
                                width = Math.max(width, rowWidth);
                                height += rowHeight + getVgap();
                                rowWidth = insets.left;
                                rowHeight = 0;
                            }
                            if (rowWidth > insets.left) {
                                rowWidth += getHgap();
                            }
                            rowWidth += d.width;
                            rowHeight = Math.max(rowHeight, d.height);
                        }
                    }

                    // Add last row
                    height += rowHeight;
                    width = Math.max(width, rowWidth) + insets.right;

                    return new Dimension(width, height);
                }
            }
        });
        mealContainer.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // Scroll pane for meal cards
        scrollPane = new JScrollPane(mealContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Styles.background);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Set scroll speed
        scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // No meals message
        noMealMsg.setFont(Styles.ismall_font);
        noMealMsg.setForeground(Color.GRAY);
        noMealMsg.setHorizontalAlignment(SwingConstants.CENTER);
        noMealMsg.setVisible(false);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Styles.background);
        centerPanel.add(noMealMsg, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(centerPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
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
                MealCard card = new MealCard(meal);
                mealContainer.add(card);
            }
        }

        mealContainer.revalidate();
        mealContainer.repaint();

        // Adjust scroll pane size to fit content
        SwingUtilities.invokeLater(() -> {
            scrollPane.getViewport().setViewSize(
                    new Dimension(
                            mealContainer.getPreferredSize().width,
                            mealContainer.getPreferredSize().height + 20));
            scrollPane.revalidate();
        });
    }

    @Override
    public Dimension getPreferredSize() {
        // Calculate preferred size based on content
        int cardWidth = 250;
        int cardHeight = 200;
        int hgap = 20;
        int vgap = 20;

        // Calculate available width based on parent container
        int availableWidth = getParent() != null ? getParent().getWidth() - 40 : Toolkit.getDefaultToolkit().getScreenSize().width;

        // Calculate number of cards per row
        int cardsPerRow = Math.max(1, (availableWidth - hgap) / (cardWidth + hgap));
        int rows = (int) Math.ceil((double) mealContainer.getComponentCount() / cardsPerRow);
        int totalHeight = rows * (cardHeight + vgap) + 100;

        // Ensure minimum height
        return new Dimension(
                super.getPreferredSize().width,
                Math.min(800, totalHeight) 
        );
    }
}