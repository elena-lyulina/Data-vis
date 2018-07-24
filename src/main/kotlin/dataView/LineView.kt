package dataView

import com.intellij.openapi.vfs.VirtualFile
import javax.swing.ImageIcon
import javax.swing.JPanel

class LineView(file: VirtualFile, panel: JPanel) : AbstractView(file, panel) {
    private val IMAGE_PATH = "/icons/lineChart.png"

    init {
        DATA_VIEW_ID = "Line chart"
        actionIcon = scaleIcon(ImageIcon(javaClass.getResource(IMAGE_PATH)))
    }
}
