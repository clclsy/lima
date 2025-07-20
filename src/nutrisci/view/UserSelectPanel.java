package nutrisci.view;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import nutrisci.db.UserProfileDAO;
import nutrisci.model.UserProfile;
import nutrisci.template.Styles;

public class UserSelectPanel extends JPanel {
    private final JFrame frame;

    public UserSelectPanel(JFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        add(top_panel(), BorderLayout.NORTH);
        add(middle_panel(), BorderLayout.CENTER);
        add(bottom_panel(), BorderLayout.SOUTH);
    }

    private JPanel top_panel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Styles.darkbackground);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        ImageIcon image = new ImageIcon("src/image.png");
        int max_dim = 100;
        int w = image.getIconWidth(), h = image.getIconHeight();
        int sw = (w > h) ? max_dim : (w * max_dim) / h;
        int sh = (h > w) ? max_dim : (h * max_dim) / w;
        Image scaled = image.getImage().getScaledInstance(sw, sh, Image.SCALE_SMOOTH);

        JLabel logo = new JLabel(new ImageIcon(scaled));
        JLabel title = new JLabel("NutriSci");
        title.setFont(Styles.nutrisci_font);

        JLabel subtitle = new JLabel("SwEATch to better!");
        subtitle.setFont(Styles.subnutri_font);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(subtitle);

        JPanel logoTitle = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logoTitle.setBackground(Styles.darkbackground);
        logoTitle.add(logo);
        logoTitle.add(titlePanel);

        headerPanel.add(logoTitle, BorderLayout.WEST);
        return headerPanel;
    }

    private JPanel middle_panel() {
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(Styles.background);
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel("Select a Profile:");
        label.setFont(Styles.title_font);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        center.add(label);

        JPanel grid = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        grid.setBackground(Styles.background);

        List<UserProfile> profiles = UserProfileDAO.getAllProfiles();
        // for tests
        System.out.println("Profiles in DB: " + profiles.size());
        for (UserProfile p : profiles) {
            System.out.println("- " + p.getName());
        }

        /**
         * if user_profiles table empty, display msg. if not empty, display all profiles
         */
        if (profiles.isEmpty()) {
            JLabel msg = new JLabel("No profiles found. Please create one.");
            msg.setFont(Styles.small_font);
            grid.add(msg);
        } else {
            for (UserProfile p : profiles) {
                UserProfile profileCopy = p; // capture the current profile safely

                JButton btn = new JButton(profileCopy.getName());
                btn.setPreferredSize(new Dimension(90, 90));
                btn.setFont(Styles.default_font);
                btn.setBackground(Styles.lightorange);
                btn.setFocusPainted(false);
                btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                btn.addActionListener(e -> {
                    System.out.println("✅ Selected profile: " + profileCopy.getName());
                    frame.setContentPane(new UserDashboardPanel(frame, profileCopy));
                    frame.invalidate();
                    frame.revalidate();
                    frame.repaint();
                });

                grid.add(btn);
            }
        }

        center.add(grid);
        return center;
    }

    private JPanel bottom_panel() {
        JPanel bottom = new JPanel();
        bottom.setBackground(Styles.background);
        bottom.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JButton createbtn = new JButton("+ Create New Profile");
        createbtn.setFont(Styles.bdefault_font);
        createbtn.setBackground(Styles.darkorange);
        createbtn.setFocusPainted(false);
        createbtn.setPreferredSize(new Dimension(200, 40));
        createbtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        createbtn.addActionListener(e -> {
            frame.setContentPane(new CreateProfilePanel(frame));
            frame.revalidate();
            frame.repaint();
        });

        bottom.add(createbtn);
        return bottom;
    }
}