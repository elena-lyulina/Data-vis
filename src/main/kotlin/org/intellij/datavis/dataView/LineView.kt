package org.intellij.datavis.dataView

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.JBColor
import com.intellij.util.ui.JBInsets
import com.intellij.util.ui.JBUI.scale
import org.intellij.datavis.data.Column
import org.intellij.datavis.data.DataWrapper
import org.intellij.datavis.settings.Settings
import org.intellij.datavis.ui.DataViewPanel
import org.intellij.datavis.visualization.LineChart
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*


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
        myPlotPanel.addComponentListener(PanelResizeListener(settings))
        completeSettingsPanel()
        updatePlotPanel()
    }


    // todo: if there is no double values
    override fun completeSettingsPanel() {
        addChartSettings(settings)
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

//        val chooserPanel = JPanel(FlowLayout())
//
//        val xPanel = JPanel()
//        xPanel.layout = BoxLayout(xPanel, BoxLayout.Y_AXIS)
//        xPanel.add(JLabel("Data for X-axis:"))
//        xPanel.add(xChooser)
//
//        val yPanel = JPanel()
//        yPanel.layout = BoxLayout(yPanel, BoxLayout.Y_AXIS)
//        yPanel.add(JLabel("Data for Y-axis:"))
//        yPanel.add(yChooser)
//
//        chooserPanel.add(xPanel)
//        chooserPanel.add(yPanel)

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

    override fun updatePlotPanel() {
        if(xModel.size > 0 && yModel.size > 0) {
            LOG.info("Line chart is drawing")
            val xData = xModel.getElementAt(xChooser.selectedIndex).doubleValues
            val yData = yModel.getElementAt(yChooser.selectedIndex).doubleValues

            val chart = LineChart(xData, yData, settings)
            myVisualizer.draw(chart, myPlotPanel)

            myViewPanel.repaint()
            parentPanel.update()
        }
        else {
            LOG.info("No available data for line chart ")
        }
    }

}
