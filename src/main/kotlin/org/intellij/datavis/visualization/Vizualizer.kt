package org.intellij.datavis.visualization

import org.intellij.datavis.data.DataWrapper
import org.intellij.datavis.settings.Settings
import javax.swing.*
import javax.swing.JTable


interface Visualizer {

    fun draw(chart: ChartView, panel: JPanel)


    //todo: distinguish tableView and chartView?
    /**
     * Visualizing libraries aren't needed for drawing table, so it implements here
     */
    fun drawTable(dataFile: DataWrapper, plotPanel: JPanel) {
        plotPanel.removeAll()
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





