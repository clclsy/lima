package nutrisci.view;

import java.awt.*;
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
        Image ng_image = new ImageIcon("src/health.png").getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        Image chart_image = new ImageIcon("src/viz_chart.png").getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH); //// need image icon
        
        ImageIcon binIcon = new ImageIcon(bin_image);
        ImageIcon editIcon = new ImageIcon(edit_image);
        ImageIcon dishIcon = new ImageIcon(dish_image);
        ImageIcon ngIcon = new ImageIcon(ng_image);
        ImageIcon viz_chartIcon = new ImageIcon(chart_image); //// 
        
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
        JButton visualizeBtn = createSquareButton("Visualize Swaps", chartIcon, new Color(220, 220, 255)); //// need viz.chart icon


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
            frame.setContentPane(new NutritionalGoalPanel(frame, profile));
            frame.revalidate();
            frame.repaint();
        });

        visualizeBtn.addActionListener(e -> { //// 
        frame.setContentPane(new VisualizeSwapPanel(frame, profile)); //// 
        frame.revalidate(); //// 
        frame.repaint(); //// 
        });

        buttonPanel.add(editBtn);
        buttonPanel.add(dietBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(visualizeBtn); ////
        
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
}
