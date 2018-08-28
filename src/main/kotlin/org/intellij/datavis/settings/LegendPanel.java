package org.intellij.datavis.settings;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class LegendPanel implements SettingsOption {
    private final String OPTION_ID = "Legend";
    private JPanel panel;


    public LegendPanel(Settings settings) {}

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
