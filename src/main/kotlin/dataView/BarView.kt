package dataView

import com.intellij.ui.components.JBList
import examples.barchart_ggplot_local.Ggplot
import ui.VirtualFileWrapper
import javax.swing.ImageIcon
import javax.swing.JPanel

class BarView(file: VirtualFileWrapper, panel: JPanel) : AbstractView(file, panel) {
    override fun completeSettingsPanel() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val IMAGE_PATH = "/icons/barChart.png"

    init {
        DATA_VIEW_ID = "Bar chart"
        actionIcon = scaleIcon(ImageIcon(javaClass.getResource(IMAGE_PATH)))
        completePlotPanel()
    }

    override fun completePlotPanel() {
        //val gg = Ggplot()
       // gg.barChart(myPlotPanel)
    }

}
