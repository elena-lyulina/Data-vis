package dataView

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.vfs.VirtualFile
import io.data2viz.timeFormat.parse
import ui.VirtualFileWrapper

import javax.swing.*
import java.awt.*
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.ArrayList


internal interface View {
    val action: AnAction
    fun show()
}

abstract class AbstractView internal constructor(internal var dataFile: VirtualFileWrapper, internal var plotPanel: JPanel) : View {
    internal var DATA_VIEW_ID: String? = null
    private val ICON_SIZE = 50
    internal var actionIcon: ImageIcon? = null

    override val action: AnAction
        get() = Action()


    internal fun scaleIcon(icon: ImageIcon): ImageIcon {
        return ImageIcon(icon.image.getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_DEFAULT))
    }

    override fun show() {
        plotPanel.removeAll()
        plotPanel.add(JLabel(DATA_VIEW_ID))
        plotPanel.repaint()
    }

    private inner class Action internal constructor() : AnAction(DATA_VIEW_ID, "Show dataFile as " + DATA_VIEW_ID!!, actionIcon) {

        override fun actionPerformed(e: AnActionEvent) {
            show()
        }
    }

}



