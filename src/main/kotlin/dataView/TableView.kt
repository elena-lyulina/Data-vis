package dataView

import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.layout.panel
import com.intellij.ui.table.JBTable
import ui.VirtualFileWrapper

import javax.swing.*
import javax.swing.table.TableCellRenderer
import java.awt.*

class TableView(val file: VirtualFileWrapper, var panel: JPanel) : AbstractView(file, panel) {
    override var DATA_VIEW_ID = "Table"

    init {
        val IMAGE_PATH = "/icons/table.png"
        actionIcon = scaleIcon(ImageIcon(javaClass.getResource(IMAGE_PATH)))
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
