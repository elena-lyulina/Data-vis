package org.intellij.datavis.settings;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.ui.ColorPicker;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class ColorPanel implements SettingsOption {
    private final String OPTION_ID = "Color";
    private JPanel panel;
    private JPanel colorSample;
    private JPanel middlePanel;
    private Settings settings;


    public ColorPanel(Settings settings) {
        this.settings = settings;
        colorSample.setBackground(settings.getChartColor());

        DefaultActionGroup toolbarGroup = new DefaultActionGroup();
        toolbarGroup.addAction(new ColorAction());
        Dimension size = new Dimension(-1,-1);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 0;
        middlePanel.add(ActionManager.getInstance().createActionToolbar("DataViewActions", toolbarGroup, true).getComponent(), c);
    }


    class ColorAction extends AnAction {
        ColorAction() {
            super("Choose color", "Open color picker", AllIcons.Ide.Pipette);
        }
        @Override
        public void actionPerformed(AnActionEvent e) {
            Color color = ColorPicker.showDialog(panel, "Colors", settings.getChartColor(), false, null, false);
            if (color != null) {
                colorSample.setBackground(color);
                settings.setChartColor(color);
                settings.updateView();
            }
        }
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


