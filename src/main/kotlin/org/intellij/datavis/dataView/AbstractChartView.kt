package org.intellij.datavis.dataView

import com.intellij.util.ui.JBUI
import org.intellij.datavis.data.DataWrapper
import org.intellij.datavis.settings.ChartSettingsPanel
import org.intellij.datavis.settings.Settings
import org.intellij.datavis.ui.DataViewPanel
import org.intellij.datavis.ui.DataViewTabbedPanel
import org.intellij.datavis.visualization.BarChart
import org.intellij.datavis.visualization.Chart
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.Timer


internal interface ChartView : View {
    val settings: Settings
    fun completeSettingsPanel()
    fun createChart(settings: Settings) : Chart?

}

abstract class AbstractChartView(dataFile: DataWrapper, parentPanel: DataViewPanel) : AbstractView(dataFile, parentPanel), ChartView {


    internal fun addChartSettings() {
        mySettingsPanel.layout = BoxLayout(mySettingsPanel, BoxLayout.X_AXIS)
        val box = Box.createHorizontalBox()
        //left inset
        box.add(Box.createHorizontalStrut(JBUI.scale(5)))
        box.add(ChartSettingsPanel(settings))
        mySettingsPanel.add(box)

    }


    /**
     * To hide or show settings panel
     */
    fun changeVisibilityOfSettings() {
        if (hasSettings) {
            mySettingsPanel.isVisible = !mySettingsPanel.isVisible
        }
    }


    // todo may be move to DataViewPanel to create only one Timer for project?
    inner class PanelResizeListener(val settings: Settings) : ComponentListener, ActionListener {
        private val DELAY = 100
        private var waitingTimer: Timer? = null

        override fun actionPerformed(ae: ActionEvent?) {
            if (ae?.source == waitingTimer) {
                waitingTimer?.stop()
                waitingTimer = null
                applyResize(settings)
            }
        }

        override fun componentResized(p0: ComponentEvent?) {
            if (waitingTimer == null) {
                waitingTimer = Timer(DELAY, this);
                waitingTimer!!.start();
            } else {
                waitingTimer!!.restart();
            }
        }

        override fun componentHidden(p0: ComponentEvent?) {
        }

        override fun componentShown(p0: ComponentEvent?) {
        }

        override fun componentMoved(p0: ComponentEvent?) {
        }

    }

    // вот это вот не оч норм, там каждая вкладка перерисовывается
    fun applyResize(settings: Settings) {
        settings.plotSize = Dimension(Math.abs(myPlotPanel.size.width - JBUI.scale(10)), Math.abs(myPlotPanel.size.height - JBUI.scale(10)))
        if (this@AbstractChartView == parentPanel.currentOpenedView) {
            println(settings.plotSize)
            parentPanel.currentOpenedView.updatePlotPanel()
        }
    }

    override fun updatePlotPanel() {
        val chart = createChart(settings)
        drawChart(chart, myPlotPanel)
        myViewPanel.repaint()
        parentPanel.update()

    }


    fun drawChart(chart: Chart?, panel: JPanel) : Map<String, Any> {
        return if (chart != null) {
            myVisualizer.draw(chart, panel)
        } else {
            HashMap()
        }
    }
}