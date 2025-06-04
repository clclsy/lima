package nutrisci;

import java.awt.*;
import javax.swing.*;

public class Layout {

    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(() -> {
                        
            ImageIcon image = new ImageIcon("src/image.png"); //create an ImageIcon
            //change image size
            int original_width = image.getIconWidth();
            int original_height = image.getIconHeight();
            int max_dim = 250;
            int scaled_width, scaled_height;
            if (original_width > original_height) {
                scaled_width = max_dim;
                scaled_height = (original_height * max_dim) / original_width;
            } else {
                scaled_height = max_dim;
                scaled_width = (original_width * max_dim) / original_height;
            }
            Image scaled_image = image.getImage().getScaledInstance(scaled_width, scaled_height, Image.SCALE_SMOOTH);
            ImageIcon imageresized = new ImageIcon(scaled_image);
            
            //New Frame
            JFrame frame = new JFrame(); //creates frame
            frame.setTitle("NutriSci: SwEATch to better!"); //set title
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //exit out of app when x is pressed rather than closed
            frame.setSize(800, 800); //set frame x-dimension and y-dimension
            frame.setIconImage(image.getImage()); //change icon of frame
            frame.getContentPane().setBackground(new Color(	255, 251, 245)); // change beackground colour
            frame.setLayout(new BorderLayout()); //allows components in frame to be arranged
            
            //JLabel -> a GUI display area for a string of text, an image or both
            //Welcome page
            JLabel label = new JLabel(); //creates a label
            label.setText("<html><div style='text-align:center;'>" +
              "<span style='font-size:40px; font-weight:bold;'>NutriSci</span><br>" +
              "<span style='font-size:20px; font-family:Arial Rounded;'>SwEATch to better!</span>" +
              "</div></html>");
            label.setIcon(imageresized);
            label.setHorizontalTextPosition(JLabel.CENTER); //set text CENTER of imageicon
            label.setVerticalTextPosition(JLabel.BOTTOM); //set text TOP of imageicon
            label.setFont(new Font("Calibri", Font.BOLD,24 )); //set font of text
            label.setForeground(new Color(0x564C4D)); //change font color
            label.setVerticalAlignment(JLabel.CENTER); //set vertical position of text and icon in label
            label.setHorizontalAlignment(JLabel.CENTER); //set horizontal position of text and icon in label
            label.setAlignmentX(Component.CENTER_ALIGNMENT);

            //Start button
            JButton start_button = new JButton("START!"); //creates a button
            start_button.setFont(new Font("Calibri", Font.PLAIN, 16)); //set font for button
            start_button.setBackground(new Color(0xff7f50)); //set color of button
            start_button.setAlignmentX(Component.CENTER_ALIGNMENT);
            start_button.setBackground(new Color(255, 114, 76)); // soft orange
            start_button.setForeground(Color.WHITE);             // white text
            start_button.setFocusPainted(false);                 // remove border glow
            start_button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            start_button.setCursor(new Cursor(Cursor.HAND_CURSOR));


            //Layout for welcome page
            JPanel welcome_panel = new JPanel(); //creating panel
            welcome_panel.setLayout(new BoxLayout(welcome_panel, BoxLayout.Y_AXIS));
            welcome_panel.add(Box.createVerticalGlue());
            welcome_panel.add(label); //add label
            welcome_panel.add(Box.createRigidArea(new Dimension(0, 20))); //add spacing between label and button
            welcome_panel.add(start_button); //add button
            welcome_panel.add(Box.createVerticalGlue());

            frame.add(welcome_panel, BorderLayout.CENTER); //set welcome panel at center of frame

            // On click: swap to tabs
            start_button.addActionListener(e -> {
                frame.getContentPane().removeAll(); // remove welcome
                frame.add(new MainMenu(frame), BorderLayout.CENTER); // add tabs
                frame.revalidate(); // refresh layout
                frame.repaint();
            });
            
            frame.setVisible(true); //make frame visible 

        });
    }

}