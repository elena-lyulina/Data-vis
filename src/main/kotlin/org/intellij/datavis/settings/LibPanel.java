package org.intellij.datavis.settings;

import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBList;
import org.intellij.datavis.visualization.Visualizer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class LibPanel implements SettingsOption{
    private final String OPTION_ID = "Library";
    private JPanel panel;


    public LibPanel(Settings settings) {
        DefaultListModel<Visualizer> model = new DefaultListModel<>();
        Visualizer[] listOfVis = Visualizer.ExtensionPoint.getName().getExtensions();
        for (Visualizer vis : listOfVis) {
            model.addElement(vis);
        }
        JBList<Visualizer> libList = new JBList<>(model);

        libList.setCellRenderer(new ColoredListCellRenderer<Visualizer>() {

            @Override
            protected void customizeCellRenderer(@NotNull JList<? extends Visualizer> list, Visualizer value, int index, boolean selected, boolean hasFocus) {
                append(value.getVIS_ID());
            }
        });

        if (listOfVis.length > 0) libList.setSelectedIndex(0);

        ListSelectionModel listSelectionModel = libList.getSelectionModel();
        listSelectionModel.addListSelectionListener(
                e -> {
                    if(!e.getValueIsAdjusting()) {
                        settings.setVisualizer(libList.getSelectedValue());
                        settings.updateView();
                    }
                });

        JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(libList);
        panel.add(scrollPane, BorderLayout.CENTER);


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
