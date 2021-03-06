package org.intellij.datavis.dataView

import com.intellij.openapi.util.IconLoader
import org.intellij.datavis.data.DataWrapper
import org.intellij.datavis.ui.DataViewPanel
import javax.swing.*

class TableView(val file: DataWrapper, parentPanel : DataViewPanel) : AbstractView(file, parentPanel) {

    override val DATA_VIEW_ID = "Table"
    private val IMAGE_PATH = "/icons/table.png"
    override val actionIcon: Icon = IconLoader.getIcon(IMAGE_PATH)

    // don't know does TableView need settings?
    override val hasSettings: Boolean = false

    init {
        println(Thread.currentThread())

        updatePlotPanel()
        completeSettingsPanel()
    }

    override fun updatePlotPanel() {
        myVisualizer.drawTable(file, myPlotPanel)
        myViewPanel.repaint()
        parentPanel.update()
    }
}
