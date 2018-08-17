package org.intellij.datavis.visualization

import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.lang.UrlClassLoader
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.gog.DemoAndTest
import java.io.File
import java.io.FileNotFoundException
import java.net.URL
import java.net.URLClassLoader
import java.net.URLDecoder
import java.util.*
import javax.swing.JPanel
import kotlin.collections.ArrayList
import kotlin.reflect.full.createInstance


object GgplotVisualizer : Visualizer() {

    private val GG_LIB_NAME = "gog-awt_deploy_woj.jar"
    private val GGPLOT_NAME = "org.intellij.datavis.visualization.GplotLib"

    private val SETTINGS_CLASS_NAME = "settings.Settings"

    private var ggplotLib: Any
    private lateinit var myClassLoader: URLClassLoader

    init {
        ggplotLib = loadGgplotVisualizer()
    }


    /**
     * Ggplot lib uses apache.batik v.1.7 while plugin classloader loads apache.batik v. 1.10
     * To avoid conflicts between them i need to use my own classloader with overridden {@link ClassLoader#getResource} method
     * So {@link org.intellij.datavis.visualization.GgplotLib} class, that calls ggplot lib and draws charts, is loaded by it
     */
    private fun loadGgplotVisualizer() : Any {
        val loader = javaClass.classLoader as UrlClassLoader
        val urls = loader.urls.filter{ url -> url.file.contains(GG_LIB_NAME)} as MutableList
        val clazz = loader.loadClass(GGPLOT_NAME)
        urls.addAll(findContainingJar(clazz, loader))

        myClassLoader = MyClassLoader(urls.toTypedArray(), loader)

        val gg = myClassLoader.loadClass(clazz.canonicalName)!!
        return gg.kotlin.createInstance()
    }

    class MyClassLoader(urls: Array<URL>, parent : ClassLoader): URLClassLoader(urls, parent) {
        override fun getResources(name: String?): Enumeration<URL> {
            return findResources(name) ?: super.getResources(name)
        }

        @Throws(ClassNotFoundException::class)
        override fun loadClass(name: String): Class<*>? {
            var loadedClass = findLoadedClass(name)

            loadedClass ?: try {
                loadedClass = findClass(name)
            } catch (e: ClassNotFoundException) {}

            return loadedClass ?: super.loadClass(name)
        }
    }

    private fun findContainingJar(clazz: Class<*>, loader: UrlClassLoader): ArrayList<URL> {
        val resource = clazz.getResource('/' + clazz.name.replace('.', '/') + ".class") ?: return ArrayList()

        val url = URLDecoder.decode(resource.toString().removePrefix("jar:file:"), "UTF-8")
        val idx = url.indexOf(".jar!")
        if (idx == -1) return ArrayList()

        return loader.urls.filter{ u -> u.file.contains(url.substring(0, idx + 4)) } as ArrayList<URL>
    }


    @Throws(FileNotFoundException::class)
    private fun verifyValidPath(url: URL) {
        val filePath = File(url.file)
        if (!filePath.exists()) {
            throw FileNotFoundException(filePath.path)
        }
    }


    /**
     * These methods call corresponding methods of {@link org.intellij.datavis.visualization.GgplotLib} class, loaded by MyClassloader
     */
    override fun drawScatterChart(panel: JPanel, xData: List<Double>, yData: List<Double>) {
        val drawBarMethod = ggplotLib.javaClass.getMethod("drawScatterChart", JPanel::class.java, List::class.java, List::class.java)
        drawBarMethod.invoke(ggplotLib, panel, xData, yData)    }

    override fun drawLineChart(panel: JPanel, xData: List<Double>, yData: List<Double>) {
        val drawBarMethod = ggplotLib.javaClass.getMethod("drawLineChart", JPanel::class.java, List::class.java, List::class.java)
        drawBarMethod.invoke(ggplotLib, panel, xData, yData)    }

    override fun drawBarChart(title: String, panel: JPanel, data: List<*>) {
        val drawBarMethod = ggplotLib.javaClass.getMethod("drawBarChart", String::class.java, JPanel::class.java, List::class.java)
        drawBarMethod.invoke(ggplotLib, title, panel, data)

    }

}




class GplotLib : Visualizer() {
    //todo: change chart size according to window size!
    private val VIEW_SIZE = DoubleVector(1500.0, 1000.0)

