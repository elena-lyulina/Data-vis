package dataView

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ui.VirtualFileWrapper

import javax.swing.*
import java.awt.*
import java.awt.CardLayout
import javax.swing.border.Border


internal interface View {
    val action: AnAction
    fun completePlotPanel()
    fun completeSettingsPanel()
}

abstract class AbstractView internal constructor(internal var dataFile: VirtualFileWrapper, internal var cardPlotPanel: JPanel) : View {
    var DATA_VIEW_ID: String? = null
    private val ICON_SIZE = 50
    private val SETTINGS_BUTTON_SIZE = 100

    internal var actionIcon: ImageIcon? = null
    var myViewPanel = JPanel()
    private var mySettings = JPanel()
    protected var mySettingsPanel = JPanel()
    protected var myPlotPanel = JPanel()

    override val action: AnAction
        get() = Action()

    override fun completePlotPanel() {
       // myViewPanel.removeAll()
        addSettings()
      //  myViewPanel.add(JLabel(DATA_VIEW_ID))
        myViewPanel.repaint()
    }

    fun addSettings() {
        mySettings.layout = GridBagLayout();
        val gc = GridBagConstraints()

        var settingsButton = JButton()
        settingsButton.icon = AllIcons.General.Settings
        settingsButton.addActionListener { _ -> mySettingsPanel.isVisible = !mySettingsPanel.isVisible }
      //  settingsButton.preferredSize = Dimension(80, 80)

        gc.gridx = 0
        gc.gridy = 0
        gc.weightx = 1.0
        gc.weighty = 1.0
        gc.anchor = GridBagConstraints.NORTHWEST
        settingsButton.preferredSize = Dimension(SETTINGS_BUTTON_SIZE, SETTINGS_BUTTON_SIZE)
        settingsButton.minimumSize = Dimension(SETTINGS_BUTTON_SIZE, SETTINGS_BUTTON_SIZE)
        mySettings.add(settingsButton, gc)

//        var separator = JSeparator(SwingConstants.VERTICAL)
//        separator.preferredSize = Dimension(mySettings.width, 10)
//
//        val sep = JSeparator()
//        sep.preferredSize = Dimension(mySettings.width, 10)
//        gc.fill = GridBagConstraints.HORIZONTAL
//        gc.weighty = 1.0
//        mySettingsPanel.add(sep, gc)

        gc.gridx = 1
        gc.gridy = 0
        gc.weightx = 1.0
        gc.weighty = 1.0
        mySettings.add(mySettingsPanel, gc)


        myViewPanel.layout = BoxLayout(myViewPanel, BoxLayout.Y_AXIS)
        myViewPanel.add(mySettings)
        myViewPanel.add(myPlotPanel)
    }



    internal fun scaleIcon(icon: ImageIcon): ImageIcon {
        return ImageIcon(icon.image.getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_DEFAULT))
    }

//    override fun show() {
//        val cl = cardPlotPanel.getLayout() as CardLayout
//        cl.show(cardPlotPanel, DATA_VIEW_ID)
//
//    }


    private inner class Action internal constructor() : AnAction(DATA_VIEW_ID, "Show dataFile as " + DATA_VIEW_ID!!, actionIcon) {

        override fun actionPerformed(e: AnActionEvent) {
            val cl = cardPlotPanel.getLayout() as CardLayout
            cl.show(cardPlotPanel, DATA_VIEW_ID)        }
    }

}



