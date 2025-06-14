package nutrisci.gui;

import java.awt.*;
import javax.swing.*;
import nutrisci.template.*;

public class EditProfilePanel extends Base {

    private UserProfile profile;

    public EditProfilePanel(JFrame frame, UserProfile profile) {
        super(frame);
        this.profile = profile;
        initializer(frame);
    }

    private void initializer(JFrame frame) {

        ProfileForm form = new ProfileForm("Edit Profile");
        form.setProfileData(profile);
        form.setEditable(true);

        //back button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setOpaque(false);
        topPanel.add(createBackButton(new UserDashboardPanel(frame, profile)));
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

    public UserProfile getProfile() {
        return profile;
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }
}
