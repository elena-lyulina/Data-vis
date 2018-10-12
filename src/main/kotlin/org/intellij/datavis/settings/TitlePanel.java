package org.intellij.datavis.settings;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TitlePanel implements SettingsOption {
    private final String OPTION_ID = "Title";

    private JTextField titleField;
    private JPanel panel;
    private Settings settings;

    public TitlePanel(Settings settings) {
        this.settings = settings;
        titleField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    settings.setTitle(titleField.getText());
                    settings.updateView();
                }
            }
        });
    }

    @NotNull
    @Override
    public String getOPTION_ID() {
        return OPTION_ID;
    }

    @NotNull
    @Override
    public JPanel getPanel() {
        return panel;
    }

}
