package org.intellij.datavis.settings

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBList
import com.intellij.util.ui.JBInsets
import com.intellij.util.ui.JBUI
import org.intellij.datavis.dataView.AbstractView
import java.awt.BorderLayout
import java.awt.CardLayout
import java.awt.Font
import javax.swing.*
import javax.swing.border.EmptyBorder


interface SettingsOption {
    val OPTION_ID: String
    val panel: JPanel
}

class ChartSettingsPanel(val settings: Settings) : JPanel() {

    private val settingsOptions = arrayOf(AxisPanel(settings), TitlePanel(settings), ColorPanel(settings), LegendPanel(settings), LibPanel(settings))
    private val listPanel = JPanel()
    private val cardPanel = JPanel(CardLayout())
    private val optionListModel: DefaultListModel<SettingsOption> = DefaultListModel()
    private val optionList = JBList(optionListModel)

    init {
        border = BorderFactory.createLineBorder(JBColor.DARK_GRAY, 1)

        listPanel.background = JBUI.CurrentTheme.ToolWindow.headerActiveBackground()
        listPanel.add(optionList)

        settingsOptions.forEach { o -> optionListModel.addElement(o); cardPanel.add(o.OPTION_ID, o.panel) }
        optionList.background = optionList.parent.background
        optionList.cellRenderer = MyCellRenderer()
        optionList.addListSelectionListener {
            val layout = cardPanel.layout as CardLayout
            layout.show(cardPanel, optionList.selectedValue.OPTION_ID)
        }

        layout = BorderLayout()
        add(listPanel, BorderLayout.WEST)
        add(cardPanel, BorderLayout.CENTER)


    }


    internal inner class MyCellRenderer : DefaultListCellRenderer() {
        override fun getListCellRendererComponent(list: JList<*>, value: Any?, index: Int, isSelected: Boolean, cellHasFocus: Boolean): JComponent {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
            if (value is SettingsOption) {
                font = font.deriveFont(Font.BOLD)
                text = value.OPTION_ID
                border = EmptyBorder(JBInsets(2, 2, 2, 5))
            }
            return this
        }
    }


}