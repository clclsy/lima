package nutrisci;

import java.awt.*;
import javax.swing.*;
import nutrisci.view.*;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("NutriSci - Main Menu");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(700, 500);
            frame.setLocationRelativeTo(null);

            showMainMenu(frame);

            frame.setVisible(true);
        });
    }

    public static void showMainMenu(JFrame frame) {
        JPanel mainMenu = new JPanel(new GridLayout(3, 1));
        JButton profileBtn = new JButton("Create Profile");
        JButton logMealBtn = new JButton("Log Meal");
        JButton swapBtn = new JButton("Request Food Swap");

        profileBtn.addActionListener(e -> switchPanel(frame, new CreateProfilePanel(frame)));
        logMealBtn.addActionListener(e -> switchPanel(frame, new LogMealPanel(frame)));
        swapBtn.addActionListener(e -> switchPanel(frame, new FoodSwapPanel(frame)));

        mainMenu.add(profileBtn);
        mainMenu.add(logMealBtn);
        mainMenu.add(swapBtn);
        
        frame.setContentPane(mainMenu);
        frame.revalidate();
        frame.repaint();
    }

    public static void switchPanel(JFrame frame, JPanel panel) {
        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();
    }
}
