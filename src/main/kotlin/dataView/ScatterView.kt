package dataView

import com.intellij.openapi.vfs.VirtualFile
import javax.swing.ImageIcon
import javax.swing.JPanel

class ScatterView(file: VirtualFile, panel: JPanel) : AbstractView(file, panel) {
    private val IMAGE_PATH = "/icons/scatterChart.png"

    init {
        DATA_VIEW_ID = "Scatter chart"
        actionIcon = scaleIcon(ImageIcon(javaClass.getResource(IMAGE_PATH)))
    }


}
