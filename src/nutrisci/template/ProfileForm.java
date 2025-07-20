package nutrisci.template;

import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.util.*;
import javax.swing.*;
import nutrisci.model.UserProfile;

public class ProfileForm extends JPanel {
    private final JTextField name = new JTextField(15);
    private final JTextField dob = new JTextField("YYYY-MM-DD");
    private final JComboBox<String> gender = new JComboBox<>(new String[] { "Select...", "Male", "Female", "Other" });
    private final JComboBox<String> unit = new JComboBox<>(new String[] { "Select...", "Metric", "Imperial" });
    private final JTextField height = new JTextField(10);
    private final JTextField weight = new JTextField(10);
    private final JLabel height_unit = new JLabel();
    private final JLabel weight_unit = new JLabel();
    private final Map<String, JComponent> inputs = new LinkedHashMap<>();
    private final JButton saveBtn = new JButton();

    // Form Layout
    public ProfileForm(String title) {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        setOpaque(true);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 2, true),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)));
        setPreferredSize(new Dimension(450, 500));
        setMaximumSize(new Dimension(600, 650));
        setMinimumSize(new Dimension(350, 400));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Title 
        JLabel formTitle = new JLabel(title);
        formTitle.setFont(Styles.dtitle_font);
        GridBagConstraints titleGbc = new GridBagConstraints();
        titleGbc.gridx = 0;
        titleGbc.gridy = 0;
        titleGbc.gridwidth = 2;
        titleGbc.insets = new Insets(0, 10, 30, 10);
        titleGbc.anchor = GridBagConstraints.CENTER;
        add(formTitle, titleGbc);

        // Adding different rows in form
        int row = 1;
        add_row("Name:", name, gbc, row++);
        setupDOBField();
        add_row("Date of Birth:", dob, gbc, row++);
        add_row("Gender:", gender, gbc, row++);
        add_row("Units:", unit, gbc, row++);
        setupUnitSwitcher();
        add_row_unit("Height:", height, height_unit, gbc, row++);
        add_row_unit("Weight:", weight, weight_unit, gbc, row++);

        String text;
        if (title.equals("Create New Profile")) {
            text = "✔ Save Profile";
        } else {
            text = "✔ Save Changes";
        }

        saveBtn.setText(text);
        saveBtn.setFont(Styles.default_font);
        saveBtn.setBackground(new Color(168, 209, 123));
        saveBtn.setFocusPainted(false);
        saveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel button_panel = new JPanel();
        button_panel.setBackground(Color.WHITE);
        button_panel.add(saveBtn);

        // Add to main form
        GridBagConstraints btnGbc = new GridBagConstraints();
        btnGbc.gridx = 0;
        btnGbc.gridy = row + 1;
        btnGbc.gridwidth = 2;
        btnGbc.anchor = GridBagConstraints.CENTER;
        btnGbc.insets = new Insets(20, 10, 10, 10);
        add(button_panel, btnGbc);

    }

    // Add fillable row with no unit like name
    private void add_row(String label, JComponent input, GridBagConstraints gbc, int row) {

        JLabel lbl = new JLabel(label);
        lbl.setFont(Styles.default_font);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        add(lbl, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(input, gbc);

        inputs.put(label, input);

    }
    
    // Add fillable row with unit like height
    private void add_row_unit(String label, JTextField field, JLabel unit, GridBagConstraints gbc, int row) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(Styles.default_font);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        add(lbl, gbc);

        field.setFont(Styles.default_font);
        unit.setFont(Styles.default_font);

        JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.add(field);
        fieldPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        fieldPanel.add(unit);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(fieldPanel, gbc);

        inputs.put(label, field);
    }

    // Add row for DOB
    private void setupDOBField() {
        dob.setFont(Styles.default_font);
        dob.setForeground(Color.GRAY);
        dob.addFocusListener(new FocusAdapter() {
            // When being filled, acceptable format disappears
            @Override
            public void focusGained(FocusEvent e) {
                if (dob.getText().equals("YYYY-MM-DD")) {
                    dob.setText("");
                }
            }
            // When not or not being filled, shows acceptable format
            @Override
            public void focusLost(FocusEvent e) {
                if (dob.getText().isEmpty()) {
                    dob.setText("YYYY-MM-DD");
                    dob.setForeground(Color.GRAY);
                }
            }
        });
    }

    // Display unit label according to unit chosen
    private void setupUnitSwitcher() {
        unit.addActionListener(e -> {
            String unit_selected = unit.getSelectedItem().toString();
            if (unit_selected.equals("Metric")) {
                height_unit.setText("cm");
                weight_unit.setText("kg");
            } else {
                height_unit.setText("in");
                weight_unit.setText("lbs");
            }
        });
    }

    public String getNameText() {
        return name.getText().trim();
    }

    public String getDOBText() {
        return dob.getText().trim();
    }

    public String getGender() {
        return Objects.requireNonNull(gender.getSelectedItem()).toString();
    }

    public String getUnit() {
        return Objects.requireNonNull(unit.getSelectedItem()).toString();
    }

    public String getHeightValue() {
        return height.getText().trim();
    }

    public String getWeightValue() {
        return weight.getText().trim();
    }

    public boolean isComplete() {
        return !getNameText().isEmpty()
                && !getDOBText().isEmpty()
                && !getDOBText().equals("YYYY-MM-DD")
                && !getGender().equals("Select...")
                && !getUnit().equals("Select...")
                && !getHeightValue().isEmpty()
                && !getWeightValue().isEmpty();
    }

    public boolean isValidDOB() {
        try {
            LocalDate birthDate = LocalDate.parse(getDOBText());
            LocalDate today = LocalDate.now();
            int age = Period.between(birthDate, today).getYears();
            return !birthDate.isAfter(today) && age >= 5 && age <= 120;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isValidHeightWeight() {
        try {
            double h = Double.parseDouble(getHeightValue());
            double w = Double.parseDouble(getWeightValue());
            if (getUnit().equals("Metric")) {
                return (h >= 100 && h <= 250) && (w >= 30 && w <= 300);
            } else {
                return (h >= 40 && h <= 100) && (w >= 70 && w <= 660);
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Different errors according to where error is
    public boolean display_errors(Component parent) {

        if (!isComplete()) {
            JOptionPane.showMessageDialog(parent, "Please complete all fields.", "Missing Info",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (!isValidDOB()) {
            JOptionPane.showMessageDialog(parent, "DOB must be in YYYY-MM-DD format, age 5–120.", "Invalid DOB",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (!isValidHeightWeight()) {
            JOptionPane.showMessageDialog(parent, "Height or weight out of range or not a number.", "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;

    }
    
    // Methods for editing
    public Map<String, JComponent> getInputs() {
        return inputs;
    }

    public void setOnSave(ActionListener action) {
        saveBtn.addActionListener(action);
    }

    public void setProfileData(UserProfile p) {
        name.setText(p.getName());
        dob.setText(p.getDob());
        gender.setSelectedItem(p.getGender());
        unit.setSelectedItem(p.getUnit());
        height.setText(String.valueOf(p.getHeight()));
        weight.setText(String.valueOf(p.getWeight()));

    }

    public void setEditable(boolean lockNameAndDob) {
        name.setEditable(!lockNameAndDob);
        dob.setEditable(!lockNameAndDob);
        gender.setEditable(lockNameAndDob);
        unit.setEditable(lockNameAndDob);
        height.setEditable(lockNameAndDob);
        weight.setEditable(lockNameAndDob);
    }

}
