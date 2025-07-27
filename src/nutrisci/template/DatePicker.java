package nutrisci.template;

import java.awt.*;
import java.time.LocalDate;
import javax.swing.*;

public class DatePicker extends JPanel {
    private final JTextField startField = new JTextField(10);
    private final JTextField endField = new JTextField(10);

    public DatePicker() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        add(new JLabel("From:"));
        add(startField);
        add(new JLabel("To:"));
        add(endField);
        
        // Set default values (current week)
        LocalDate today = LocalDate.now();
        startField.setText(today.minusDays(7).toString());
        endField.setText(today.toString());
    }

    public String getStartDate() {
        return startField.getText();
    }

    public String getEndDate() {
        return endField.getText();
    }
}