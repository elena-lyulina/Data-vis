package org.intellij.datavis.visualization

import com.intellij.openapi.extensions.ExtensionPointName
import org.intellij.datavis.data.DataWrapper
import org.intellij.datavis.extensions.Parent
import java.awt.image.BufferedImage
import javax.swing.*
import javax.swing.JTable


interface Visualizer {
    val VIS_ID: String


    object ExtensionPoint {
        @JvmStatic
        val name : ExtensionPointName<Visualizer> = ExtensionPointName.create("org.intellij.datavis.visualizer")
    }


    /**
     *  Draws chart on JPanel, passed as argument
     */
    fun draw(chart: Chart, panel: JPanel)


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

    /**
     *  To get chart's image; it used by SaveChart action
     */
    fun getImage(chart: Chart?) : BufferedImage?
}





