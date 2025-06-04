package nutrisci.gui;

import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.util.Objects;
import javax.swing.*;
import nutrisci.MainMenu;

public class CreateProfilePanel extends JPanel {
    public CreateProfilePanel(JFrame frame) {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 251, 245)); // soft cream background

        // === TOP-RIGHT FLOATING BUTTON ===
        JPanel top_panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        top_panel.setOpaque(false);

        JButton backIcon = new JButton("←");
        backIcon.setFont(new Font("Helvetica", Font.BOLD, 25));
        backIcon.setForeground(new Color(0x564C4D));
        backIcon.setFocusPainted(false);
        backIcon.setContentAreaFilled(false);
        backIcon.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        backIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backIcon.addActionListener(e -> {
            frame.setContentPane(new MainMenu(frame));
            frame.revalidate();
        });

        top_panel.add(backIcon);
        add(top_panel, BorderLayout.NORTH);

        // === FORM PANEL ===
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setOpaque(true);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 2, true), // rounded
                BorderFactory.createEmptyBorder(30, 40, 30, 40)));
        formPanel.setPreferredSize(new Dimension(450, 500));
        formPanel.setMaximumSize(new Dimension(600, 650));
        formPanel.setMinimumSize(new Dimension(350, 400));

        Font defaultFont = new Font("Helvetica", Font.PLAIN, 14);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Title inside form
        JLabel formTitle = new JLabel("Create New Profile");
        formTitle.setFont(new Font("Georgia", Font.BOLD, 18));
        formTitle.setForeground(new Color(0x564C4D));
        GridBagConstraints titleGbc = new GridBagConstraints();
        titleGbc.gridx = 0;
        titleGbc.gridy = 0;
        titleGbc.gridwidth = 2;
        titleGbc.insets = new Insets(0, 10, 30, 10);
        titleGbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(formTitle, titleGbc);

        // Input Fields
        JTextField nameField = new JTextField(15);
        nameField.setFont(defaultFont);
        JTextField dobField = new JTextField("YYYY-MM-DD");
        dobField.setFont(defaultFont);
        dobField.setForeground(Color.GRAY);
        dobField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (dobField.getText().equals("YYYY-MM-DD")) {
                    dobField.setText("");
                    dobField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (dobField.getText().isEmpty()) {
                    dobField.setText("YYYY-MM-DD");
                    dobField.setForeground(Color.GRAY);
                }
            }
        });

        JComboBox<String> sexBox = new JComboBox<>(new String[] { "Select...", "Male", "Female", "Other" });
        sexBox.setFont(defaultFont);

        JComboBox<String> unitBox = new JComboBox<>(new String[] { "Metric", "Imperial" });
        unitBox.setFont(defaultFont);

        JTextField heightField = new JTextField(10);
        heightField.setFont(defaultFont);
        JTextField weightField = new JTextField(10);
        weightField.setFont(defaultFont);
        JLabel heightUnit = new JLabel("cm");
        heightUnit.setFont(defaultFont);
        JLabel weightUnit = new JLabel("kg");
        weightUnit.setFont(defaultFont);

        unitBox.addActionListener(e -> {
            String unit = Objects.requireNonNull(unitBox.getSelectedItem()).toString();
            if (unit.equals("Metric")) {
                heightUnit.setText("cm");
                weightUnit.setText("kg");
            } else {
                heightUnit.setText("in");
                weightUnit.setText("lbs");
            }
        });

        int row = 1;
        addRow(formPanel, gbc, row++, "Name:", nameField, defaultFont);
        addRow(formPanel, gbc, row++, "Date of Birth:", dobField, defaultFont);
        addRow(formPanel, gbc, row++, "Sex:", sexBox, defaultFont);
        addRow(formPanel, gbc, row++, "Units:", unitBox, defaultFont);
        addRowWithUnit(formPanel, gbc, row++, "Height:", heightField, heightUnit, defaultFont);
        addRowWithUnit(formPanel, gbc, row++, "Weight:", weightField, weightUnit, defaultFont);

        // === SAVE BUTTON (Centered) ===
        JButton saveBtn = new JButton("✔ Save Profile");
        saveBtn.setFont(new Font("Helvetica", Font.PLAIN, 15));
        saveBtn.setBackground(new Color(168, 209, 123));
        saveBtn.setFocusPainted(false);
        saveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(saveBtn, gbc);

        // === CONTAINER WRAP ===
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(new Color(255, 251, 245));
        container.add(formPanel);
        add(container, BorderLayout.CENTER);

        // === VALIDATION & SAVE ===
        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String dob = dobField.getText().trim();
            String sex = sexBox.getSelectedItem().toString();
            String unit = unitBox.getSelectedItem().toString();
            String height = heightField.getText().trim();
            String weight = weightField.getText().trim();

            if (name.isEmpty() || dob.isEmpty() || dob.equals("YYYY-MM-DD") ||
                    sex.equals("Select...") || height.isEmpty() || weight.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please complete all fields.", "Missing Info",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!isValidDOB(dob)) {
                JOptionPane.showMessageDialog(frame, "Enter DOB in YYYY-MM-DD format. Age must be 5–120.",
                        "Invalid DOB", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double h = Double.parseDouble(height);
                double w = Double.parseDouble(weight);
                if (unit.equals("Metric")) {
                    if (h < 100 || h > 250 || w < 30 || w > 300) {
                        JOptionPane.showMessageDialog(frame, "Metric: Height must be 100–250 cm, weight 30–300 kg.",
                                "Invalid Input", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    if (h < 40 || h > 100 || w < 70 || w > 660) {
                        JOptionPane.showMessageDialog(frame, "Imperial: Height must be 40–100 in, weight 70–660 lbs.",
                                "Invalid Input", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                UserProfile profile = new UserProfile(name, dob, sex, height, weight, unit);
                UserProfile.addProfile(profile);
                UserProfile.saveProfilesToFile();
                JOptionPane.showMessageDialog(frame, "Profile created successfully!");
                frame.setContentPane(new MainMenu(frame));
                frame.revalidate();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Height and weight must be numbers.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent input, Font font) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(font);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(input, gbc);
    }

    private void addRowWithUnit(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field,
            JLabel unitLabel, Font font) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(font);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(lbl, gbc);

        JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.add(field);
        fieldPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        fieldPanel.add(unitLabel);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(fieldPanel, gbc);
    }

    private boolean isValidDOB(String dob) {
        try {
            LocalDate birthDate = LocalDate.parse(dob);
            LocalDate today = LocalDate.now();
            int age = Period.between(birthDate, today).getYears();
            return !birthDate.isAfter(today) && age >= 5 && age <= 120;
        } catch (Exception e) {
            return false;
        }
    }
}
