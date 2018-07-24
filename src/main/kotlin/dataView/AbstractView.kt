package dataView

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.vfs.VirtualFile
import io.data2viz.timeFormat.parse

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

abstract class AbstractView internal constructor(internal var dataFile: VirtualFile, internal var plotPanel: JPanel) : View {
    internal var DATA_VIEW_ID: String? = null
    private val ICON_SIZE = 50
    internal var actionIcon: ImageIcon? = null
    internal var parsedData: ArrayList<Array<String>> = ArrayList();

    override val action: AnAction
        get() = Action()

    init {
        parseCSV()
    }

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


    // just first implementation todo: check Scientific mode and OpenCSV
    // may it should be connected with virtualFile, not with view?
    private fun parseCSV() {
//        val reader = BufferedReader(FileReader(dataFile.path))
//        reader.lines().map{str ->
//          //  parsedData.add(str.split(DEFAULT_SEPARATOR.toRegex()));
//            println(str)}

        val stream = Files.newInputStream(Paths.get(dataFile.path))
        stream.buffered().reader().use { reader ->
            reader.readLines().map { l -> parsedData.add(l.split(DEFAULT_SEPARATOR.toRegex()).toTypedArray()) }
        }
    }

    companion object {
        private val DEFAULT_SEPARATOR = ","
    }
}



