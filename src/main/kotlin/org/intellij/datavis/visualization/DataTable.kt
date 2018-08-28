package org.intellij.datavis.visualization

import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import org.intellij.datavis.data.Column
import java.awt.Color
import java.awt.Component
import javax.swing.JTable
import javax.swing.table.AbstractTableModel
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.TableCellRenderer


class DataTable(myHeaders: List<String>, myColumns: List<Column>) : JBTable() {

    private val selectionColor = JBColor(Color(201, 209, 221), Color(13, 41, 62))
    private val secondColor = JBColor(Color(249, 251, 252), Color(43, 43, 43))

    private val myScrollPane: JBScrollPane

    init {
        model = MyModel(myHeaders, myColumns)
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF)
        setRowSelectionAllowed(true)
        setMaxItemsForSizeCalculation(50)
        setDefaultRenderer(String::class.java, MyRenderer())
        setRowHeight((getRowHeight() * 1.3).toInt())
        getTableHeader().reorderingAllowed = false

        myScrollPane = JBScrollPane(this)
    }

    inner class MyModel(private val myHeaders: List<String>, private val myColumns: List<Column>) : AbstractTableModel() {
        override fun getRowCount(): Int {
            return myColumns[0].values.size
        }

        override fun getColumnCount(): Int {
            return myHeaders.size
        }

        override fun getColumnName(i: Int): String {
            return myHeaders[i]
        }

        override fun getColumnClass(i: Int): Class<*> {
            return getValueAt(0, i).javaClass
        }

        override fun isCellEditable(i: Int, i1: Int): Boolean {
            return false
        }

        override fun getValueAt(i: Int, i1: Int): Any {
            return myColumns[i1].values[i]
        }

    }


    override fun getScrollableTracksViewportWidth(): Boolean {
        return preferredSize.width < parent.width
    }


    override fun prepareRenderer(renderer: TableCellRenderer, row: Int, column: Int): Component {
        val component = super.prepareRenderer(renderer, row, column)
        updateColumnWidth(column, component.preferredSize.width, this)
        return component
    }

    /**
     * Makes columns width long enough for header and data
     */
    private fun updateColumnWidth(column: Int, width: Int, table: JTable): Int {
        val tableColumn = table.columnModel.getColumn(column)
        val headerWidth = DefaultTableCellRenderer().getTableCellRendererComponent(table, tableColumn.headerValue, false, false, -1, column).preferredSize.width + JBUI.scale(4)
        val newWidth = Math.max(width, headerWidth) + 2 * table.intercellSpacing.width
        tableColumn.preferredWidth = Math.max(newWidth, tableColumn.preferredWidth)
        return newWidth
    }

    fun getScrollPane(): JBScrollPane {
        return myScrollPane
    }


    private inner class MyRenderer : DefaultTableCellRenderer() {
        internal var backgroundColor = background

        override fun getTableCellRendererComponent(
                table: JTable?, value: Any, isSelected: Boolean,
                hasFocus: Boolean, row: Int, column: Int): Component {

            val c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column)
            val model = table!!.model
            if (row % 2 == 0) {
                c.background = secondColor
            } else {
                c.background = backgroundColor
            }

            if (isSelected) {
                c.foreground = UIUtil.getActiveTextColor()
                c.background = selectionColor
            }
            return c
        }
    }
}