package org.intellij.datavis.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.util.ui.JBUI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.intellij.datavis.data.DataWrapper
import org.intellij.datavis.dataView.*
import org.intellij.datavis.extensions.Parent
import org.intellij.datavis.settings.Settings
import org.intellij.datavis.visualization.Visualizer
import java.awt.*
import java.awt.GridBagConstraints.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import javax.swing.JPanel
import javax.swing.Timer

/**
 * Panel with toolbar of data views and cardlayout panel {@link org.intellij.datavis.ui.DataViewPanel#dataViewCardPanel} for chart drawing
 */
// todo: open immediately when user clicks on data variable
class DataViewPanel(myData: DataWrapper, private val tabbedPanel: DataViewTabbedPanel) : JPanel() {

    private val dataViewCardPanel: JPanel = JPanel(CardLayout())
    private lateinit var dataViewKinds: List<AbstractView>
    internal var currentOpenedView: AbstractView

    private val settingsAction = SettingsAction()
    private val saveChartAction = SaveChartAction()

    init {



// todo: extension point for abstract view
        runBlocking {

            val table =  async { TableView(myData, this@DataViewPanel) }
            val bar =  async { BarView(myData, this@DataViewPanel) }
            val scatter = async { ScatterView(myData, this@DataViewPanel) }
            val line = async { LineView(myData, this@DataViewPanel) }

            dataViewKinds = mutableListOf(table.await(), bar.await(), scatter.await(), line.await())

        }
        dataViewKinds.forEach { view -> dataViewCardPanel.add(view.DATA_VIEW_ID, view.myViewPanel) }
        currentOpenedView = dataViewKinds[0]

        layout = BorderLayout()
        val toolbar = createToolbar().component
        add(toolbar, BorderLayout.NORTH)

        add(dataViewCardPanel, BorderLayout.CENTER)
    }

    private fun createToolbar(): ActionToolbar {
        val toolbarGroup = DefaultActionGroup()
        toolbarGroup.addAction(settingsAction)
        toolbarGroup.addAction(saveChartAction)
        toolbarGroup.addSeparator("Data views:")
        dataViewKinds.forEach { view -> toolbarGroup.add(viewAction(view)) }
        return ActionManager.getInstance().createActionToolbar("DataViewActions", toolbarGroup, true)
    }


    private fun viewAction(view: AbstractView): AnAction {
        return object : AnAction(view.DATA_VIEW_ID, "Show dataFile as " + view.DATA_VIEW_ID, view.actionIcon) {
            override fun actionPerformed(e: AnActionEvent) {
                viewSelected(view)
            }
        }
    }

    /**
     * When view is selected, corresponding panel shows and tab icon changes
     */
    internal fun viewSelected(view: AbstractView) {
        val cl = dataViewCardPanel.layout as CardLayout
        cl.show(dataViewCardPanel, view.DATA_VIEW_ID)
        currentOpenedView = view
        currentOpenedView.updatePlotPanel()
        tabbedPanel.getCurrentOpenTabInfo()!!.icon = view.actionIcon
    }


    fun setEnabled(e: AnActionEvent?) {
        val presentation = e!!.presentation
        presentation.isEnabled = currentOpenedView is AbstractChartView
    }

    fun update() {
        tabbedPanel.update()
    }


    private inner class SettingsAction : AnAction("Settings", "Settings", AllIcons.General.GearPlain) {
        override fun update(e: AnActionEvent?) {
            setEnabled(e)
        }

        override fun actionPerformed(e: AnActionEvent?) {
            if (currentOpenedView is AbstractChartView) {
                (currentOpenedView as AbstractChartView).changeVisibilityOfSettings()
            }

        }
    }

    private inner class SaveChartAction : AnAction("Save", "Save chart", AllIcons.Actions.Menu_saveall) {
        override fun update(e: AnActionEvent?) {
            setEnabled(e)
        }

        override fun actionPerformed(e: AnActionEvent?) {
            if (currentOpenedView is AbstractChartView) {
                val dialog = SaveChartDialog(tabbedPanel.myProject, currentOpenedView as AbstractChartView)
                dialog.show()
                if (dialog.isOK) {
                    dialog.saveChart()
                }
            }
            // select graph size
            // save as picture?
        }
    }




}

