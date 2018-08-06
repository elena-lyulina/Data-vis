package dataView

import com.intellij.util.lang.UrlClassLoader
import ui.VirtualFileWrapper
import java.io.File
import java.io.FileNotFoundException
import java.net.URL
import java.net.URLClassLoader
import java.net.URLDecoder
import java.util.*
import javax.swing.JPanel
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.primaryConstructor

interface VisFactory {
    fun createVisualizer() : Visualizer
}

class Data2visFactory : VisFactory {
    override fun createVisualizer() : Visualizer {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

class GgplotFactory (): VisFactory {
    override fun createVisualizer() : Visualizer {
        return GgplotVisualizer()
    }
}