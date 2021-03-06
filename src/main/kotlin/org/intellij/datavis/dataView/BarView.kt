package org.intellij.datavis.dataView

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.JBColor
import com.intellij.util.ui.JBInsets
import com.intellij.util.ui.JBUI
import org.intellij.datavis.data.Column
import org.intellij.datavis.data.DataWrapper
import org.intellij.datavis.settings.Settings
import org.intellij.datavis.ui.DataViewPanel
import org.intellij.datavis.visualization.BarChart
import org.intellij.datavis.visualization.Chart
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*

class BarView(val file: DataWrapper, parentPanel : DataViewPanel) : AbstractChartView(file, parentPanel) {

    override val DATA_VIEW_ID= "Bar chart"
    private val IMAGE_PATH = "/icons/barChart.png"
    override val actionIcon: Icon = IconLoader.getIcon(IMAGE_PATH)

    override val hasSettings = true

    private val valueModel = DefaultComboBoxModel<Column>()
    private val valueChooser = ComboBox<Column>(valueModel)

    override val settings = Settings(this)


    init {
        println(Thread.currentThread())

        myPlotPanel.addComponentListener(PanelResizeListener(settings))
        completeSettingsPanel()
        updatePlotPanel()
    }


    override fun completeSettingsPanel() {
        addChartSettings()

        file.columns.forEach { c -> valueModel.addElement(c) }
        valueChooser.addActionListener { updatePlotPanel() }

        valueChooser.maximumSize = Dimension(comboBoxSize, valueChooser.preferredSize.height);
        valueChooser.preferredSize =  Dimension(comboBoxSize, valueChooser.preferredSize.height);
        valueChooser.minimumSize = Dimension(comboBoxSize, valueChooser.preferredSize.height);

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
        chooserPanel.add(valueChooser, c)

        chooserPanel.border = BorderFactory.createMatteBorder(1, 0, 1, 1, JBColor.DARK_GRAY)
        mySettingsPanel.add(chooserPanel)


        myViewPanel.repaint()

    }

    override fun createChart(settings: Settings): Chart? = BarChart(valueModel.getElementAt(valueChooser.selectedIndex).values, settings)
}

