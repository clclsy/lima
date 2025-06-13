package nutrisci;

import java.awt.*;
import javax.swing.*;
import nutrisci.gui.UserProfile;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        UserProfile.loadProfilesFromFile();
            ImageIcon image = new ImageIcon("src/image.png"); //create an ImageIcon
            JFrame frame = new JFrame(); //creates frame
            frame.setTitle("NutriSci: SwEATch to better!"); //set title
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //exit out of app when x is pressed rather than closed
            frame.setSize(900, 800); //set frame x-dimension and y-dimension
            frame.setIconImage(image.getImage()); //change icon of frame
            frame.getContentPane().setBackground(new Color(	255, 251, 245)); // change beackground colour
            frame.setLayout(new BorderLayout()); //allows components in frame to be arranged
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new MainMenu(frame));
            frame.setVisible(true);
        });
    }
}
