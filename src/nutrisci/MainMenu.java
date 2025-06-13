package nutrisci;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import nutrisci.gui.*;
import nutrisci.template.Styles;

public class MainMenu extends JPanel {
    private final JFrame frame;

    public MainMenu(JFrame frame) {
        this.frame=frame;
        setLayout(new BorderLayout());
        add(top_panel(), BorderLayout.NORTH);
        add(middle_panel(), BorderLayout.CENTER);
        add(bottom_panel(), BorderLayout.SOUTH);
    }

    private JPanel top_panel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 231, 216));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        ImageIcon image = new ImageIcon("src/image.png");
        int max_dim = 100;
        int w = image.getIconWidth(), h = image.getIconHeight();
        int sw = (w > h) ? max_dim : (w * max_dim) / h;
        int sh = (h > w) ? max_dim : (h * max_dim) / w;
        Image scaled = image.getImage().getScaledInstance(sw, sh, Image.SCALE_SMOOTH);

        JLabel logo = new JLabel(new ImageIcon(scaled));
        JLabel title = new JLabel("NutriSci");
        title.setFont(new Font("Georgia", Font.BOLD, 30));
        title.setForeground(new Color(0x564C4D));

        JLabel subtitle = new JLabel("SwEATch to better!");
        subtitle.setFont(new Font("Georgia", Font.PLAIN, 22));
        subtitle.setForeground(new Color(0x564C4D));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(subtitle);

        JPanel logoTitle = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logoTitle.setBackground(new Color(245, 231, 216));
        logoTitle.add(logo);
        logoTitle.add(titlePanel);

        headerPanel.add(logoTitle, BorderLayout.WEST);
        return headerPanel;
    }

    private JPanel middle_panel() {
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(new Color(255, 251, 245));
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel("Select a Profile:");
        label.setFont(Styles.title_font.deriveFont(Font.PLAIN));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        center.add(label);

        JPanel grid = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        grid.setBackground(new Color(255, 251, 245));

        UserProfile.seedTestProfilesIfEmpty();
        List<UserProfile> profiles = UserProfile.getProfiles();

        if (profiles.isEmpty()) {
            JLabel msg = new JLabel("No profiles found. Please create one.");
            msg.setFont(Styles.small_font);
            grid.add(msg);
        } else {
            for (UserProfile p : profiles) {
                JButton btn = new JButton(p.getName());
                btn.setPreferredSize(new Dimension(90, 90));
                btn.setFont(Styles.default_font);
                btn.setBackground(new Color(255, 150, 120));
                btn.setFocusPainted(false);
                btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                btn.addActionListener(e -> {
                    frame.setContentPane(new UserDashboardPanel(frame, p));
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
        bottom.setBackground(new Color(255, 251, 245));
        bottom.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JButton createBtn = new JButton("+ Create New Profile");
        createBtn.setFont(Styles.default_font.deriveFont(Font.BOLD));
        createBtn.setBackground(new Color(255, 114, 76));
        createBtn.setForeground(Color.BLACK);
        createBtn.setFocusPainted(false);
        createBtn.setPreferredSize(new Dimension(200, 40));
        createBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        createBtn.addActionListener(e -> {
            frame.setContentPane(new CreateProfilePanel(frame));
            frame.revalidate();
            frame.repaint();
        });

        bottom.add(createBtn);
        return bottom;
    }

}
