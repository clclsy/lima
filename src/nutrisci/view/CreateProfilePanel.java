package nutrisci.view;

import java.awt.*;
import javax.swing.*;
import nutrisci.MainApp;
import nutrisci.controller.ProfileController;
import nutrisci.model.UserProfile;

public class CreateProfilePanel extends JPanel {
    public CreateProfilePanel(JFrame frame) {
        setLayout(new GridLayout(8, 2));

        JTextField nameField = new JTextField();
        JTextField dobField = new JTextField();
        JTextField genderField = new JTextField();
        JTextField heightField = new JTextField();
        JTextField weightField = new JTextField();
        JComboBox<String> unitBox = new JComboBox<>(new String[] { "Metric", "Imperial" });

        JButton createBtn = new JButton("Create Profile");
        JLabel statusLabel = new JLabel("");

        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("Date of Birth (yyyy-mm-dd):"));
        add(dobField);
        add(new JLabel("Gender:"));
        add(genderField);
        add(new JLabel("Height:"));
        add(heightField);
        add(new JLabel("Weight:"));
        add(weightField);
        add(new JLabel("Unit:"));
        add(unitBox);
        add(createBtn);
        add(statusLabel);

        createBtn.addActionListener(e -> {
            try {
                UserProfile profile = new UserProfile(
                        nameField.getText(),
                        dobField.getText(),
                        genderField.getText(),
                        Double.parseDouble(heightField.getText()),
                        Double.parseDouble(weightField.getText()),
                        (String) unitBox.getSelectedItem());
                ProfileController.saveProfile(profile);
                statusLabel.setText("Profile created!");
            } catch (NumberFormatException ex) {
                statusLabel.setText("Error: " + ex.getMessage());
            }
        });
        JButton backBtn = new JButton("Back to Menu");
        backBtn.addActionListener(e -> MainApp.showMainMenu(frame));
        add(backBtn);

    }
}
