package org.intellij.datavis.dataView

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.JBColor
import com.intellij.util.ui.JBInsets
import com.intellij.util.ui.JBUI
import org.intellij.datavis.data.Column
import org.intellij.datavis.data.DataWrapper
import org.intellij.datavis.settings.Settings
import org.intellij.datavis.ui.DataViewPanel
import org.intellij.datavis.visualization.Chart
import org.intellij.datavis.visualization.LineChart
import org.intellij.datavis.visualization.ScatterChart
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*

class ScatterView(val file: DataWrapper, parentPanel : DataViewPanel) : AbstractChartView(file, parentPanel) {

    companion object {
        private val LOG = Logger.getInstance(ScatterView::class.java)
    }

    override val DATA_VIEW_ID = "Scatter chart"
    private val IMAGE_PATH = "/icons/scatterChart.png"
    override val actionIcon: Icon = IconLoader.getIcon(IMAGE_PATH)

    override val hasSettings = true

    private val xModel = DefaultComboBoxModel<Column>()
    private val xChooser = ComboBox<Column>(xModel)

    private val yModel = DefaultComboBoxModel<Column>()
    private val yChooser = ComboBox<Column>(yModel)

    override val settings = Settings(this)

    init {
        println(Thread.currentThread())

        myPlotPanel.addComponentListener(PanelResizeListener(settings))
        completeSettingsPanel()
        updatePlotPanel()
    }

    override fun completeSettingsPanel() {
        addChartSettings()

        file.columns.forEach { c -> if (c.canBeCastedToDouble) { xModel.addElement(c); yModel.addElement(c) } }
        xChooser.addActionListener { updatePlotPanel() }
        yChooser.addActionListener { updatePlotPanel() }

        xChooser.maximumSize = Dimension(comboBoxSize, xChooser.preferredSize.height);
        xChooser.preferredSize =  Dimension(comboBoxSize, xChooser.preferredSize.height);
        xChooser.minimumSize = Dimension(comboBoxSize, xChooser.preferredSize.height);

        yChooser.maximumSize = Dimension(comboBoxSize, yChooser.preferredSize.height);
        yChooser.preferredSize =  Dimension(comboBoxSize, yChooser.preferredSize.height);
        yChooser.minimumSize = Dimension(comboBoxSize, yChooser.preferredSize.height);

        if (xModel.size > 0) {
            xChooser.selectedIndex = 0
            yChooser.selectedIndex = yModel.size - 1
        }

        val chooserPanel = JPanel(GridBagLayout())
        val c = GridBagConstraints()

        c.gridwidth = 1
        c.anchor = GridBagConstraints.WEST

        c.gridx = 0
        c.gridy = 0
        c.insets = JBInsets(0, 6, 0, 5)
        chooserPanel.add(JLabel("Data for X-axis:"), c)

        c.gridx = 0
        c.gridy = 1
        c.insets = JBInsets(0, 5, 0, 5)
        chooserPanel.add(xChooser, c)

        c.gridx = 1
        c.gridy = 0
        c.insets = JBInsets(0, 6, 0, 5)
        chooserPanel.add(JLabel("Data for Y-axis:"), c)

        c.gridx = 1
        c.gridy = 1
        c.insets = JBInsets(0, 5, 0, 5)
        chooserPanel.add(yChooser, c)

        chooserPanel.border = BorderFactory.createMatteBorder(1, 0, 1, 1, JBColor.DARK_GRAY)
        mySettingsPanel.add(chooserPanel)

        myViewPanel.repaint()
    }

    override fun createChart(settings: Settings): Chart? {
        return if(xModel.size > 0 && yModel.size > 0) {
            LOG.info("Line chart is drawing")
            val xData = xModel.getElementAt(xChooser.selectedIndex).doubleValues
            val yData = yModel.getElementAt(yChooser.selectedIndex).doubleValues

            ScatterChart(xData, yData, settings)
        }
        else null
    }

}
