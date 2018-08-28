package org.intellij.datavis.visualization

import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.lang.UrlClassLoader
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.gog.DemoAndTest
import org.intellij.datavis.settings.Settings
import java.awt.Color
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
    private val GGPLOT_NAME = "org.intellij.datavis.visualization.GgplotLib"

    private val SETTINGS_CLASS_NAME = "settings.Settings"

    private var ggplotLib: Any
    private lateinit var myClassLoader: URLClassLoader

    private val LOG = Logger.getInstance(javaClass)

    init {
        ggplotLib = loadGgplotVisualizer()
    }


    override fun draw(chart: ChartView, panel: JPanel) {
        when (chart) {
            is LineChart -> invokeMethod("drawLineChart", arrayOf(panel, chart.xData, chart.yData, chart.settings),
                    arrayOf(JPanel::class.java, List::class.java, List::class.java, Settings::class.java))
            is ScatterChart -> invokeMethod("drawScatterChart", arrayOf(panel, chart.xData, chart.yData, chart.settings),
                    arrayOf(JPanel::class.java, List::class.java, List::class.java, Settings::class.java))
            is BarChart -> invokeMethod("drawBarChart", arrayOf(panel, chart.data, chart.settings),
                    arrayOf(JPanel::class.java, List::class.java, Settings::class.java))
            else  ->  LOG.warn("Cannot draw such type of chart")

        }
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
            //todo: just for checking; find better solution
            if (name.contains("Settings")) return super.loadClass(name)


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
     *
     */
    private fun invokeMethod(name: String, args: Array<*>, classes: Array<Class<*>>) {
        assert(args.size == classes.size)
        for (i in 0 until classes.size)
            assert(classes[i].isInstance(args[i]))

        val method = ggplotLib.javaClass.getMethod(name, *classes)
        method.invoke(ggplotLib, *args)
    }

}




class GgplotLib  {
    //todo: change chart size according to window size!
    private val VIEW_SIZE = DoubleVector(1500.0, 1000.0)

    init {
        println(javaClass.classLoader)
    }

    fun drawLineChart(panel: JPanel, xData: List<Double>, yData: List<Double>, settings: Settings) {
        panel.removeAll()
        val plotSpecList = Arrays.asList<Map<String, Any>>(basicLineChart(xData, yData, settings))
        SwingDemoUtil.show(DoubleVector(settings.plotSize.getWidth(), settings.plotSize.getHeight()), plotSpecList, panel)
    }

    fun drawScatterChart(panel: JPanel, xData: List<Double>, yData: List<Double>, settings: Settings) {
        panel.removeAll()
        val plotSpecList = Arrays.asList<Map<String, Any>>(basicScatterChart(xData, yData, settings))
        SwingDemoUtil.show(DoubleVector(settings.plotSize.getWidth(), settings.plotSize.getHeight()), plotSpecList, panel)
    }

    fun drawBarChart(panel: JPanel, data: List<*>, settings: Settings) {
        panel.removeAll()
        val plotSpecList = Arrays.asList<Map<String, Any>>(basicBarChart(data, settings))
        SwingDemoUtil.show(DoubleVector(settings.plotSize.getWidth(), settings.plotSize.getHeight()), plotSpecList, panel)
    }

    private val xRepl = "x"
    private val yRepl = "y"

    private fun notEmptyTitle(xTitle: String, replacement: String) : String {
        return if(xTitle.isBlank()) replacement else xTitle
    }

    private fun lineChartData(xData: List<Double>, yData: List<Double>, settings: Settings) : Map<String, List<Double>> {
        val map = HashMap<String, List<Double>>()
        map[notEmptyTitle(settings.xTitle, xRepl)] = xData
        map[notEmptyTitle(settings.yTitle, yRepl)] = yData
        return map
    }

    private fun scatterChartData(xData: List<Double>, yData: List<Double>, settings: Settings) : Map<String, List<Double>>{
        val map = HashMap<String, List<Double>>()
        map[notEmptyTitle(settings.xTitle, xRepl)] = xData
        map[notEmptyTitle(settings.yTitle, yRepl)] = yData
        return map
    }

    private fun barChartData(data: List<*>, settings: Settings) : Map<String, List<*>>  {
        val weights = java.util.ArrayList(Collections.nCopies(data.size, 1.0))
        val map = HashMap<String, List<*>>()
        map[notEmptyTitle(settings.xTitle, xRepl)] = data
        //map["weights"] = weights
        return map
    }


    private fun xMapping(xTitle: String) =
            " 'mapping':   " +
                    "     {        " +
                    "      'x': '${notEmptyTitle(xTitle, xRepl)}'" +
                    "                           , " +
                    "             'y': '..count..'," +
                    "             'fill': '..count..'" +
                    "     }        "



    private fun xyMapping(xTitle: String, yTitle: String) =
            " 'mapping':    " +
                    "     {         " +
                    "      'x': '${notEmptyTitle(xTitle, xRepl)}'," +
                    "      'y': '${notEmptyTitle(yTitle, yRepl)}' " +
                    "     }         "


