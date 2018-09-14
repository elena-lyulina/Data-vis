package org.intellij.datavis.ui

import com.intellij.openapi.fileChooser.FileChooserFactory
import com.intellij.openapi.fileChooser.FileSaverDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import org.intellij.datavis.dataView.AbstractChartView
import java.awt.AWTException
import java.awt.Robot
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JComponent


class SaveChartDialog(val project: Project, private val chartView: AbstractChartView) : DialogWrapper(project, false) {
    private val form = SaveChartPanel(chartView.settings.plotSize.width, chartView.settings.plotSize.height)
    private val extension = "jpeg"

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
        val wrapper = FileChooserFactory.getInstance().createSaveFileDialog(descriptor, project).save(project.baseDir, "chart.$extension")
        if (wrapper != null) {
//            val size = Dimension(form.width, form.height)
//
//            val panel = JPanel()
//            panel.size = size
//            panel.preferredSize = size
//            panel.maximumSize = size
//            panel.minimumSize = size
//
//            val settings = chartView.settings.copy()
//            settings.plotSize = size
//
//            println(chartView.settings.plotSize)
//            println(settings.plotSize)
//
//            val chart = chartView.createChart(settings)
//            chartView.drawChart(chart, panel)
//           // panel.components
//
//            val image = UIUtil.createImage(panel.width, panel.height, BufferedImage.TYPE_INT_ARGB)
//            val g = image.graphics
//            //panel.paint(g)
//            try {
//                ImageIO.write(image, "png", File(wrapper.file.path))
//            } catch (ex: IOException) {
//            }


            val panel = chartView.myPlotPanel
            panel.isVisible = true
            panel.doLayout()

            var image: BufferedImage? = null
            try {
                image = Robot().createScreenCapture(panel.bounds())
            } catch (e1: AWTException) {
                // TODO Auto-generated catch block
                e1.printStackTrace()
            }

            val graphics2D = image!!.createGraphics()
            panel.paint(graphics2D)
            try {
                ImageIO.write(image, extension, File(wrapper.file.path))
            } catch (e: Exception) {
                // TODO Auto-generated catch block
                println("error")
            }

        }

    }


//
//    fun getWidth() : Int = form.width
//    fun getHeight() : Int = form.height
}