package dataView

import data.DataWrapper
import javax.swing.*

class TableView(val file: DataWrapper) : AbstractView(file) {

    override val DATA_VIEW_ID = "Table"
    private val IMAGE_PATH = "/icons/table.png"
    override val actionIcon = scaleIcon(ImageIcon(javaClass.getResource(IMAGE_PATH)))

    override val hasSettings: Boolean = false

    init {

        completePlotPanel()
        completeSettingsPanel()
    }

    // don't know do TableView need settings?
    override fun completeSettingsPanel() {

    }

    override fun completePlotPanel() {
        myVisualizer.drawTable(file, myPlotPanel)
        // myViewPanel.removeAll()
        //  myViewPanel.add(JLabel(DATA_VIEW_ID))
        myViewPanel.repaint()
    }

}
