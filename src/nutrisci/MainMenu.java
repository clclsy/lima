package nutrisci;

import java.awt.*;
import javax.swing.*;
import nutrisci.gui.*;

public class MainMenu extends JPanel {
    public MainMenu(JFrame frame) {
        setLayout(new GridBagLayout());
        setBackground(new Color(255, 251, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        JButton profileBtn = new JButton("👤 Profile");
        profileBtn.setFont(new Font("Arial", Font.BOLD, 24));
        profileBtn.setPreferredSize(new Dimension(200, 80));
        profileBtn.setBackground(new Color(255, 204, 153));

        JButton dietBtn = new JButton("🍽️ Diet");
        dietBtn.setFont(new Font("Arial", Font.BOLD, 24));
        dietBtn.setPreferredSize(new Dimension(200, 80));
        dietBtn.setBackground(new Color(204, 229, 255));

        // Add buttons to panel
        gbc.gridx = 0;
        add(profileBtn, gbc);
        gbc.gridx = 1;
        add(dietBtn, gbc);

        // On button click: swap screen
        profileBtn.addActionListener(e -> {
            frame.getContentPane().removeAll();
            frame.add(new ProfileCreation(), BorderLayout.CENTER);
            frame.revalidate();
            frame.repaint();
        });

        dietBtn.addActionListener(e -> {
            frame.getContentPane().removeAll();
            frame.add(new MealLogging(), BorderLayout.CENTER);
            frame.revalidate();
            frame.repaint();
        });
    }
}
