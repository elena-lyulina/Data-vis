package ui

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.openapi.actionSystem.DefaultActionGroup
import dataView.*

import javax.swing.*
import java.awt.*
import java.util.Arrays


// todo: open immediately when user clicks on data variable
internal class DataViewPanel(private val myFile: VirtualFileWrapper) : JPanel(BorderLayout()) {
    private val plotPanel: JPanel
    private val dataViewKinds: List<AbstractView>

    init {
        plotPanel = JPanel(CardLayout())
        dataViewKinds = Arrays.asList(TableView(myFile, plotPanel), BarView(myFile, plotPanel), ScatterView(myFile, plotPanel), LineView(myFile, plotPanel))
        dataViewKinds.forEach { view -> plotPanel.add(view.DATA_VIEW_ID, view.myPlotPanel) }
        //dataViewKinds[0].show()
        add(createToolbar().component, BorderLayout.NORTH)
        add(plotPanel, BorderLayout.CENTER)
    }

    // todo: figure out how to add spaces between actions!
    private fun createToolbar(): ActionToolbar {
        val toolbarGroup = DefaultActionGroup()
        dataViewKinds.forEach { view -> toolbarGroup.add(view.action) }
        return ActionManager.getInstance().createActionToolbar("DataViewActions", toolbarGroup, true)
    }
}
