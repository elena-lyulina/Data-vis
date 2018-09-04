package org.intellij.datavis.dataView

import com.intellij.util.ui.JBInsets
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.JBUI.scale
import org.intellij.datavis.data.DataWrapper
import org.intellij.datavis.settings.ChartSettingsPanel
import org.intellij.datavis.settings.Settings
import org.intellij.datavis.ui.DataViewPanel
import org.intellij.datavis.visualization.GgplotFactory
import org.intellij.datavis.visualization.VisFactory
import org.intellij.datavis.visualization.Visualizer

import javax.swing.*
import java.awt.*
import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener


internal interface View {
    val DATA_VIEW_ID: String
    val actionIcon: Icon
    val hasSettings: Boolean

    /**
     * Draws plot with current settings
     */
    fun updatePlotPanel()

    fun completeSettingsPanel()
}

/**
 * Abstract class for data view
 */
abstract class AbstractView internal constructor(internal var dataFile: DataWrapper, val parentPanel: DataViewPanel) : View {

    var myViewPanel = JPanel()
    protected var mySettingsPanel = JPanel()
    protected var myPlotPanel = JPanel()

    protected val myVisFactory: VisFactory = GgplotFactory()
    protected val myVisualizer : Visualizer
    // todo: add button to change library?

    init {
        myVisualizer = myVisFactory.createVisualizer()
        completeMyViewPanel()
    }

    /**
     * myViewPanel consists of mySettingsPanel and myPlotPanel
     */
    private fun completeMyViewPanel() {
        myViewPanel.layout = GridBagLayout()
        val c = GridBagConstraints()

        c.fill = NONE
        c.gridx = 0
        c.gridy = 0
        c.weighty = 0.0
        c.weightx= 0.0
        c.anchor = FIRST_LINE_START
        //c.insets = Insets(0, 15, 0, 15)
        myViewPanel.add(mySettingsPanel, c)

        c.fill = BOTH
        c.gridx = 0
        c.gridy = 1
        c.weighty = 1.0
        c.weightx= 1.0
        c.insets = JBInsets(5, 5, 5, 5)
        c.anchor = CENTER
        myViewPanel.add(myPlotPanel, c)


        myPlotPanel.preferredSize = myPlotPanel.preferredSize;
        myPlotPanel.validate()

        myPlotPanel.background = Color.WHITE
    }


    /**
     * Views, which have settings, override that function
     * Which haven't just hide settings panel
     */
    override fun completeSettingsPanel() {
        mySettingsPanel.isVisible = hasSettings
    }

    internal fun addChartSettings(settings: Settings) {
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
        settings.plotSize = Dimension(Math.abs(myPlotPanel.size.width - scale(10)), Math.abs(myPlotPanel.size.height - scale(10)))
        if (this@AbstractView == parentPanel.currentOpenedView) {
            println(settings.plotSize)
            parentPanel.currentOpenedView.updatePlotPanel()
        }
    }



}



