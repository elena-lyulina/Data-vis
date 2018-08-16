package settings

import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JLabel
import javax.swing.JPanel
import java.awt.Graphics2D


class ChartSettingsPanel(settings: Settings) : JPanel() {
    val DEFAULT_PANEL = "defaultPanel"
    val ALL_WIDTH = 400
    val ALL_HEIGHT = 200

    val LEFT_WIDTH = 40
    val RIGHT_WIDTH = ALL_WIDTH - LEFT_WIDTH

    val mySize = Dimension(ALL_WIDTH + 10, ALL_HEIGHT + 10)
    val options = mutableListOf<AbstractOption>(
            TitleOption(5, 10, RIGHT_WIDTH, ALL_HEIGHT, settings),
            AxisOption(5, 60, RIGHT_WIDTH, ALL_HEIGHT, settings),
            LegendOption(5, 110, RIGHT_WIDTH, ALL_HEIGHT, settings),
            ColorsOption(5, 160, RIGHT_WIDTH, ALL_HEIGHT, settings))

    var leftVerticalSettingsPanel = object : JPanel() {
        init {
            addMouseListener(MyMouseListener())
            background = Color.WHITE
            preferredSize = Dimension(LEFT_WIDTH, ALL_HEIGHT)
            minimumSize = Dimension(LEFT_WIDTH, ALL_HEIGHT)
            maximumSize = Dimension(LEFT_WIDTH, ALL_HEIGHT)
        }
        override fun paintComponent(g: Graphics?) {
            super.paintComponent(g)
            val g2 = g as Graphics2D
            g.color = Color.BLACK
            options.forEach { o -> g.stroke = BasicStroke(5.0F); g.drawOval(o.xCoord, o.yCoord, o.size, o.size) }
        }
    }

    var currentOpenPanel = JPanel(CardLayout())

    var defaultPanel = object : JPanel() {
        init {
            layout = null
            preferredSize = Dimension(RIGHT_WIDTH, ALL_HEIGHT)
            maximumSize = Dimension(RIGHT_WIDTH, ALL_HEIGHT)
            minimumSize = Dimension(RIGHT_WIDTH, ALL_HEIGHT)
            add(JLabel(DEFAULT_PANEL))
            background = Color.WHITE

            options. forEach { o -> val label = JLabel(o.title); val size = label.preferredSize;
                label.setBounds(o.xCoord, o.yCoord + (o.size - size.height) / 2, size.width, size.height); add(label) }

        }
//        override fun paintComponent(g: Graphics?) {
//            super.paintComponent(g)
//            val width = 8
//            val space = 5
//            g?.color = Color.LIGHT_GRAY
//            options. forEach { o -> g?.fillRect(o.xCoord + o.label.preferredSize.width + space, o.yCoord + (o.size - width) / 2, RIGHT_WIDTH, width)}
//        }
    }
    init {
        preferredSize = mySize
        minimumSize = mySize
        maximumSize = mySize

        currentOpenPanel.add(DEFAULT_PANEL, defaultPanel)
        options.forEach { o -> currentOpenPanel.add(o.title, o.settingsPanel) }

        add(leftVerticalSettingsPanel)
        add(currentOpenPanel)
    }



    private fun addIcon() {

    }

    inner class MyMouseListener : MouseAdapter() {
        override fun mousePressed(e: MouseEvent?) {
            options.forEach {
                if (e != null) {
                    if (it.checkClicking(e.x, e.y)) {
                        println("pressed ${e.x}, ${e.y}, ${it.title}")
                        val cl = currentOpenPanel.layout as CardLayout
                        if (it.isSettingsPanelOpen) {
                            cl.show(currentOpenPanel, it.title)
                        }
                        else {
                            cl.show(currentOpenPanel, DEFAULT_PANEL)
                        }
                    }
                }
            }
        }
    }


}





