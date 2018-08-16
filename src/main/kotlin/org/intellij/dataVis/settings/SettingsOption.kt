package org.intellij.dataVis.settings

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

interface Option {
    val title: String
    fun completeSettingsPanel()
}

abstract class AbstractOption(val xCoord: Int, val yCoord: Int, panelWidth: Int, panelHeight: Int, settings: org.intellij.dataVis.settings.Settings) : org.intellij.dataVis.settings.Option {
    val color = Color.GRAY
    val size = 30

    var isSettingsPanelOpen = false
    var settingsPanel = object : JPanel() {
        init {
            background = Color.WHITE
            preferredSize = Dimension(panelWidth, panelHeight)
        }
    }

    fun checkClicking(x: Int, y: Int) : Boolean {
        if ((x >= xCoord && x <= xCoord + size) && (y >= yCoord && y <= yCoord + size)) {
            isSettingsPanelOpen = !isSettingsPanelOpen
            return true
        }
        return false
    }

}

class TitleOption(val myX : Int,
                  val myY : Int,
                  val myWidth: Int,
                  val myHeight: Int,
                  val mySettings: org.intellij.dataVis.settings.Settings)
    : org.intellij.dataVis.settings.AbstractOption(myX, myY, myWidth, myHeight, mySettings) {

    override val title: String = "Title"
    private val label = JLabel(title)
    private val textField = JTextField(10)


    init {
        completeSettingsPanel()
    }

    override fun completeSettingsPanel() {
       // settingsPanel.layout = BorderLayout()
        settingsPanel.add(label)
        settingsPanel.add(textField)

        textField.addKeyListener(EnterPressingListener())

    }

    private inner class EnterPressingListener : KeyAdapter() {

        override fun keyPressed(e: KeyEvent) {
            if (e.keyCode == KeyEvent.VK_ENTER) {
                mySettings.TITLE = textField.text
            }
        }

    }

}

class AxisOption(val myX : Int,
                 val myY : Int,
                 val myWidth: Int,
                 val myHeight: Int,
                 val mySettings: org.intellij.dataVis.settings.Settings)
    : org.intellij.dataVis.settings.AbstractOption(myX, myY, myWidth, myHeight, mySettings) {

    override val title: String = "Axis"
    val label = JLabel(title)


    init {
        completeSettingsPanel()
    }

    override fun completeSettingsPanel() {
        settingsPanel.layout = BorderLayout()
        settingsPanel.add(label, BorderLayout.NORTH)
    }

}

class LegendOption(val myX : Int,
                   val myY : Int,
                   val myWidth: Int,
                   val myHeight: Int,
                   val mySettings: org.intellij.dataVis.settings.Settings)
    : org.intellij.dataVis.settings.AbstractOption(myX, myY, myWidth, myHeight, mySettings) {

    override val title: String = "Legend"
    val label = JLabel(title)


    init {
        completeSettingsPanel()
    }

    override fun completeSettingsPanel() {
        settingsPanel.layout = BorderLayout()
        settingsPanel.add(label, BorderLayout.NORTH)
    }

}

class ColorsOption(val myX : Int,
                   val myY : Int,
                   val myWidth: Int,
                   val myHeight: Int,
                   val mySettings: org.intellij.dataVis.settings.Settings)
    : org.intellij.dataVis.settings.AbstractOption(myX, myY, myWidth, myHeight, mySettings) {

    override val title: String = "Colors"
    val label = JLabel(title)

    init {
        completeSettingsPanel()
    }

    override fun completeSettingsPanel() {
        settingsPanel.layout = BorderLayout()
        settingsPanel.add(label, BorderLayout.NORTH)
    }

}

