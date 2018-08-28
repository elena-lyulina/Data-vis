package org.intellij.datavis.visualization

import org.intellij.datavis.data.DataWrapper
import javax.swing.*
import javax.swing.JTable


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

    fun drawTable(dataFile: DataWrapper, plotPanel: JPanel) {

        plotPanel.layout = BoxLayout(plotPanel, BoxLayout.LINE_AXIS)
        if (dataFile.parsed) {
            val table = DataTable(dataFile.headers, dataFile.columns)
            if(table.scrollableTracksViewportWidth) {
                table.autoResizeMode = JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS
            }
            plotPanel.add(table.getScrollPane())
        }

    }
}





