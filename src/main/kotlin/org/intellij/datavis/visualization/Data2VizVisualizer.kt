package org.intellij.datavis.visualization

import io.data2viz.axis.Orient
import io.data2viz.axis.axis
import io.data2viz.color.color
import io.data2viz.color.colors
import io.data2viz.core.Point
import io.data2viz.scale.scales
import io.data2viz.viz.Margins
import io.data2viz.viz.VizContext
import io.data2viz.viz.viz
import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.embed.swing.SwingFXUtils
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.image.WritableImage
import java.awt.image.BufferedImage
import javax.swing.JPanel
import kotlin.math.min
import java.util.concurrent.FutureTask




object Data2VizVisualizer : Visualizer {
    override val VIS_ID: String
        get() = "Data2viz"

    @Synchronized override fun draw(chart: Chart, panel: JPanel) {
        drawAndGetFXPanel(chart, panel)
    }

    override fun getImage(chart: Chart?): BufferedImage? {
        if (chart != null) {
            val size = chart.settings.plotSize
            val fxPanel = drawAndGetFXPanel(chart, JPanel())
            val image = WritableImage(size.width, size.height)

            val query = FutureTask {
                fxPanel.scene.snapshot(image)
                SwingFXUtils.fromFXImage(image, null)
            }

            Platform.runLater(query)
            return query.get()

        } else return null
    }


    private val margins = Margins(40.5, 30.5, 50.5, 50.5)

    private fun drawAndGetFXPanel(chart: Chart, panel: JPanel) : JFXPanel {
        panel.removeAll()

        val width = chart.settings.plotSize.width - margins.hMargins
        val height = chart.settings.plotSize.height - margins.vMargins

        val fxPanel = JFXPanel()

        Platform.setImplicitExit(false);
        Platform.runLater {
            val root = Group()
            root.viz {
                when(chart) {
                    is LineChart -> visLineOrScatter(chart.xData, chart.yData, chart, width, height)
                    is ScatterChart -> visLineOrScatter(chart.xData, chart.yData, chart, width, height)
                    is BarChart -> visBarChart(chart.data, chart, width, height)
                } }
            fxPanel.scene = Scene(root, width + margins.hMargins, height + margins.vMargins)
        }

        panel.add(fxPanel)
        return fxPanel
    }

    private fun VizContext.visLineOrScatter(xPoints: List<Double>, yPoints: List<Double>, chart: Chart, width: Double, height: Double) {

        val points = ArrayList<Point>()

        val size = min(xPoints.size, yPoints.size)
        for (i in 0 until size) {
            points.add(Point(xPoints[i], yPoints[i]))
        }

        transform {
            translate(x = margins.left, y = margins.top)
        }

        val xMin = points.map { p -> p.x }.min()!!
        val yMin = points.map { p -> p.y }.min()!!

        val xMax = points.map { p -> p.x }.max()!!
        val yMax = points.map { p -> p.y }.max()!!


        // The ordinate scale is a standard linear scale
        val yScale = scales.continuous.linear {
            domain = listOf(yMin, yMax)
            range = listOf(height, .0)
        }


        // The abciss scale is also linear scale (could have been a timeScale)
        val xScale = scales.continuous.linear {
            domain = listOf(xMin, xMax)
            range = listOf(.0, width)
        }


        group {
            transform {
                translate(x = -10.0)
            }
            axis(Orient.LEFT, yScale)
        }

        group {
            transform {
                translate(y = height + 10.0)
            }
            axis(Orient.BOTTOM, xScale)       // <- default axis. Labels are provided by the x scale.
        }


        group {

            path {                            // <- the curve is rendered with a path.
                fill = null
                stroke = chart.settings.chartColor.hex().color  // <- code completion due to typed system
                strokeWidth = 1.5

                moveTo(xScale(points[0].x), yScale(points[0].y))

                (0..(points.size - 1)).forEach {

                    // todo: find better solution
                    when(chart) {
                        is LineChart -> lineTo(xScale(points[it].x), yScale(points[it].y))
                        is ScatterChart -> circle {
                            fill = chart.settings.chartColor.hex().color
                            cx = xScale(points[it].x)
                            cy =  yScale(points[it].y)
                            radius = 5.0
                        }

                    }
                }
            }
        }

    }

    private fun VizContext.visBarChart(data: List<String>, chart: BarChart, chartWidth: Double, chartHeight: Double) {
        val diffValues = data.groupingBy { it }.eachCount()

        val xCount = diffValues.keys.size
        val yMax = diffValues.values.max()!!
        val barWidth = chartWidth / xCount


        transform {
            translate(x = margins.left, y = margins.top)
        }

        // The ordinate scale is a standard linear scale
        val yScale = scales.continuous.linear {
            domain = listOf(.0, yMax.toDouble())
            range = listOf(chartHeight, .0)
        }

        group {
            transform {
                translate(x = -10.0)
            }
            axis(Orient.LEFT, yScale)
        }

        diffValues.keys.forEachIndexed { index, s ->
            group {
                rect {
                    y = yScale(diffValues[s]!!.toDouble())
                    x = index * barWidth
                    width = barWidth - 5.0
                    height = yScale(.0) - yScale(diffValues[s]!!.toDouble())
                    fill = chart.settings.chartColor.hex().color
                }


            }
        }
    }


    private fun java.awt.Color.hex() : String =  String.format("#%02x%02x%02x", this.red, this.green, this.blue)


}

