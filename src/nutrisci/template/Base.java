package nutrisci.template;

import java.awt.*;
import javax.swing.*;

public abstract class Base extends JPanel {
    protected JFrame frame;

    public Base(JFrame frame) {
        this.frame = frame;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Styles.background);
    }

    public JPanel createTopPanel(JPanel destination) {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setOpaque(false);
        topPanel.add(createBackButton(destination));
        return topPanel;
    }

    public JPanel createCenterPanel(Component... components) {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(255, 251, 245));
        for (Component comp : components) {
            centerPanel.add(comp);
        }
        return centerPanel;
    }

    protected JButton createBackButton(JPanel destination) {
        JButton backButton = new JButton("←");
        backButton.setFont(new Font("Helvetica", Font.BOLD, 20)); // Adjust to match other icons
        backButton.setForeground(new Color(0x564C4D));
        backButton.setBackground(new Color(0, 0, 0, 0)); // Transparent
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        backButton.setMargin(new Insets(0, 0, 0, 0));

        backButton.addActionListener(e -> {
            frame.setContentPane(destination);
            frame.revalidate();
        });

        return backButton;
    }

    protected JPanel formRow(String labelText, JComponent inputField) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER));
        row.setOpaque(false);
        JLabel label = new JLabel(labelText);
        row.add(label);
        row.add(inputField);
        return row;
    }

    protected Component centered(JComponent component) {
        component.setAlignmentX(Component.CENTER_ALIGNMENT);
        return component;
    }
}