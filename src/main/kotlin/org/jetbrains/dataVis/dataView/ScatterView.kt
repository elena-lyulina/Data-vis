package org.jetbrains.dataVis.dataView

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.util.IconLoader
import org.jetbrains.dataVis.data.Column
import org.jetbrains.dataVis.data.DataWrapper
import org.jetbrains.dataVis.settings.Settings
import org.jetbrains.dataVis.ui.DataViewPanel
import javax.swing.DefaultComboBoxModel
import javax.swing.Icon
import javax.swing.ImageIcon

class ScatterView(val file: DataWrapper, parentPanel : DataViewPanel) : AbstractView(file, parentPanel) {

    companion object {
        private val LOG = Logger.getInstance(ScatterView::class.java)
    }

    override val DATA_VIEW_ID = "Scatter chart"
    private val IMAGE_PATH = "/icons/scatterChart.png"
    override val actionIcon: Icon = IconLoader.getIcon(IMAGE_PATH)


    //override val actionIcon: ImageIcon = ImageIcon(javaClass.getResource(IMAGE_PATH))

    override val hasSettings = true

    private val xModel = DefaultComboBoxModel<Column>()
    private val xChooser = ComboBox<Column>(xModel)

    private val yModel = DefaultComboBoxModel<Column>()
    private val yChooser = ComboBox<Column>(yModel)

    val settings = Settings(this)


    init {
        completeSettingsPanel()
        updatePlotPanel()

    }

    override fun completeSettingsPanel() {

        file.columns.forEach { c -> if (c.canBeCastedToDouble) { xModel.addElement(c); yModel.addElement(c) } }
        xChooser.addActionListener { updatePlotPanel() }
        yChooser.addActionListener { updatePlotPanel() }

        mySettingsPanel.add(xChooser)
        mySettingsPanel.add(yChooser)

        myViewPanel.repaint()
    }

    override fun updatePlotPanel() {
        if (xModel.size > 0 && yModel.size > 0) {
            LOG.info("Scatter chart is drawing")
            val xData = xModel.getElementAt(xChooser.selectedIndex).doubleValues
            val yData = yModel.getElementAt(yChooser.selectedIndex).doubleValues
            myVisualizer.drawScatterChart(myPlotPanel, xData, yData)
            myViewPanel.repaint()
            parentPanel.update()

        }
        else {
            LOG.info("No available data for scatter chart")
        }
    }

}
