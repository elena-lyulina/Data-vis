package org.intellij.datavis.dataView

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.util.ui.JBInsets
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.JBUI.scale
import org.intellij.datavis.data.DataWrapper
import org.intellij.datavis.settings.ChartSettingsPanel
import org.intellij.datavis.settings.Settings
import org.intellij.datavis.ui.DataViewPanel
import org.intellij.datavis.visualization.Data2vizFactory
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
}

/**
 * Abstract class for data view
 */
abstract class AbstractView internal constructor(internal var dataFile: DataWrapper, val parentPanel: DataViewPanel) : View {

    object ExtensionPoint {
        @JvmStatic
        val name : ExtensionPointName<AbstractView> = ExtensionPointName.create("org.intellij.datavis.abstractview")
    }


    var myViewPanel = JPanel()
    protected var mySettingsPanel = JPanel()
    public var myPlotPanel = JPanel()
    protected val comboBoxSize = JBUI.scale(100)


    //todo: make it up-to-date with settings.visualizer or remove
    private val myVisFactory: VisFactory = Data2vizFactory()
    protected val myVisualizer : Visualizer


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
    open fun completeSettingsPanel() {
        mySettingsPanel.isVisible = hasSettings
    }


}



