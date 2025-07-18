package nutrisci.view;

import javax.swing.*;
import nutrisci.model.UserProfile;
import nutrisci.template.*;

public class UserDashboardPanel extends Base {
    public UserDashboardPanel(JFrame frame, UserProfile profile) {
        super(frame);
/**
        JLabel welcome = new JLabel("Welcome, " + profile.getName() + "!");
        welcome.setFont(Styles.title_font);
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton logMealBtn = createMenuButton("🍽 Log Meal", new Color(200, 255, 200));
        JButton swapBtn = createMenuButton("🔄 Request Food Swap", new Color(255, 230, 180));
        JButton backBtn = createMenuButton("⬅ Back to Profile Select", new Color(255, 204, 204));

        logMealBtn.addActionListener(e -> frame.setContentPane(new LogMealPanel(frame)));
        swapBtn.addActionListener(e -> frame.setContentPane(new FoodSwapPanel(frame)));
        backBtn.addActionListener(e -> frame.setContentPane(new UserSelectPanel(frame)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(Styles.background);
        buttonPanel.add(logMealBtn);
        buttonPanel.add(swapBtn);
        buttonPanel.add(backBtn);

        add(createTopPanel(null), BorderLayout.NORTH);
        add(createCenterPanel(welcome, Box.createRigidArea(new Dimension(0, 20)), buttonPanel), BorderLayout.CENTER);

       
    }

    private JButton createMenuButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(Styles.default_font);
        btn.setPreferredSize(new Dimension(180, 80));
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
         */
    }
}
