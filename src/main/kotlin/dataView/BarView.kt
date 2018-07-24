package dataView

import com.intellij.openapi.vfs.VirtualFile
import javax.swing.ImageIcon
import javax.swing.JPanel

class BarView(file: VirtualFile, panel: JPanel) : AbstractView(file, panel) {
    private val IMAGE_PATH = "/icons/barChart.png"

    init {
        DATA_VIEW_ID = "Bar chart"
        actionIcon = scaleIcon(ImageIcon(javaClass.getResource(IMAGE_PATH)))
    }
}
