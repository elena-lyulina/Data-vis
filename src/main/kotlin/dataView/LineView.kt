package dataView

import com.intellij.openapi.ui.ComboBox
import io.data2viz.axis.Orient
import io.data2viz.axis.axis
import io.data2viz.color.colors
import io.data2viz.core.Point
import io.data2viz.scale.scales
import io.data2viz.viz.Margins
import io.data2viz.viz.VizContext
import io.data2viz.viz.viz
import ui.Column
import ui.VirtualFileWrapper
import javax.swing.*
import javax.swing.ImageIcon
import javafx.embed.swing.JFXPanel
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.paint.Color
import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import java.util.stream.IntStream


class LineView(var file: VirtualFileWrapper, panel: JPanel) : AbstractView(file, panel) {
    private val IMAGE_PATH = "/icons/lineChart.png"
    var points = ArrayList<Point>()
    var fxPanel = JFXPanel()

    private val X_CHOOSER_ID = "xChooser"
    private val Y_CHOOSER_ID = "yChooser"


    init {
        DATA_VIEW_ID = "Line chart"
        actionIcon = scaleIcon(ImageIcon(javaClass.getResource(IMAGE_PATH)))
        completeSettingsPanel()
        addSettings()
    }


    // todo: if there is no double values?
    override fun completeSettingsPanel() {
        val xModel = DefaultComboBoxModel<Column>()
        val yModel = DefaultComboBoxModel<Column>()
        val xChooser = ComboBox<Column>(xModel)
        val yChooser = ComboBox<Column>(yModel)

        file.columns.filter { c -> c.canBeCastedToDouble }.forEach { c -> xModel.addElement(c); yModel.addElement(c) }

        xChooser.addMyListener(X_CHOOSER_ID)
        yChooser.addMyListener(Y_CHOOSER_ID)

        mySettingsPanel.add(xChooser)
        mySettingsPanel.add(yChooser)

//        xChooser.selectedIndex = 0;
//        yChooser.selectedIndex = 0;

        val xValue = xModel.getElementAt(xChooser.selectedIndex).doubleValue
        val yValue = yModel.getElementAt(yChooser.selectedIndex).doubleValue


        IntStream.range(0, minOf(xValue.size,
                yValue.size)).forEach {
            i -> points.add(Point(xValue[i], yValue[i]));
           // println("${xValue[i]} ${yValue[i]}")
        }

        // completePlotPanel()
        // width and height of plotPanel don't fit :(

        myPlotPanel.add(fxPanel)

    }




    private fun ComboBox<Column>.addMyListener(id: String) {
        addActionListener() {
            val values = model.getElementAt(selectedIndex).doubleValue
                IntStream.range(0, minOf(points.size, values.size)).forEach { i: Int ->
                    if (id.equals(X_CHOOSER_ID))
                        points[i] = Point(values[i], points[i].y)
                    else if (id.equals(Y_CHOOSER_ID))
                        points[i] = Point(points[i].x, values[i])
                }
                completePlotPanel()
            }
    }


    // move it away to lib wrapper?
    // todo: create it
    override fun completePlotPanel() {
        myPlotPanel.repaint()
        val scene = createScene(points)
        fxPanel.repaint()
        fxPanel.scene = scene
    }

    private fun createScene(points: ArrayList<Point>): Scene {
        val root = Group()
        val scene = Scene(root, Color.ALICEBLUE)
        root.viz {
            naturalLogScale(points)
        }
        return scene
    }


    private fun VizContext.naturalLogScale(points: List<Point>) {

        val margins = Margins(100.0, 100.0, 100.0, 100.0)
        val width = myViewPanel.width - margins.hMargins
        val height = myViewPanel.height - margins.vMargins - mySettingsPanel.height

        println("$width, $height")

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
            path {
                // <- the curve is rendered with a path.
                fill = null
                stroke = colors.steelblue     // <- code completion due to typed system
                strokeWidth = 1.5

                moveTo(xScale(points[0].x), yScale(points[0].y))

                (0..(points.size - 1)).forEach {
                    lineTo(xScale(points[it].x), yScale(points[it].y))
                }
            }
        }

    }
}
