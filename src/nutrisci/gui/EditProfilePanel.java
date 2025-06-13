package nutrisci.gui;

import java.awt.*;
import javax.swing.*;
import nutrisci.MainMenu;
import nutrisci.template.*;

public class EditProfilePanel extends Base {

    private final UserProfile profile;

    public EditProfilePanel(JFrame frame, UserProfile profile) {
        super(frame);
        this.profile = profile;
        initializer(frame);
    }

    private void initializer(JFrame frame) {

        ProfileForm form = new ProfileForm("Edit Profile");
        form.setProfileData(profile);
        form.setEditable(true);

        JPanel back = new JPanel(new FlowLayout(FlowLayout.LEFT));
        back.setOpaque(false);
        back.add(createBackButton(new MainMenu(frame)));
        add(back); // This goes before the form or container

        // Add the form itself (centered)
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(Styles.background);
        container.add(form);
        add(container);

        add(Box.createVerticalStrut(10));

        form.setOnSave(e -> {
            if (!form.display_errors(this))
                return;

            try {
                profile.update(
                        form.getGender(),
                        form.getHeightValue(),
                        form.getWeightValue(),
                        form.getUnit());
                UserProfile.saveProfilesToFile();
                JOptionPane.showMessageDialog(this, "Profile updated successfully!");
                frame.setContentPane(new UserDashboardPanel(frame, profile));
                frame.revalidate();
            } catch (HeadlessException ex) {
                JOptionPane.showMessageDialog(this, "Error updating profile: " + ex.getMessage());
            }
        });

    }
}
