package nutrisci.template;

import java.awt.*;
import java.time.LocalDate;
import javax.swing.*;

public class DatePicker extends JPanel {
    private final JTextField startDate;
    private final JTextField endDate;

    public DatePicker() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setBackground(Styles.background);

        startDate = new JTextField(LocalDate.now().minusDays(7).toString(), 10);
        startDate.setFont(Styles.small_font);
        endDate = new JTextField(LocalDate.now().toString(), 10);
        endDate.setFont(Styles.small_font);
        JLabel startLabel = new JLabel("From:");
        startLabel.setFont(Styles.small_font);
        JLabel endLabel = new JLabel("To:");
        endLabel.setFont(Styles.small_font);
        add(startLabel);
        add(startDate);
        add(Box.createHorizontalStrut(10));
        add(endLabel);
        add(endDate);
    }

    public String getStartDate() {
        return startDate.getText();
    }

    public String getEndDate() {
        return endDate.getText();
    }
}
