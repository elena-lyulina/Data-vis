package org.intellij.datavis.ui

import com.intellij.openapi.fileChooser.FileChooserFactory
import com.intellij.openapi.fileChooser.FileSaverDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import org.intellij.datavis.dataView.AbstractChartView
import org.intellij.datavis.visualization.SwingDemoUtil
import java.awt.AWTException
import java.awt.Dimension
import java.awt.Robot
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.JComponent
import javax.swing.JPanel


class SaveChartDialog(val project: Project, private val chartView: AbstractChartView) : DialogWrapper(project, false) {
    private val form = SaveChartPanel(chartView.settings.plotSize.width, chartView.settings.plotSize.height)
    private val extension = "png"

    init {
        isModal = true
        title = "Save chart as $extension"
        init()
        pack()
    }

    override fun createCenterPanel(): JComponent? {
        return form.panel
    }

    fun saveChart() {
        val descriptor = FileSaverDescriptor("Save chart", "To save chart as $extension", extension)
        val wrapper = FileChooserFactory.getInstance().createSaveFileDialog(descriptor, project).save(null, "chart.$extension")
        if (wrapper != null) {
            val size = Dimension(form.width, form.height)

            val panel = JPanel()
            val chart = chartView.createChart(chartView.settings)
            val plotSpec = chartView.drawChart(chart, panel)
            val image = SwingDemoUtil.getImageFromPlotSpec(plotSpec, size.width, size.height)

            try {
                ImageIO.write(image, extension, File(wrapper.file.path))
            } catch (e: IOException) {
                e.printStackTrace()
            }


//            val panel = chartView.myPlotPanel
//            panel.isVisible = true
//            panel.doLayout()
//
//            var image: BufferedImage? = null
//            try {
//                image = Robot().createScreenCapture(panel.bounds())
//            } catch (e1: AWTException) {
//                // TODO Auto-generated catch block
//                e1.printStackTrace()
//            }
//
//            val graphics2D = image!!.createGraphics()
//            panel.paint(graphics2D)
//            try {
//                ImageIO.write(image, extension, File(wrapper.file.path))
//            } catch (e: Exception) {
//                // TODO Auto-generated catch block
//                println("error")
//            }
//
        }

    }


//
//    fun getWidth() : Int = form.width
//    fun getHeight() : Int = form.height
}