package nutrisci.view;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import nutrisci.db.UserProfileDAO;
import nutrisci.model.UserProfile;
import nutrisci.template.Base;
import nutrisci.template.Styles;

public class UserDashboardPanel extends Base {

    public UserDashboardPanel(JFrame frame, UserProfile profile) {
        super(frame);
        init(frame, profile);
    }

    private void init(JFrame frame, UserProfile profile) {
        setLayout(new BorderLayout());
        setBackground(Styles.background);

        // Icons
        Image bin_image = new ImageIcon("src/recycle-bin.png").getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        Image edit_image = new ImageIcon("src/edit.png").getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        Image dish_image = new ImageIcon("src/dish.png").getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        Image ng_image = new ImageIcon("src/goal.png").getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        Image log_image = new ImageIcon("src/notes.png").getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        Image vm_image = new ImageIcon("src/menu.png").getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        Image chart_image = new ImageIcon("src/bar-chart.png").getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH); //// need image icon
        Image comp_image= new ImageIcon("src/ab-testing.png").getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH); //// need image icon
        ImageIcon binIcon = new ImageIcon(bin_image);
        ImageIcon editIcon = new ImageIcon(edit_image);
        ImageIcon dishIcon = new ImageIcon(dish_image);
        ImageIcon ngIcon = new ImageIcon(ng_image);
        ImageIcon logIcon = new ImageIcon(log_image);
        ImageIcon vmIcon = new ImageIcon(vm_image);
        ImageIcon viz_chartIcon = new ImageIcon(chart_image);
        ImageIcon compIcon = new ImageIcon(comp_image); //// need image icon
        // Top panel with back button and greeting centered
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Styles.background);

        // Back button on the left
        top.add(createTopPanel(new UserSelectPanel(frame)), BorderLayout.WEST);

        // Centered greeting
        JLabel greeting = new JLabel("Hello, " + profile.getName() + "!");
        greeting.setFont(Styles.dtitle_font);
        greeting.setHorizontalAlignment(SwingConstants.CENTER);
        greeting.setBorder(BorderFactory.createEmptyBorder(60, 0, 20, 0));

        top.add(greeting, BorderLayout.CENTER);

        // Add to main panel
        add(top, BorderLayout.NORTH);

        // Center Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(Styles.background);

        JButton editBtn = createSquareButton("Edit Profile", editIcon, new Color(255, 234, 200));
        JButton dietBtn = createSquareButton("View Diet", dishIcon, new Color(200, 255, 200));
        JButton deleteBtn = createSquareButton("Delete Profile", binIcon, new Color(255, 210, 210));
        JButton nutrigoalBtn = createSquareButton("Nutritional goal", ngIcon, new Color(210, 255, 255));
        JButton logmealBtn = createSquareButton("Log Meal", logIcon, new Color(250, 220, 220));
        JButton viewmealBtn = createSquareButton("View Meals", vmIcon, new Color(220, 255, 220));
        JButton visualizeBtn = createSquareButton("Visualize Swaps", viz_chartIcon, new Color(220, 220, 255)); //// need viz.chart icon
        JButton compBtn = createSquareButton("Compare Nutrients to RDV", compIcon, new Color(222,210,250)); //// need compare icon
        Image swap_image = createSwapIcon();
        ImageIcon swapIcon = new ImageIcon(swap_image);
        JButton applySwapBtn = createSquareButton("Apply Swap", swapIcon, new Color(220, 255, 255));


        // Button Actions
        editBtn.addActionListener(e -> {
            frame.setContentPane(new EditProfilePanel(frame, profile));
            frame.revalidate();
            frame.repaint();
        });

        dietBtn.addActionListener(e -> {
            frame.setContentPane(new DietDashboardPanel(frame, profile));
            frame.revalidate();
            frame.repaint();
        });

        deleteBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to delete this profile?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                if (UserProfileDAO.deleteProfile(profile)) {
                    JOptionPane.showMessageDialog(frame, "Profile deleted.");
                    frame.setContentPane(new UserSelectPanel(frame));
                    frame.revalidate();
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to delete profile.");
                }
            }
        });

        nutrigoalBtn.addActionListener(e -> {
            frame.setContentPane(new SwapSuggestionPanel(frame, profile));
            frame.revalidate();
            frame.repaint();
        });

        logmealBtn.addActionListener(e -> {
            frame.setContentPane(new LogMealPanel(frame, profile));
            frame.revalidate();
            frame.repaint();
        });

        viewmealBtn.addActionListener(e -> {
            frame.setContentPane(new ViewMealsPanel(frame, profile));
            frame.revalidate();
            frame.repaint();
        });
        

        compBtn.addActionListener(e -> {
            frame.setContentPane(new DailyIntakeBarChartPanel(profile));
            frame.revalidate();
            frame.repaint();
        });

        visualizeBtn.addActionListener(e -> { //// 
        frame.setContentPane(new VisualizeSwapPanel(frame, profile)); //// 
        frame.revalidate(); //// 
        frame.repaint(); ////
        });

        applySwapBtn.addActionListener(e -> {
            frame.setContentPane(new ApplySwapPanel(frame, profile));
            frame.revalidate();
            frame.repaint();
        });

        buttonPanel.add(editBtn);
        buttonPanel.add(logmealBtn);
        buttonPanel.add(dietBtn);
        buttonPanel.add(nutrigoalBtn);
        buttonPanel.add(compBtn);
        buttonPanel.add(viewmealBtn);
        buttonPanel.add(visualizeBtn);
        buttonPanel.add(applySwapBtn);
        buttonPanel.add(deleteBtn); 

        add(buttonPanel, BorderLayout.CENTER);
    }

    private JButton createSquareButton(String text, ImageIcon icon, Color bg) {
        JButton btn = new JButton(text, icon);
        btn.setFont(Styles.default_font);
        btn.setPreferredSize(new Dimension(100, 100)); // Square
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private Image createSwapIcon() {
        int s = 32;
        BufferedImage img = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(52, 73, 94));
        int[] x1 = {6, 22, 22, 28, 22, 22};
        int[] y1 = {10, 10, 4, 16, 28, 22};
        g.fillPolygon(x1, y1, x1.length);
        int[] x2 = {26, 10, 10, 4, 10, 10};
        int[] y2 = {22, 22, 28, 16, 4, 10};
        g.fillPolygon(x2, y2, x2.length);
        g.dispose();
        return img;
    }
}
