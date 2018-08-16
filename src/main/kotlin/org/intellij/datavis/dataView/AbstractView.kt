package org.intellij.datavis.dataView

import org.intellij.datavis.data.DataWrapper
import org.intellij.datavis.ui.DataViewPanel
import org.intellij.datavis.visualization.GgplotFactory
import org.intellij.datavis.visualization.VisFactory
import org.intellij.datavis.visualization.Visualizer

import javax.swing.*
import java.awt.*
import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.*


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

        c.fill = HORIZONTAL
        c.gridx = 0
        c.gridy = 0
        c.weighty = 0.0
        c.weightx= 0.0
        c.anchor = FIRST_LINE_START
        c.insets = Insets(0, 15, 0, 15)
        myViewPanel.add(mySettingsPanel, c)

        c.fill = BOTH
        c.gridx = 0
        c.gridy = 1
        c.weighty = 1.0
        c.weightx= 1.0
        c.insets = Insets(15, 15, 15, 15)
        // c.anchor = CENTER
        myViewPanel.add(myPlotPanel, c)

        myPlotPanel.background = Color.WHITE
    }


    /**
     * Views, which have settings, override that function
     * Which haven't just hide settings panel
     */
    override fun completeSettingsPanel() {
        mySettingsPanel.isVisible = hasSettings
    }

//
//    fun completeMySettings() {
//        mySettings.layout = GridBagLayout();
//        val gc = GridBagConstraints()
//        mySettings.preferredSize = Dimension(100, 200)
//        mySettings.background = Color.BLACK
//        mySettings.add(mySettingsPanel, gc)
//
//    }


    /**
     * To hide or show settings panel
     */
    fun changeVisibilityOfSettings() {
        if (hasSettings) {
            mySettingsPanel.isVisible = !mySettingsPanel.isVisible
        }
    }

}