    init {
        println(javaClass.classLoader)
    }

    override fun drawLineChart(panel: JPanel, xData: List<Double>, yData: List<Double>) {
        panel.removeAll()
        val plotSpecList = Arrays.asList<Map<String, Any>>(basicLineChart(xData, yData))
        SwingDemoUtil.show(VIEW_SIZE, plotSpecList, panel)
    }

    override fun drawScatterChart(panel: JPanel, xData: List<Double>, yData: List<Double>) {
        panel.removeAll()
        val plotSpecList = Arrays.asList<Map<String, Any>>(basicScatterChart(xData, yData))
        SwingDemoUtil.show(VIEW_SIZE, plotSpecList, panel)
    }

    override fun drawBarChart(title: String, panel: JPanel, data: List<*>) {
        panel.removeAll()
        val plotSpecList = Arrays.asList<Map<String, Any>>(basicBarChart(data, title))
        SwingDemoUtil.show(VIEW_SIZE, plotSpecList, panel)
    }

    private fun lineChartData(xData: List<Double>, yData: List<Double>) : Map<String, List<Double>> {
        val map = HashMap<String, List<Double>>()
        map["x"] = xData
        map["y"] = yData
        return map
    }

    private fun scatterChartData(xData: List<Double>, yData: List<Double>) : Map<String, List<Double>>{
        val map = HashMap<String, List<Double>>()
        map["x"] = xData
        map["y"] = yData
        return map
    }

    private fun barChartData(data: List<*>) : Map<String, List<*>>  {
        val weights = java.util.ArrayList(Collections.nCopies(data.size, 1.0))
        val map = HashMap<String, List<*>>()
        map["x"] = data
        map["weights"] = weights
        return map
    }


    private val xMapping =
            " 'mapping':   " +
                    "     {        " +
                    "      'x': 'x'" +
                    "     }        "



    private val xyMapping =
            " 'mapping':    " +
                    "     {         " +
                    "      'x': 'x'," +
                    "      'y': 'y' " +
                    "     }         "


    private fun pointGeom(color: String = "#4382b7") : String {
        return  "  {                                " +
                "     'geom':  {                    " +
                "         'name': 'point',          " +
                "         'colour': '" + color + "'," +
                "         'size': 5                 " +
                "               }                   " +
                "  }                                "
    }

    private fun lineGeom(color: String = "#a7ceef") : String {
        return  " {                                 " +
                "      'geom':  {                   " +
                "         'name': 'line',           " +
                "         'size': 2,                " +
                "         'colour': '" + color + "'," +
                xyMapping +
                "                }                  " +
                "  }                                "
    }

    private fun barGeom(color: String = "#a7ceef") : String {
        return  " {                                 " +
                "      'geom':  {                   " +
                "         'name': 'bar',            " +
                "         'fill': '" + color + "'   " +
                "                }                  " +
                "  }                                "
    }

    private fun title(s: String): String {
        return  "   'ggtitle': {         " +
                "     'text': '" + s + "'" +
                "              }         "
    }


    private fun basicLineChart(xData: List<Double>, yData: List<Double>) : Map<String, Any> {
        val spec =
                "{                    " +
                        xyMapping +    "," +
                        "   'layers': [       " +
                        lineGeom() + "," +
                        pointGeom()      +
                        "             ]       " +
                        "}                    "


        val plotSpec = HashMap(DemoAndTest.parseJson(spec))
        plotSpec["data"] = lineChartData(xData, yData)
        return plotSpec

    }


    private fun basicScatterChart(xData: List<Double>, yData: List<Double>): Map<String, Any> {
        val spec =
                "{                    " +
                        xyMapping +    "," +
                        "   'layers': [       " +
                        pointGeom()      +
                        "             ]       " +
                        "}                    "



        val plotSpec = HashMap(DemoAndTest.parseJson(spec))
        plotSpec["data"] = scatterChartData(xData, yData)
        return plotSpec
    }

    private fun basicBarChart(data: List<*>, title: String): Map<String, Any> {

        val spec =
                "{                    " +
                        xMapping +     "," +
                        "   'layers': [       " +
                        barGeom()        +
                        "             ],      " +
                        title(title)       +
                        "}                    "


        val plotSpec = HashMap(DemoAndTest.parseJson(spec))
        plotSpec["data"] = barChartData(data)
        return plotSpec
    }


}

