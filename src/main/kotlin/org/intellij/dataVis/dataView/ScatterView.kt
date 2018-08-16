package org.intellij.dataVis.dataView

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.util.IconLoader
import org.intellij.dataVis.data.Column
import org.intellij.dataVis.data.DataWrapper
import org.intellij.dataVis.settings.Settings
import org.intellij.dataVis.ui.DataViewPanel
import javax.swing.DefaultComboBoxModel
import javax.swing.Icon
import javax.swing.ImageIcon

class ScatterView(val file: org.intellij.dataVis.data.DataWrapper, parentPanel : DataViewPanel) : org.intellij.dataVis.dataView.AbstractView(file, parentPanel) {

    companion object {
        private val LOG = Logger.getInstance(org.intellij.dataVis.dataView.ScatterView::class.java)
    }

    override val DATA_VIEW_ID = "Scatter chart"
    private val IMAGE_PATH = "/icons/scatterChart.png"
    override val actionIcon: Icon = IconLoader.getIcon(IMAGE_PATH)


    //override val actionIcon: ImageIcon = ImageIcon(javaClass.getResource(IMAGE_PATH))

    override val hasSettings = true

    private val xModel = DefaultComboBoxModel<org.intellij.dataVis.data.Column>()
    private val xChooser = ComboBox<org.intellij.dataVis.data.Column>(xModel)

    private val yModel = DefaultComboBoxModel<org.intellij.dataVis.data.Column>()
    private val yChooser = ComboBox<org.intellij.dataVis.data.Column>(yModel)

    val settings = org.intellij.dataVis.settings.Settings(this)


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
            org.intellij.dataVis.dataView.ScatterView.Companion.LOG.info("Scatter chart is drawing")
            val xData = xModel.getElementAt(xChooser.selectedIndex).doubleValues
            val yData = yModel.getElementAt(yChooser.selectedIndex).doubleValues
            myVisualizer.drawScatterChart(myPlotPanel, xData, yData)
            myViewPanel.repaint()
            parentPanel.update()

        }
        else {
            org.intellij.dataVis.dataView.ScatterView.Companion.LOG.info("No available data for scatter chart")
        }
    }

}
