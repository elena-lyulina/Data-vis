package dataView

import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.table.JBTable
import ui.VirtualFileWrapper
import java.awt.Component
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.table.TableCellRenderer


abstract class Visualizer {

//    fun drawChart(chartViewId: String?) {
//        when(chartViewId) {
//            "Table" -> drawTable()
//            "Bar chart" -> drawBarChart()
//            "Scatter chart" -> drawScatterChart()
//            "Line chart" -> drawLineChart()
//        }
//    }

    abstract fun drawBarChart(panel: JPanel, data: List<*>)
    abstract fun drawLineChart(panel: JPanel, xData: List<Double>, yData: List<Double>)
    abstract fun drawScatterChart(panel: JPanel, xData: List<Double>, yData: List<Double>)

    fun drawTable(dataFile: VirtualFileWrapper, plotPanel: JPanel) {
        if (dataFile.parsed) {
            val model = DataTableModel(dataFile.headers, dataFile.columns)
            // todo: headers
            val table = object : JBTable(model) {
                override fun prepareRenderer(renderer: TableCellRenderer, row: Int, column: Int): Component {
                    val component = super.prepareRenderer(renderer, row, column)
                    val rendererWidth = component.preferredSize.width
                    val tableColumn = getColumnModel().getColumn(column)

                    tableColumn.preferredWidth = Math.max(rendererWidth + intercellSpacing.width, tableColumn.preferredWidth)
                    return component
                }
            }


            table.autoResizeMode = JTable.AUTO_RESIZE_OFF
            //table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
            //JBScrollPane pane = new JBScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );
            //myPanel.add(pane);
            table.fillsViewportHeight = true
            plotPanel.add(ScrollPaneFactory.createScrollPane(table))
            //myPlotPanel.add(ScrollPaneFactory.createScrollPane(table))
        }

    }
}

