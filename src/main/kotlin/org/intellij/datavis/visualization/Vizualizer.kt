package org.intellij.datavis.visualization

import org.intellij.datavis.data.DataWrapper
import org.intellij.datavis.settings.Settings
import javax.swing.*
import javax.swing.JTable


abstract class Visualizer {

    /**
     * Three types of charts are supported: barChart, lineChart and scatterChart
     */
    abstract fun drawBarChart(title: String, panel: JPanel, data: List<*>, settings: Settings)
    abstract fun drawLineChart(panel: JPanel, xData: List<Double>, yData: List<Double>, settings: Settings)
    abstract fun drawScatterChart(panel: JPanel, xData: List<Double>, yData: List<Double>, settings: Settings)


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





