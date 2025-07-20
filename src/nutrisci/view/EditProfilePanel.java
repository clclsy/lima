package nutrisci.view;

import java.awt.*;
import javax.swing.*;
import nutrisci.db.UserProfileDAO;
import nutrisci.model.UserProfile;
import nutrisci.template.Base;
import nutrisci.template.ProfileForm;
import nutrisci.template.Styles;

public class EditProfilePanel extends Base {

    public EditProfilePanel(JFrame frame, UserProfile profile) {
        super(frame);
        initializer(frame, profile);
    }

    @SuppressWarnings("UseSpecificCatch")
    private void initializer(JFrame frame, UserProfile profile) {
        // Back button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setOpaque(false);
        topPanel.add(createBackButton(new UserDashboardPanel(frame, profile)));
        add(topPanel, BorderLayout.NORTH);

        // Profile form
        ProfileForm form = new ProfileForm("Edit Profile");
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(Styles.background);
        container.add(form);
        add(container, BorderLayout.CENTER);

        // Prefill form with profile data
        form.setProfileData(profile);

        // Enable editting
        form.setEditable(true);

        // Add to db
        form.setOnSave(e -> {
            if (!form.display_errors(this)) return;

            try {
                UserProfile updatedProfile = new UserProfile(
                    form.getNameText(),
                    form.getDOBText(),
                    form.getGender(),
                    Double.parseDouble(form.getHeightValue()),
                    Double.parseDouble(form.getWeightValue()),
                    form.getUnit()
                );
                
                boolean success = UserProfileDAO.updateProfile(updatedProfile);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Profile updated successfully!");
                    frame.setContentPane(new UserSelectPanel(frame));
                    frame.revalidate();
                    frame.repaint();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update profile in database.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving changes: " + ex.getMessage());
            }
        });
    }
} 
