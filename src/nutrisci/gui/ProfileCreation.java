package nutrisci.gui;

import java.awt.*;
import javax.swing.*;

public class ProfileCreation extends JPanel{
    
    private final JTextField nameField, dobField, heightField, weightField;
    private final JComboBox<String> sexBox, unitBox;

    public ProfileCreation() {
        setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Create Your Profile"));

        // Create fields
        nameField = new JTextField();
        dobField = new JTextField();
        heightField = new JTextField();
        weightField = new JTextField();
        sexBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        unitBox = new JComboBox<>(new String[]{"Metric", "Imperial"});

        // Add components to panel
        formPanel.add(new JLabel("Name:")); formPanel.add(nameField);
        formPanel.add(new JLabel("Sex:")); formPanel.add(sexBox);
        formPanel.add(new JLabel("Date of Birth (YYYY-MM-DD):")); formPanel.add(dobField);
        formPanel.add(new JLabel("Height:")); formPanel.add(heightField);
        formPanel.add(new JLabel("Weight:")); formPanel.add(weightField);
        formPanel.add(new JLabel("Units:")); formPanel.add(unitBox);

        // Add to main layout
        add(formPanel, BorderLayout.CENTER);
        JButton saveBtn = new JButton("Save Profile");
        add(saveBtn, BorderLayout.SOUTH);
    }
}
