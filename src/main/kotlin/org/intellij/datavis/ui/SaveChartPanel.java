package org.intellij.datavis.ui;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;

public class SaveChartPanel {
    private JPanel panel;
    private JFormattedTextField heightField;
    private JFormattedTextField widthField;

    public SaveChartPanel(int primWidth, int primHeight) {
        heightField.setValue(primHeight);
        widthField.setValue(primWidth);
    }


    public JPanel getPanel() {
        return panel;
    }

    public int getWidth() {
        return ((Number) widthField.getValue()).intValue();

    }

    public int getHeight() {
        return ((Number) heightField.getValue()).intValue();

    }

    private void createUIComponents() {
        heightField = new JFormattedTextField(NumberFormat.getNumberInstance());
        widthField = new JFormattedTextField(NumberFormat.getNumberInstance());
    }
}
