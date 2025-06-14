package nutrisci.gui;

import java.awt.*;
import javax.swing.*;
import nutrisci.*;
import nutrisci.template.*;

public class CreateProfilePanel extends Base {

    public CreateProfilePanel(JFrame frame) {
        super(frame);
        initializer(frame);
    }

    private void initializer(JFrame frame) {

        ProfileForm form = new ProfileForm("Create New Profile");

        //back button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setOpaque(false);
        topPanel.add(createBackButton(new MainMenu(frame)));
        add(topPanel, BorderLayout.NORTH);

        //add form
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(Styles.background);
        container.add(form);
        add(container);

        add(Box.createVerticalStrut(10));

        form.setOnSave(e -> {
            if (!form.display_errors(this))
                return;

            try {
                UserProfile profile = new UserProfile(
                        form.getNameText(), form.getDOBText(), form.getGender(),
                        form.getHeightValue(), form.getWeightValue(), form.getUnit());
                UserProfile.addProfile(profile);
                UserProfile.saveProfilesToFile();
                JOptionPane.showMessageDialog(this, "Profile created successfully!");
                frame.setContentPane(new MainMenu(frame));
                frame.revalidate();
            } catch (HeadlessException ex) {
                JOptionPane.showMessageDialog(this, "Error saving profile: " + ex.getMessage());
            }
        });

    }
}
