package dataView

import com.intellij.openapi.ui.ComboBox
import data.Column
import data.DataWrapper
import javax.swing.DefaultComboBoxModel
import javax.swing.ImageIcon

class BarView(val file: DataWrapper) : AbstractView(file) {

    override val DATA_VIEW_ID= "Bar chart"
    private val IMAGE_PATH = "/icons/barChart.png"
    override val actionIcon = scaleIcon(ImageIcon(javaClass.getResource(IMAGE_PATH)))

    override val hasSettings = true

    private val valueModel = DefaultComboBoxModel<Column>()
    private val valueChooser = ComboBox<Column>(valueModel)


    init {
        completeSettingsPanel()
        completePlotPanel()
    }

    override fun completeSettingsPanel() {
        file.columns.forEach { c -> valueModel.addElement(c) }
        valueChooser.addActionListener { completePlotPanel() }
        mySettingsPanel.add(valueChooser)

    }

    override fun completePlotPanel() {
        myVisualizer.drawBarChart(myPlotPanel, valueModel.getElementAt(valueChooser.selectedIndex).values)
        myViewPanel.repaint()
    }
}

