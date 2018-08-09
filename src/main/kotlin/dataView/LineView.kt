package dataView

import com.intellij.openapi.ui.ComboBox
import data.Column
import data.DataWrapper
import javax.swing.*
import javax.swing.ImageIcon


class LineView(val file: DataWrapper) : AbstractView(file) {

    override val DATA_VIEW_ID = "Line chart"
    private val IMAGE_PATH = "/icons/lineChart.png"
    override val actionIcon: ImageIcon = scaleIcon(ImageIcon(javaClass.getResource(IMAGE_PATH)))

    override val hasSettings = true

    private val xModel = DefaultComboBoxModel<Column>()
    private val xChooser = ComboBox<Column>(xModel)

    private val yModel = DefaultComboBoxModel<Column>()
    private val yChooser = ComboBox<Column>(yModel)

    init {
        completeSettingsPanel()
        completePlotPanel()
    }


    // todo: if there is no double values
    override fun completeSettingsPanel() {
        file.columns.forEach { c -> if (c.canBeCastedToDouble) { xModel.addElement(c); yModel.addElement(c) } }
        xChooser.addActionListener { completePlotPanel() }
        yChooser.addActionListener { completePlotPanel() }

        mySettingsPanel.add(xChooser)
        mySettingsPanel.add(yChooser)

        myViewPanel.repaint()
    }

    override fun completePlotPanel() {
        val xData = xModel.getElementAt(xChooser.selectedIndex).doubleValues
        val yData = yModel.getElementAt(yChooser.selectedIndex).doubleValues
        myVisualizer.drawLineChart(myPlotPanel, xData, yData)
        myViewPanel.repaint()
    }

}
