package nutrisci;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import nutrisci.gui.*;

public class MainMenu extends JPanel {
    
    public MainMenu(JFrame frame) {
        setLayout(new BorderLayout());

        // === HEADER with Logo + Title ===
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 231, 216));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        // Logo (smaller)
        ImageIcon image = new ImageIcon("src/image.png");
        int original_width = image.getIconWidth();
        int original_height = image.getIconHeight();
        int max_dim = 100;
        int scaled_width, scaled_height;
        if (original_width > original_height) {
            scaled_width = max_dim;
            scaled_height = (original_height * max_dim) / original_width;
        } else {
            scaled_height = max_dim;
            scaled_width = (original_width * max_dim) / original_height;
        }
        Image scaled_image = image.getImage().getScaledInstance(scaled_width, scaled_height, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaled_image));

        // Title Text
        JLabel titleLine = new JLabel("NutriSci");
        titleLine.setFont(new Font("Georgia", Font.BOLD, 28)); // or any installed font
        titleLine.setForeground(new Color(0x564C4D));

        JLabel subtitleLine = new JLabel("SwEATch to better!");
        subtitleLine.setFont(new Font("Georgia", Font.PLAIN, 18));
        subtitleLine.setForeground(new Color(0x564C4D));
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false); // keep header background
        titlePanel.add(titleLine);
        titlePanel.add(subtitleLine);
        // Wrap both logo and text
        JPanel logoTitlePanel = new JPanel();
        logoTitlePanel.setBackground(new Color(245, 231, 216));
        logoTitlePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logoTitlePanel.add(logoLabel);
        logoTitlePanel.add(titlePanel);

        headerPanel.add(logoTitlePanel, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // === PROFILE GRID ===
        // Main center layout wrapper
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(255, 251, 245)); // match bg
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Label above buttons
        JLabel selectLabel = new JLabel("Select a Profile:");
        selectLabel.setFont(new Font("Helvetica", Font.PLAIN, 18));
        selectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        centerPanel.add(selectLabel);

        JPanel profileGrid = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        profileGrid.setBackground(new Color(255, 251, 245));
        UserProfile.seedTestProfilesIfEmpty(); // Seed first //just for testing
        List<UserProfile> profiles = UserProfile.getProfiles();
        if (profiles.isEmpty()) {
            JLabel err = new JLabel("No profiles found. Please create one.");
            err.setFont(new Font("Helvetica", Font.PLAIN, 14));
            profileGrid.add(err);
        } else {
            for (UserProfile profile : profiles) {
                JButton profileBtn = new JButton(profile.getName());
                profileBtn.setPreferredSize(new Dimension(90, 90));
                profileBtn.setFont(new Font("Helvetica", Font.PLAIN, 15));
                profileBtn.setBackground(new Color(255, 150, 120));
                profileBtn.setFocusPainted(false);
                profileBtn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));

                profileBtn.addActionListener(e -> {
                    frame.setContentPane(new UserDashboardPanel(frame, profile));
                    frame.revalidate();
                    frame.repaint();
                });

                profileGrid.add(profileBtn);
            }

        }

        centerPanel.add(profileGrid);
        add(centerPanel, BorderLayout.CENTER);

        // === CREATE NEW PROFILE BUTTON ===
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(255, 251, 245));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JButton createNewBtn = new JButton("+ Create New Profile");
        createNewBtn.setFont(new Font("Helvetica", Font.PLAIN, 16));
        createNewBtn.setBackground(new Color(255, 114, 76));
        createNewBtn.setForeground(Color.BLACK);
        createNewBtn.setFocusPainted(false);
        createNewBtn.setPreferredSize(new Dimension(200, 40));
        createNewBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        createNewBtn.addActionListener(e -> {
            frame.setContentPane(new CreateProfilePanel(frame));
            frame.revalidate();
            frame.repaint();
        });

        bottomPanel.add(createNewBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

}
