package org.jetbrains.dataVis.dataView

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.util.IconLoader
import org.jetbrains.dataVis.data.Column
import org.jetbrains.dataVis.data.DataWrapper
import org.jetbrains.dataVis.settings.ChartSettingsPanel
import org.jetbrains.dataVis.settings.Settings
import org.jetbrains.dataVis.ui.DataViewPanel
import java.awt.Color
import javax.swing.*

class BarView(val file: DataWrapper, parentPanel : DataViewPanel) : AbstractView(file, parentPanel) {

    override val DATA_VIEW_ID= "Bar chart"
    private val IMAGE_PATH = "/icons/barChart.png"
    override val actionIcon: Icon = IconLoader.getIcon(IMAGE_PATH)

    //override val actionIcon = ImageIcon(javaClass.getResource(IMAGE_PATH))

    override val hasSettings = true

    private val valueModel = DefaultComboBoxModel<Column>()
    private val valueChooser = ComboBox<Column>(valueModel)

    val settings = Settings(this)
    val dataSettingsPanel = JPanel()

    init {
        completeSettingsPanel()
        updatePlotPanel()
    }

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
        myVisualizer.drawBarChart(settings.TITLE, myPlotPanel, valueModel.getElementAt(valueChooser.selectedIndex).values)
        myViewPanel.repaint()
        parentPanel.update()
    }
}

