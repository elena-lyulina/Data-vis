package org.intellij.dataVis.dataView

import com.intellij.openapi.util.IconLoader
import org.intellij.dataVis.data.DataWrapper
import org.intellij.dataVis.ui.DataViewPanel
import javax.swing.*

class TableView(val file: org.intellij.dataVis.data.DataWrapper, parentPanel : DataViewPanel) : org.intellij.dataVis.dataView.AbstractView(file, parentPanel) {

    override val DATA_VIEW_ID = "Table"
    private val IMAGE_PATH = "/icons/table.png"
    override val actionIcon: Icon = IconLoader.getIcon(IMAGE_PATH)

    // don't know does TableView need settings?
    override val hasSettings: Boolean = false

    init {
        updatePlotPanel()
        completeSettingsPanel()
    }

    override fun completeSettingsPanel() {
    }

    override fun updatePlotPanel() {
        myVisualizer.drawTable(file, myPlotPanel)
        myViewPanel.repaint()
        parentPanel.update()
    }

}
