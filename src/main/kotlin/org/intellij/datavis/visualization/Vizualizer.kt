package org.intellij.datavis.visualization

import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI.scale
import org.intellij.datavis.data.DataWrapper
import java.awt.*
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.ScrollPaneConstants
import javax.swing.border.Border
import javax.swing.table.*


abstract class Visualizer {

    /**
     * Three types of charts are supported: barChart, lineChart and scatterChart
     */
    abstract fun drawBarChart(title: String, panel: JPanel, data: List<*>)
    abstract fun drawLineChart(panel: JPanel, xData: List<Double>, yData: List<Double>)
    abstract fun drawScatterChart(panel: JPanel, xData: List<Double>, yData: List<Double>)


    /**
     * Visualizing libraries aren't needed for drawing table, so it implements here
     */

    //todo: stretch all columns according to window size?
    fun drawTable(dataFile: DataWrapper, plotPanel: JPanel) {

        plotPanel.layout = BoxLayout(plotPanel, BoxLayout.LINE_AXIS)
        if (dataFile.parsed) {
            val model = DataTableModel(dataFile.headers, dataFile.columns)
            val table = MyTable(model)
            table.autoResizeMode = JTable.AUTO_RESIZE_OFF
            table.setMaxItemsForSizeCalculation(100)
            table.fillsViewportHeight = false

            val scrollPane = JBScrollPane(table)
            plotPanel.add(scrollPane)
        }

    }

    inner class MyTable(model : TableModel) : JBTable(model) {
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
            val headerWidth = DefaultTableCellRenderer().getTableCellRendererComponent(table, tableColumn.headerValue, false, false, -1, column).preferredSize.width + scale(4)
            val newWidth = Math.max(width, headerWidth) + 2 * table.intercellSpacing.width
            tableColumn.preferredWidth = Math.max(newWidth, tableColumn.preferredWidth)
            return newWidth
        }
    }

}

