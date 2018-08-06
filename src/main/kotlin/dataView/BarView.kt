package dataView

import com.intellij.openapi.ui.ComboBox
import ui.Column
import ui.VirtualFileWrapper
import javax.swing.DefaultComboBoxModel
import javax.swing.ImageIcon
import javax.swing.JPanel

class BarView(val file: VirtualFileWrapper, var panel: JPanel) : AbstractView(file, panel) {
    private val IMAGE_PATH = "/icons/barChart.png"

    private val valueModel = DefaultComboBoxModel<Column>()
    private val valueChooser = ComboBox<Column>(valueModel)


    init {
        DATA_VIEW_ID= "Bar chart"
        actionIcon = scaleIcon(ImageIcon(javaClass.getResource(IMAGE_PATH)))
        completeSettingsPanel()
        completePlotPanel()
    }

    override fun completeSettingsPanel() {

        file.columns.forEach { c -> valueModel.addElement(c) }
        valueChooser.addActionListener { e -> completePlotPanel() }
        mySettingsPanel.add(valueChooser)

    }

    override fun completePlotPanel() {
        myVisualizer.drawBarChart(myPlotPanel, valueModel.getElementAt(valueChooser.selectedIndex).values)
        myViewPanel.repaint()
    }
}

