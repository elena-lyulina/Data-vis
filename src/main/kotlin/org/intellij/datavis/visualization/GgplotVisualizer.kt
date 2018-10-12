package org.intellij.datavis.visualization

import com.intellij.openapi.diagnostic.Logger
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.gog.DemoAndTest
import org.intellij.datavis.settings.Settings
import java.awt.Color
import java.awt.image.BufferedImage
import java.net.URLClassLoader
import java.util.*
import javax.swing.JPanel
import kotlin.collections.HashMap

object GgplotVisualizer : Visualizer {
    override val VIS_ID: String
        get() = "Ggplot"

    private val LOG = Logger.getInstance(javaClass)

    @Synchronized override fun draw(chart: Chart, panel: JPanel) {
        drawAndGetMap(chart, panel)
    }

    private fun drawAndGetMap(chart: Chart, panel: JPanel) : Map<String, Any> {
        return when (chart) {
            is LineChart -> drawLineChart(panel, chart.xData, chart.yData, chart.settings)
            is ScatterChart -> drawScatterChart(panel, chart.xData, chart.yData, chart.settings)
            is BarChart -> drawBarChart(panel, chart.data, chart.settings)
            else -> {
                LOG.warn("Such chart id doesn't support")
                HashMap()
            }
        }
    }


    override fun getImage(chart: Chart?): BufferedImage? {
        return chart?.let {SwingDemoUtil.getImageFromPlotSpec(drawAndGetMap(chart, JPanel()), chart.settings.plotSize.width, chart.settings.plotSize.height)}
    }


    private fun drawLineChart(panel: JPanel, xData: List<Double>, yData: List<Double>, settings: Settings) : Map<String, Any> {
        panel.removeAll()
        val plotSpec = basicLineChart(xData, yData, settings)
        SwingDemoUtil.show(DoubleVector(settings.plotSize.getWidth(), settings.plotSize.getHeight()), plotSpec, panel)
        return plotSpec
    }

    private fun drawScatterChart(panel: JPanel, xData: List<Double>, yData: List<Double>, settings: Settings) : Map<String, Any> {
        panel.removeAll()
        val plotSpec = basicScatterChart(xData, yData, settings)
        SwingDemoUtil.show(DoubleVector(settings.plotSize.getWidth(), settings.plotSize.getHeight()), plotSpec, panel)
        return plotSpec
    }

    private fun drawBarChart(panel: JPanel, data: List<*>, settings: Settings) : Map<String, Any> {
        panel.removeAll()
        val plotSpec = basicBarChart(data, settings)
        SwingDemoUtil.show(DoubleVector(settings.plotSize.getWidth(), settings.plotSize.getHeight()), plotSpec, panel)
        return plotSpec
    }

    private const val xRepl = "x"
    private const val yRepl = "y"

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
                    "         'size': 4                 " +
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


   private fun StringBuilder.appendWithComa(isFirstLine: Boolean, toAppend: String) : Boolean {
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

