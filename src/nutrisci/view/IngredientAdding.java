package nutrisci.view;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public final class IngredientAdding extends JPanel {
    private final ArrayList<JTextField> names = new ArrayList<>();
    private final ArrayList<JTextField> quantities = new ArrayList<>();

    public IngredientAdding() {
        setLayout(new GridLayout(0, 2, 5, 5));
        setBackground(Color.WHITE);
        for (int i = 0; i < 3; i++) {
            addRow();
        }
    }

    public void addRow() {
        JTextField name = placeholderField("e.g. Apple");
        JTextField qty = placeholderField("e.g. 100");

        names.add(name);
        quantities.add(qty);

        add(name);
        add(qty);
        revalidate();
        repaint();
    }

    public ArrayList<String[]> getInputRows() {
        ArrayList<String[]> rows = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            String n = names.get(i).getText().trim();
            String q = quantities.get(i).getText().trim();
            if (!n.isEmpty() && !q.isEmpty() &&
                    !n.equalsIgnoreCase("e.g. Apple") &&
                    !q.equals("e.g. 100")) {
                rows.add(new String[] { n, q });
            }
        }
        return rows;
    }

    private JTextField placeholderField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setForeground(Color.GRAY);

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });

        return field;
    }
}
