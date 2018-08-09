package dataView

import data.DataWrapper
import visualization.GgplotFactory
import visualization.VisFactory
import visualization.Visualizer

import javax.swing.*
import java.awt.*


internal interface View {
    val DATA_VIEW_ID: String
    val actionIcon: ImageIcon
    val hasSettings: Boolean
    fun completePlotPanel()
    fun completeSettingsPanel()
}

abstract class AbstractView internal constructor(internal var dataFile: DataWrapper) : View {
    var myViewPanel = JPanel()
    protected var mySettingsPanel = JPanel()
    protected var myPlotPanel = JPanel()

    protected val myVisFactory: VisFactory = GgplotFactory()
    protected val myVisualizer : Visualizer
    // add button to change library?

    init {
        myVisualizer = myVisFactory.createVisualizer()
        completeMyViewPanel()
    }

    // myViewPanel consists of mySettingsPanel and myPlotPanel
    private fun completeMyViewPanel() {
        myViewPanel.layout = BoxLayout(myViewPanel, BoxLayout.Y_AXIS)
        myViewPanel.add(mySettingsPanel)
        myViewPanel.add(myPlotPanel)
    }

    override fun completeSettingsPanel() {
        mySettingsPanel.isVisible = hasSettings
    }

//
//    fun completeMySettings() {
//        mySettings.layout = GridBagLayout();
//        val gc = GridBagConstraints()
//        mySettings.preferredSize = Dimension(100, 200)
//        mySettings.background = Color.BLACK
//        mySettings.add(mySettingsPanel, gc)
//
//    }

    companion object {
        fun scaleIcon(icon: ImageIcon): ImageIcon {
            val ICON_SIZE = 50
            return ImageIcon(icon.image.getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_DEFAULT))
        }
    }

    fun changeVisibilityOfSettings() {
        if (hasSettings) {
            mySettingsPanel.isVisible = !mySettingsPanel.isVisible
        }
    }

}