    private fun pointGeom(color: String) : String =
            "  {                                " +
                    "     'geom':  {                    " +
                    "         'name': 'point',          " +
                    "         'colour': '" + color + "'," +
                    "         'size': 5                 " +
                    "               }                   " +
                    "  }                                "


    private fun lineGeom(color: String, settings: Settings) : String =
            " {                                 " +
                    "      'geom':  {                   " +
                    "         'name': 'line',           " +
                    "         'size': 2,                " +
                    "         'colour': '" + color + "'," +
                    xyMapping(settings.xTitle, settings.yTitle) +
                    "                }                  " +
                    "  }                                "


    private fun barGeom(color: String) : String =
            " {                                 " +
                    "      'geom':  {                   " +
                    "         'name': 'bar'" +
                    ",            " +
                    "         'fill': '" + color + "'   " +
                    "                }                  " +
                    "  }                                "


    private fun title(s: String): String =
            "   'ggtitle': {         " +
                    "     'text': '" + s + "'" +
                    "              }         "


    private fun addComa(isFirstLine: Boolean, builder: StringBuilder): Boolean {
        if(!isFirstLine) {
            builder.append(", ")
        }
        return false
    }


   fun StringBuilder.appendWithComa(isFirstLine: Boolean, toAppend: String) : Boolean {
        if(!isFirstLine) {
            this.append(", ")
        }
        this.append(toAppend)
        return false
    }


    private fun theme(settings: Settings) : String {
        var addComa = true;
        val builder = StringBuilder("'theme': {")

        if (settings.xTitle.isBlank()) {
            addComa = builder.appendWithComa(addComa, "'axis_title_x': {'name': 'blank'} ")
        }

        if (settings.yTitle.isBlank()) {
            addComa = builder.appendWithComa(addComa, "'axis_title_y': {'name': 'blank'} ")
        }

        if (!settings.xLabels) {
            addComa = builder.appendWithComa(addComa, "'axis_text_x': {'name': 'blank'} ")
        }

        if (!settings.yLabels) {
            addComa = builder.appendWithComa(addComa, "'axis_text_y': {'name': 'blank'}" )
        }

        if (!settings.xTicks) {
            addComa = builder.appendWithComa(addComa, "'axis_ticks_x': {'name': 'blank'}" )
        }

        if (!settings.yTicks) {
            addComa = builder.appendWithComa(addComa, "'axis_ticks_y': {'name': 'blank'}")
        }

        if (!settings.xLines) {
            addComa = builder.appendWithComa(addComa, "'axis_line_x': {'name': 'blank'}")
            builder.append("'axis_line_x': {'name': 'blank'}")
        }

        if (!settings.yLines) {
            addComa = builder.appendWithComa(addComa, "'axis_line_y': {'name': 'blank'}")
        }

        builder.append("}")

        return builder.toString()
    }



    private fun basicLineChart(xData: List<Double>, yData: List<Double>, settings: Settings) : Map<String, Any> {
        val spec =
                "{                          " +
                        xyMapping(settings.xTitle, settings.yTitle) +       "," +
                        "   'layers': [     " +
                        lineGeom(settings.chartColor.hex(), settings) + ",     " +
                        pointGeom(settings.chartColor.darker().hex())           +
                        "             ],    " +
                        title(settings.title)  + "," +
                        theme(settings) +
                        "}                "


        val plotSpec = HashMap(DemoAndTest.parseJson(spec))
        plotSpec["data"] = lineChartData(xData, yData, settings)
        return plotSpec

    }


    private fun basicScatterChart(xData: List<Double>, yData: List<Double>, settings: Settings): Map<String, Any> {
        val spec =
                "{                    " +
                        xyMapping(settings.xTitle, settings.yTitle) +    "," +
                        "   'layers': [       " +
                        pointGeom(settings.chartColor.hex())      +
                        "             ],       " +
                        title(settings.title)  + "," +
                        theme(settings) +
                        "}                    "



        val plotSpec = HashMap(DemoAndTest.parseJson(spec))
        plotSpec["data"] = scatterChartData(xData, yData, settings)
        return plotSpec
    }

    private fun basicBarChart(data: List<*>, settings: Settings): Map<String, Any> {

        val spec =
                "{                    " +
                        xMapping(settings.xTitle) +     "," +
                        "   'layers': [       " +
                        barGeom(settings.chartColor.hex())        +
                        "             ]" +
                        ",      " +
//                        "   'scales': [" +
//                        "               {" +
//                        "                  'aesthetic': 'colour'," +
//                        "                   'colour': '${settings.chartColor.hex()}'" +
////                        "," +
////                        "                  'discrete': true," +
////                        "                  'scale_mapper_kind': 'color_hue'" +
//                        "               }" +
//                        "           ]" + "," +
                        title(settings.title) + "," +
                        theme(settings) +
                        "}                    "


        val plotSpec = HashMap(DemoAndTest.parseJson(spec))
        plotSpec["data"] = barChartData(data, settings)
        return plotSpec
    }

    private fun Color.hex() : String =  String.format("#%02x%02x%02x", this.red, this.green, this.blue)


}

