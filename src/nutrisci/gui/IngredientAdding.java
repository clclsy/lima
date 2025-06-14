package nutrisci.gui;

import java.awt.*;
import java.util.*;
import javax.swing.*;

public final class IngredientAdding extends JPanel {
    private final ArrayList<JTextField> ingredient = new ArrayList<>();
    private final ArrayList<JTextField> quantity = new ArrayList<>();

    public IngredientAdding() {
        setLayout(new GridLayout(0, 2, 5, 5));
        setBackground(Color.WHITE);
        for (int i = 0; i < 3; i++) {
            addRow();
        }
    }

    public void addRow() {
        JTextField igd = placeholderField("e.g. Chicken");
        JTextField qty = placeholderField("e.g. 100g");
        ingredient.add(igd);
        quantity.add(qty);
        add(igd);
        add(qty);
        revalidate();
        repaint();
    }

    public ArrayList<String> getFormattedIngredients() {
    ArrayList<String> list = new ArrayList<>();
    for (int i = 0; i < ingredient.size(); i++) {
        String igd = ingredient.get(i).getText().trim();
        String qty = quantity.get(i).getText().trim();
        if (!igd.isEmpty() && !qty.isEmpty() &&
            !igd.equals("e.g. Chicken") && !qty.equals("e.g. 100g")) {
            list.add(igd + " (" + qty + ")");
        }
    }
    return list;
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
