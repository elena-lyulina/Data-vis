package org.intellij.datavis.dataView

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.util.IconLoader
import org.intellij.datavis.data.Column
import org.intellij.datavis.data.DataWrapper
import org.intellij.datavis.settings.Settings
import org.intellij.datavis.ui.DataViewPanel
import org.intellij.datavis.visualization.BarChart
import javax.swing.BoxLayout
import javax.swing.DefaultComboBoxModel
import javax.swing.Icon
import javax.swing.JPanel

class BarView(val file: DataWrapper, parentPanel : DataViewPanel) : AbstractView(file, parentPanel) {

    override val DATA_VIEW_ID= "Bar chart"
    private val IMAGE_PATH = "/icons/barChart.png"
    override val actionIcon: Icon = IconLoader.getIcon(IMAGE_PATH)

    override val hasSettings = true

    private val valueModel = DefaultComboBoxModel<Column>()
    private val valueChooser = ComboBox<Column>(valueModel)

    val dataSettingsPanel = JPanel()
    var settings = Settings(this)

    init {
        completeSettingsPanel()
        updatePlotPanel()
    }


    /**
     * currently settings consist only of a combobox... but it s temporary
     */
    override fun completeSettingsPanel() {
        addChartSettings(settings)

        mySettingsPanel.add(dataSettingsPanel)


        file.columns.forEach { c -> valueModel.addElement(c) }
        valueChooser.addActionListener { updatePlotPanel() }

        dataSettingsPanel.add(valueChooser)

    }

    override fun updatePlotPanel() {
        val chart = BarChart(valueModel.getElementAt(valueChooser.selectedIndex).values, settings)
        myVisualizer.draw(chart, myPlotPanel)
        myViewPanel.repaint()
        parentPanel.update()
    }
}

