package nutrisci;

import java.awt.*;
import javax.swing.*;
import nutrisci.template.Styles;
import nutrisci.view.UserSelectPanel;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ImageIcon image = new ImageIcon("src/image.png");
            JFrame frame = new JFrame();
            frame.setTitle("NutriSci: SwEATch to better!");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 800);
            frame.setIconImage(image.getImage());
            frame.getContentPane().setBackground(Styles.background);
            frame.setLayout(new BorderLayout());
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new UserSelectPanel(frame));
            frame.setVisible(true);
        });
    }
    
}
