package org.intellij.datavis.dataView

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.util.IconLoader
import org.intellij.datavis.data.Column
import org.intellij.datavis.data.DataWrapper
import org.intellij.datavis.settings.Settings
import org.intellij.datavis.ui.DataViewPanel
import org.intellij.datavis.visualization.LineChart
import javax.swing.*
import javax.swing.ImageIcon


class LineView(val file: DataWrapper, parentPanel : DataViewPanel) : AbstractView(file, parentPanel) {

    companion object {
        private val LOG = Logger.getInstance(LineView::class.java)
    }

    override val DATA_VIEW_ID = "Line chart"
    private val IMAGE_PATH = "/icons/lineChart.png"
    override val actionIcon: Icon = IconLoader.getIcon(IMAGE_PATH)

    override val hasSettings = true

    private val xModel = DefaultComboBoxModel<Column>()
    private val xChooser = ComboBox<Column>(xModel)

    private val yModel = DefaultComboBoxModel<Column>()
    private val yChooser = ComboBox<Column>(yModel)

    var settings = Settings(this)


    init {
        completeSettingsPanel()
        updatePlotPanel()
    }


    // todo: if there is no double values
    override fun completeSettingsPanel() {
        addChartSettings(settings)
        file.columns.forEach { c -> if (c.canBeCastedToDouble) { xModel.addElement(c); yModel.addElement(c) } }
        xChooser.addActionListener { updatePlotPanel() }
        yChooser.addActionListener { updatePlotPanel() }

        val box = Box.createVerticalBox()
        box.add(xChooser)
        box.add(Box.createVerticalGlue())
        box.add(yChooser)
        mySettingsPanel.add(box)

        myViewPanel.repaint()
    }

    override fun updatePlotPanel() {
        if(xModel.size > 0 && yModel.size > 0) {
            LOG.info("Line chart is drawing")
            val xData = xModel.getElementAt(xChooser.selectedIndex).doubleValues
            val yData = yModel.getElementAt(yChooser.selectedIndex).doubleValues

            val chart = LineChart(xData, yData, settings)
            myVisualizer.draw(chart, myPlotPanel)

           // myVisualizer.drawLineChart(myPlotPanel, xData, yData, settings)
            myViewPanel.repaint()
            parentPanel.update()
        }
        else {
            LOG.info("No available data for line chart ")
        }
    }

}
