package org.intellij.datavis.dataView

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.util.IconLoader
import org.intellij.datavis.data.Column
import org.intellij.datavis.data.DataWrapper
import org.intellij.datavis.ui.DataViewPanel
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

    init {
        completeSettingsPanel()
        updatePlotPanel()
    }


    /**
     * currently settings only consist of a combobox... but it s temporary
     */
    override fun completeSettingsPanel() {
        mySettingsPanel.layout = BoxLayout(mySettingsPanel, BoxLayout.LINE_AXIS)
      //  mySettingsPanel.add(ChartSettingsPanel(settings))
        mySettingsPanel.add(dataSettingsPanel)


        file.columns.forEach { c -> valueModel.addElement(c) }
        valueChooser.addActionListener { updatePlotPanel() }

        dataSettingsPanel.add(valueChooser)
//        dataSettingsPanel.background = Color.WHITE
//        dataSettingsPanel.preferredSize = Dimension(SETTINGS_WIDTH, SETTINGS_HEIGHT)
//        dataSettingsPanel.minimumSize = Dimension(SETTINGS_WIDTH, SETTINGS_HEIGHT)
//        dataSettingsPanel.maximumSize = Dimension(SETTINGS_WIDTH, SETTINGS_HEIGHT)

    }

    override fun updatePlotPanel() {
        myVisualizer.drawBarChart("", myPlotPanel, valueModel.getElementAt(valueChooser.selectedIndex).values)
        myViewPanel.repaint()
        parentPanel.update()
    }
}

