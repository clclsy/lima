package nutrisci.view;

import nutrisci.controller.SwapController;
import nutrisci.model.UserProfile;
import nutrisci.template.Base;
import nutrisci.template.DatePicker;
import nutrisci.template.Styles;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class ApplySwapPanel extends Base {
    private final UserProfile profile;
    private final SwapController controller;
    private JComboBox<String> originalCombo;
    private JTextField replacementField;
    private DatePicker datePicker;
    private JCheckBox allMealsCheck;

    public ApplySwapPanel(JFrame frame, UserProfile profile) {
        super(frame);
        this.profile = profile;
        this.controller = new SwapController();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Styles.background);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Styles.background);
        top.add(createTopPanel(new UserDashboardPanel(frame, profile)), BorderLayout.WEST);
        JLabel title = new JLabel("Apply Food Swap", SwingConstants.CENTER);
        title.setFont(Styles.dtitle_font);
        top.add(title, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(Styles.background);
        center.setBorder(BorderFactory.createEmptyBorder(40, 60, 60, 60));

        java.util.List<String> items = nutrisci.db.MealDAO.getDistinctIngredientsByUserId(profile.getId());
        originalCombo = new JComboBox<>(items.toArray(new String[0]));
        originalCombo.setEditable(true);
        replacementField = new JTextField(20);
        datePicker = new DatePicker();
        allMealsCheck = new JCheckBox("Apply to all meals");
        allMealsCheck.setOpaque(false);
        JButton applyBtn = new JButton("Apply Swap");

        center.add(formRow("Original Ingredient:", originalCombo));
        center.add(formRow("Replacement Ingredient:", replacementField));
        center.add(centered(datePicker));
        center.add(centered(allMealsCheck));
        center.add(Box.createVerticalStrut(20));
        center.add(centered(applyBtn));

        applyBtn.addActionListener(e -> handleApply());

        add(center, BorderLayout.CENTER);
    }

    private void handleApply() {
        String original = ((String)originalCombo.getEditor().getItem()).trim();
        String replacement = replacementField.getText().trim();
        if (original.isEmpty() || replacement.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both ingredients.");
            return;
        }
        LocalDate start = null;
        LocalDate end = null;
        if (!allMealsCheck.isSelected()) {
            try {
                start = LocalDate.parse(datePicker.getStartDate());
                end = LocalDate.parse(datePicker.getEndDate());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format.");
                return;
            }
        }
        int updated = controller.applySwapAcrossMeals(profile.getId(), original, replacement, start, end);
        JOptionPane.showMessageDialog(this, "Updated items: " + updated);
    }
} 