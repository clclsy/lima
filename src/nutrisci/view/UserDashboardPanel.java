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
        System.out.println("UserDashboardPanel constructor called for profile: " + profile.getName());
        init(frame, profile);
        System.out.println("UserDashboardPanel init completed.");
    }

    private void init(JFrame frame, UserProfile profile) {
        setLayout(new BorderLayout());
        setBackground(Styles.background);

        // Icons
        Image bin_image = new ImageIcon("src/recycle-bin.png").getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        Image edit_image = new ImageIcon("src/edit.png").getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        Image dish_image = new ImageIcon("src/dish.png").getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);

        ImageIcon binIcon = new ImageIcon(bin_image);
        ImageIcon editIcon = new ImageIcon(edit_image);
        ImageIcon dishIcon = new ImageIcon(dish_image);

        // Top Panel: Back button
        add(createTopPanel(new UserSelectPanel(frame)), BorderLayout.NORTH);

        // Greeting
        JLabel greeting = new JLabel("Hello, " + profile.getName() + "!");
        greeting.setFont(Styles.title_font);
        greeting.setHorizontalAlignment(SwingConstants.CENTER);
        greeting.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // Center Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(Styles.background);

        JButton editBtn = createSquareButton("Edit Profile", editIcon, new Color(255, 234, 200));
        JButton dietBtn = createSquareButton("View Diet", dishIcon, new Color(200, 255, 200));
        JButton deleteBtn = createSquareButton("Delete Profile", binIcon, new Color(255, 210, 210));

        dietBtn.setIcon(dishIcon);
        editBtn.setIcon(editIcon);
        deleteBtn.setIcon(binIcon);

        // Button Actions
        editBtn.addActionListener(e -> {
            frame.setContentPane(new EditProfilePanel(frame, profile));
            frame.revalidate();
        });

        dietBtn.addActionListener(e -> {
            // frame.setContentPane(new DietDashboardPanel(frame, profile));
            // frame.revalidate();
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

        buttonPanel.add(editBtn);
        buttonPanel.add(dietBtn);
        buttonPanel.add(deleteBtn);

        add(createCenterPanel(greeting, buttonPanel), BorderLayout.CENTER);
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
