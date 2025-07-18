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
        endDate = new JTextField(LocalDate.now().toString(), 10);

        add(new JLabel("From: "));
        add(startDate);
        add(Box.createHorizontalStrut(10));
        add(new JLabel("To: "));
        add(endDate);
    }

    public String getStartDate() {
        return startDate.getText();
    }

    public String getEndDate() {
        return endDate.getText();
    }
}
