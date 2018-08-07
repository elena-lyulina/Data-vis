package dataView

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


class GgplotVisualizer : Visualizer() {

    private val GG_LIB_NAME = "gog-awt_deploy_woj.jar"
    private val GGPLOT_NAME = "dataView.ggplotLib"

    private var ggplotLib: Any
    private lateinit var myClassLoader: URLClassLoader


    init {
        ggplotLib = loadGgplotVisualizer()
    }

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
            if (loadedClass == null) {
                try {
                    loadedClass = findClass(name)
                } catch (e: ClassNotFoundException) {}

                if (loadedClass == null) {
                    loadedClass = super.loadClass(name)
                }
            }
            return loadedClass
        }
    }

    private fun findContainingJar(clazz: Class<*>, loader: UrlClassLoader): ArrayList<URL> {
        val resource = clazz.getResource('/' + clazz.name.replace('.', '/') + ".class")

        if (resource == null) return ArrayList()

        val url = URLDecoder.decode(resource.toString().removePrefix("jar:file:"), "UTF-8")
        val idx = url.indexOf(".jar!")
        if (idx == -1) return ArrayList()

        return loader.urls.filter{ u -> u.file.contains(url.substring(0, idx + 4)) } as ArrayList<URL>
    }


    @Throws(FileNotFoundException::class)
    fun verifyValidPath(url: URL) {
        val filePath = File(url.file)
        if (!filePath.exists()) {
            throw FileNotFoundException(filePath.path)
        }
    }

    override fun drawScatterChart(panel: JPanel, xData: List<Double>, yData: List<Double>) {
        val drawBarMethod = ggplotLib.javaClass.getMethod("drawScatterChart", JPanel::class.java, List::class.java, List::class.java)
        drawBarMethod.invoke(ggplotLib, panel, xData, yData)    }

    override fun drawLineChart(panel: JPanel, xData: List<Double>, yData: List<Double>) {
        val drawBarMethod = ggplotLib.javaClass.getMethod("drawLineChart", JPanel::class.java, List::class.java, List::class.java)
        drawBarMethod.invoke(ggplotLib, panel, xData, yData)    }

    override fun drawBarChart(panel: JPanel, data: List<*>) {
        val drawBarMethod = ggplotLib.javaClass.getMethod("drawBarChart", JPanel::class.java, List::class.java)
        drawBarMethod.invoke(ggplotLib, panel, data)

    }

}




class ggplotLib : Visualizer() {
    val VIEW_SIZE = DoubleVector(1500.0, 1000.0)

    init {
        println(javaClass.classLoader)
    }

    override fun drawLineChart(panel: JPanel, xData: List<Double>, yData: List<Double>) {
        panel.removeAll()
        val plotSpecList = Arrays.asList<Map<String, Any>>(basicLineChart(xData, yData))
        SwingDemoUtil.show(VIEW_SIZE, plotSpecList, panel)    }

    override fun drawScatterChart(panel: JPanel, xData: List<Double>, yData: List<Double>) {
        panel.removeAll()
        val plotSpecList = Arrays.asList<Map<String, Any>>(basicScatterChart(xData, yData))
        SwingDemoUtil.show(VIEW_SIZE, plotSpecList, panel)
    }

    override fun drawBarChart(panel: JPanel, data: List<*>) {
        panel.removeAll()
        val plotSpecList = Arrays.asList<Map<String, Any>>(basicBarChart(data))
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

    private fun title(s: String): String {
        return "   'ggtitle': {" +
                "                 'text': '" + s + "'" +
                "              }" +
                ""
    }

    private fun layerMapping(): String {
        return "   'mapping': {" +
                "             'x': 'x'," +
                "             'y': 'y'" +
                "           }," +
                "   'layers': [" +
                "               {" +
                "                  'geom':  {" +
                "                             'name': 'point'," +
                "                             'colour': '#4382b7'," +
                "                             'size': 5" +
                "                           }" +
                "               }" +
                "           ]" +
                ""

    }

    fun basicLineChart(xData: List<Double>, yData: List<Double>) : Map<String, Any> {
       val mapping = "{" +
                "   'mapping': {" +
                "             'x': 'x'," +
                "             'y': 'y'" +
                "           }," +
                "   'layers': [" +
                "               {" +
                "                  'geom':  {" +
                "                              'name': 'line'," +
                "                              'size': 2," +
                "                               'colour': '#a7ceef'," +
                "                               'mapping': {"  +
                "                                          'x': 'x'," +
                "                                          'y': 'y'" +
                "                                        }" +
                "                           }" +
                "               },{  " +
                "                  'geom':  {" +
                "                                        'name': 'point'," +
                "                                        'size': 5," +
                "                                        'colour': '#4382b7'" +
                "                                      }" +
                "                 }" +
                "           ]" +
                "        }" +
                ""

        val plotSpec = HashMap(DemoAndTest.parseJson(mapping))
        plotSpec["data"] = lineChartData(xData, yData)
        return plotSpec

    }


    fun basicScatterChart(xData: List<Double>, yData: List<Double>): Map<String, Any> {
        val spec = "{" +
                layerMapping() +
                "," +
                title("Default") +
                "}"

        val plotSpec = HashMap(DemoAndTest.parseJson(spec))
        plotSpec["data"] = scatterChartData(xData, yData)
        return plotSpec
    }

    protected fun basicBarChart(data: List<*>): Map<String, Any> {
        val spec = "{" +
                "   'mapping': {" +
                "             'x': 'x'" +
                "           }," +

                "   'layers': [" +
                "               {" +
                "                  'geom':  {" +
                "                             'name': 'bar'," +
                "                             'fill': '#a7ceef'" +
//                "                             'colour': 'black'"
                "                           }" +
                "               }" +
                "           ]" +
                "}"

        val plotSpec = HashMap(DemoAndTest.parseJson(spec))
        plotSpec["data"] = barChartData(data)
        return plotSpec
    }


}

