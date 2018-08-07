package dataView

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ui.VirtualFileWrapper

import javax.swing.*
import java.awt.*
import java.awt.CardLayout


internal interface View {
    var DATA_VIEW_ID: String
    fun completePlotPanel()
    fun completeSettingsPanel()
}

abstract class AbstractView internal constructor(internal var dataFile: VirtualFileWrapper, internal var cardPlotPanel: JPanel) : View {
    var actionIcon: ImageIcon? = null

    var myViewPanel = JPanel()
    private var mySettings = JPanel()
    protected var mySettingsPanel = JPanel()
    protected var myPlotPanel = JPanel()

    protected val  myVisFactory: VisFactory = GgplotFactory()
    protected val myVisualizer : Visualizer
    // add button to change library?

    // myViewPanel is component of cardPlotPanel, so here i need to complete myViewPanel
    init {
        myVisualizer = myVisFactory.createVisualizer()
        completeMyViewPanel()
    }

    // myViewPanel consists of mySettings and PlotPanel
    // (mySettings consists of SettingsButton and SettingsPanel)
    private fun completeMyViewPanel() {
      //  completePlotPanel()
        completeMySettings()

        myViewPanel.layout = BoxLayout(myViewPanel, BoxLayout.Y_AXIS)
        myViewPanel.add(mySettings)
        myViewPanel.add(myPlotPanel)

        myPlotPanel.background = Color.WHITE
    }



    fun completeMySettings() {
        mySettings.layout = GridBagLayout();
        val gc = GridBagConstraints()
        mySettings.preferredSize = Dimension(100, 200)
        mySettings.background = Color.BLACK
        mySettings.add(mySettingsPanel, gc)

    }

    companion object {
        fun scaleIcon(icon: ImageIcon): ImageIcon {
            val ICON_SIZE = 50
            return ImageIcon(icon.image.getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_DEFAULT))
        }
    }


    fun changeVisibilityOfSettings() {
        mySettings.isVisible = !mySettings.isVisible
    }

//    override fun show() {
//        val cl = cardPlotPanel.getLayout() as CardLayout
//        cl.show(cardPlotPanel, DATA_VIEW_ID)
//
//    }

}



