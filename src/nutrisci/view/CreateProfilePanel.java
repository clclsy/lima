package nutrisci.view;

import java.awt.*;
import javax.swing.*;
import nutrisci.db.UserProfileDAO;
import nutrisci.model.UserProfile;
import nutrisci.template.Base;
import nutrisci.template.ProfileForm;
import nutrisci.template.Styles;

public class CreateProfilePanel extends Base {

    public CreateProfilePanel(JFrame frame) {
        super(frame);
        initializer(frame);
    }

    @SuppressWarnings("UseSpecificCatch")
    private void initializer(JFrame frame) {
        // Back button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setOpaque(false);
        topPanel.add(createBackButton(new UserSelectPanel(frame)));
        add(topPanel, BorderLayout.NORTH);

        // Profile form
        ProfileForm form = new ProfileForm("Create New Profile", this);
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(Styles.background);
        container.add(form);
        add(container, BorderLayout.CENTER);

        // Add to db
        form.setOnSave(e -> {
            if (!form.display_errors(this)) return;

            try {
                UserProfile profile = new UserProfile(
                    form.getNameText(),
                    form.getDOBText(),
                    form.getGender(),
                    Double.parseDouble(form.getHeightValue()),
                    Double.parseDouble(form.getWeightValue()),
                    form.getUnit()
                );
                
                UserProfileDAO.insertProfile(profile);
                System.out.println("Inserted: " + profile.getName() + " " + profile.getDob());
                JOptionPane.showMessageDialog(this, "Profile created successfully!");
                frame.setContentPane(new UserSelectPanel(frame));
                frame.revalidate();
                frame.repaint();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving profile: " + ex.getMessage());
            }
        });
    }
} 
