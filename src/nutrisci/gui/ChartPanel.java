package nutrisci.gui;

import java.awt.*;
import javax.swing.*;

public class ChartPanel {

    public static JPanel createPlaceholder(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(250, 200));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Helvetica", Font.BOLD, 14));

        JLabel placeholder = new JLabel("[ Chart Placeholder ]", JLabel.CENTER);
        placeholder.setFont(new Font("Helvetica", Font.ITALIC, 12));
        placeholder.setForeground(Color.GRAY);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(placeholder, BorderLayout.CENTER);
        return panel;
    }

}
