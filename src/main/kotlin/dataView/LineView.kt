package dataView

import ui.VirtualFileWrapper
import javax.swing.ImageIcon
import javax.swing.JPanel

class LineView(file: VirtualFileWrapper, panel: JPanel) : AbstractView(file, panel) {
    private val IMAGE_PATH = "/icons/lineChart.png"

    init {
        DATA_VIEW_ID = "Line chart"
        actionIcon = scaleIcon(ImageIcon(javaClass.getResource(IMAGE_PATH)))
    }
}
