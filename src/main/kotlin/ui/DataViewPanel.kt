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
    private val cardPlotPanel: JPanel
    private val dataViewKinds: List<AbstractView>

    init {
        cardPlotPanel = JPanel(CardLayout())
        dataViewKinds = Arrays.asList(TableView(myFile, cardPlotPanel), BarView(myFile, cardPlotPanel), ScatterView(myFile, cardPlotPanel), LineView(myFile, cardPlotPanel))
        dataViewKinds.forEach { view -> cardPlotPanel.add(view.DATA_VIEW_ID, view.myViewPanel) }
        add(createToolbar().component, BorderLayout.NORTH)
        add(cardPlotPanel, BorderLayout.CENTER)
    }

    // todo: figure out how to manage toolbar view
    private fun createToolbar(): ActionToolbar {
        val toolbarGroup = DefaultActionGroup()
        dataViewKinds.forEach { view -> toolbarGroup.add(view.action) }
        return ActionManager.getInstance().createActionToolbar("DataViewActions", toolbarGroup, true)
    }

}
