package dataView

import com.intellij.openapi.ui.ComboBox
import ui.Column
import ui.VirtualFileWrapper
import javax.swing.DefaultComboBoxModel
import javax.swing.ImageIcon
import javax.swing.JPanel

class ScatterView(val file: VirtualFileWrapper, panel: JPanel) : AbstractView(file, panel) {

    private val IMAGE_PATH = "/icons/scatterChart.png"

    private val xModel = DefaultComboBoxModel<Column>()
    private val xChooser = ComboBox<Column>(xModel)

    private val yModel = DefaultComboBoxModel<Column>()
    private val yChooser = ComboBox<Column>(yModel)

    init {
        DATA_VIEW_ID = "Scatter chart"
        actionIcon = scaleIcon(ImageIcon(javaClass.getResource(IMAGE_PATH)))
        completeSettingsPanel()
        completePlotPanel()

    }

    override fun completeSettingsPanel() {

        file.columns.forEach { c -> if (c.canBeCastedToDouble) { xModel.addElement(c); yModel.addElement(c) } }
        xChooser.addActionListener { e -> completePlotPanel() }
        yChooser.addActionListener { e -> completePlotPanel() }

        mySettingsPanel.add(xChooser)
        mySettingsPanel.add(yChooser)

        myViewPanel.repaint()
    }

    override fun completePlotPanel() {
        val xData = xModel.getElementAt(xChooser.selectedIndex).doubleValues
        val yData = yModel.getElementAt(yChooser.selectedIndex).doubleValues
        myVisualizer.drawScatterChart(myPlotPanel, xData, yData)
        myViewPanel.repaint()
    }


}
