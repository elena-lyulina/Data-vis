package dataView

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ui.VirtualFileWrapper

import javax.swing.*
import java.awt.*
import java.awt.CardLayout




internal interface View {
    val action: AnAction
    fun completePlotPanel()
}

abstract class AbstractView internal constructor(internal var dataFile: VirtualFileWrapper, internal var cardPlotPanel: JPanel) : View {
    var DATA_VIEW_ID: String? = null
    private val ICON_SIZE = 50
    internal var actionIcon: ImageIcon? = null
    var myPlotPanel = JPanel()

    override val action: AnAction
        get() = Action()

    override fun completePlotPanel() {
        myPlotPanel.removeAll()
        myPlotPanel.add(JLabel(DATA_VIEW_ID))
        myPlotPanel.repaint()
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



