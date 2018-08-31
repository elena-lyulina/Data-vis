package org.intellij.datavis.settings;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBDimension;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.awt.*;

import static com.intellij.util.ui.JBUI.scale;

public class AxisPanel implements SettingsOption {
    private final String OPTION_ID = "Axis";
    private JPanel panel;
    private JBScrollPane scrollPane;
    private JTable table;

    private Settings settings;

    private int minColumnWidth = scale(70);

    public AxisPanel(Settings settings) {
        this.settings = settings;


        MyTableModel myModel = new MyTableModel();
        table = new MyTable(myModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setSelectionModel(new NullSelectionModel());
        table.setFillsViewportHeight(true);

        scrollPane.setViewportView(table);
        scrollPane.setMaximumSize(new JBDimension(220, 100));
        scrollPane.setPreferredSize(new JBDimension(220, 100));

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

    class MyTableModel extends AbstractTableModel {
        private String[] columnNames = {"Properties", "x", "y"};

        private Object[][] data = new Object[][]{
                {"titles", settings.getXTitle(), settings.getYTitle() },
                { "labels", settings.getXLabels(), settings.getYLabels() },
                {"ticks", settings.getXTicks(), settings.getYTicks() },
                { "lines", settings.getXLines(), settings.getYLines()} };

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return settings.getGettersAxisTable().size();
            //return data.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return settings.getGettersAxisTable().get(row).get(col).invoke();
            //return data[row][col];
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            return col != 0;
        }

        public void setValueAt(Object value, int row, int col) {
            settings.getSettersAxisTable().get(row).get(col).invoke(value);
           // data[row][col] = value;
           // fireTableCellUpdated(row, col);
        }
    }


    class MyTable extends JBTable {
        MyTable(TableModel model) {
            super(model);
        }

        @Override
        public TableCellRenderer getCellRenderer(int row, int column) {
            if (getValueAt(row, column) instanceof Boolean) {
                return super.getDefaultRenderer(Boolean.class);
            } else {
                return super.getCellRenderer(row, column);
            }
        }

        @Override
        public TableCellEditor getCellEditor(int row, int column) {
            if (getValueAt(row, column) instanceof Boolean) {
                return super.getDefaultEditor(Boolean.class);
            } else {
                return super.getCellEditor(row, column);
            }
        }

        @NotNull
        @Override
        public Component prepareRenderer(@NotNull TableCellRenderer renderer, int row, int column) {
            Component component = super.prepareRenderer(renderer, row, column);
            updateColumnWidth(column, component.getPreferredSize().width, this);
            return component;
        }

        private void updateColumnWidth(int column, int width, MyTable table) {
            TableColumn tableColumn = table.getColumnModel().getColumn(column);
            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
            Component header = renderer.getTableCellRendererComponent(table, tableColumn.getHeaderValue(), false, false, -1, column);
            int headerWidth = header.getPreferredSize().width;
            int newWidth = Math.max(minColumnWidth, headerWidth) + 2 * table.getIntercellSpacing().width;
            int maxWidth = Math.max(newWidth, tableColumn.getPreferredWidth());
            tableColumn.setPreferredWidth(maxWidth);
            tableColumn.setMinWidth(maxWidth);
//            tableColumn.setMaxWidth(maxWidth);
        }


    }

    class NullSelectionModel implements ListSelectionModel {
        public boolean isSelectionEmpty() { return true; }
        public boolean isSelectedIndex(int index) { return false; }
        public int getMinSelectionIndex() { return -1; }
        public int getMaxSelectionIndex() { return -1; }
        public int getLeadSelectionIndex() { return -1; }
        public int getAnchorSelectionIndex() { return -1; }
        public void setSelectionInterval(int index0, int index1) { }
        public void setLeadSelectionIndex(int index) { }
        public void setAnchorSelectionIndex(int index) { }
        public void addSelectionInterval(int index0, int index1) { }
        public void insertIndexInterval(int index, int length, boolean before) { }
        public void clearSelection() { }
        public void removeSelectionInterval(int index0, int index1) { }
        public void removeIndexInterval(int index0, int index1) { }
        public void setSelectionMode(int selectionMode) { }
        public int getSelectionMode() { return SINGLE_SELECTION; }
        public void addListSelectionListener(ListSelectionListener lsl) { }
        public void removeListSelectionListener(ListSelectionListener lsl) { }
        public void setValueIsAdjusting(boolean valueIsAdjusting) { }
        public boolean getValueIsAdjusting() { return false; }
    }



}







