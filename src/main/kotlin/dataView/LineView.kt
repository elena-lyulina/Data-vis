package dataView

import com.intellij.openapi.ui.ComboBox
import io.data2viz.axis.Orient
import io.data2viz.axis.axis
import io.data2viz.color.colors
import io.data2viz.core.Point
import io.data2viz.scale.scales
import io.data2viz.viz.Margins
import io.data2viz.viz.VizContext
import io.data2viz.viz.viz
import ui.Column
import ui.VirtualFileWrapper
import javax.swing.*
import javax.swing.ImageIcon
import javafx.embed.swing.JFXPanel
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.paint.Color
import java.util.stream.IntStream


class LineView(val file: VirtualFileWrapper, panel: JPanel) : AbstractView(file, panel) {
    override var DATA_VIEW_ID = "Line chart"
    private val IMAGE_PATH = "/icons/lineChart.png"

    private val xModel = DefaultComboBoxModel<Column>()
    private val xChooser = ComboBox<Column>(xModel)

    private val yModel = DefaultComboBoxModel<Column>()
    private val yChooser = ComboBox<Column>(yModel)

    init {
        actionIcon = scaleIcon(ImageIcon(javaClass.getResource(IMAGE_PATH)))
        completeSettingsPanel()
        completePlotPanel()
    }


    // todo: if there is no double values
    override fun completeSettingsPanel() {
        file.columns.forEach { c -> if (c.canBeCastedToDouble) { xModel.addElement(c); yModel.addElement(c) } }
        xChooser.addActionListener { completePlotPanel() }
        yChooser.addActionListener { completePlotPanel() }

        mySettingsPanel.add(xChooser)
        mySettingsPanel.add(yChooser)

        myViewPanel.repaint()
    }

    override fun completePlotPanel() {
        val xData = xModel.getElementAt(xChooser.selectedIndex).doubleValues
        val yData = yModel.getElementAt(yChooser.selectedIndex).doubleValues
        myVisualizer.drawLineChart(myPlotPanel, xData, yData)
        myViewPanel.repaint()
    }

}
