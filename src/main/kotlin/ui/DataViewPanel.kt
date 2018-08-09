package ui

import com.intellij.openapi.actionSystem.*
import data.DataWrapper
import dataView.*
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking

import javax.swing.*
import java.awt.*


// todo: open immediately when user clicks on data variable
class DataViewPanel(myData: DataWrapper) : JPanel(BorderLayout()) {
    private val tabbedActionPanel: JPanel
    private lateinit var dataViewKinds: List<AbstractView>
    private var currentOpenedView: AbstractView?

    private val SETTINGS_ICON_PATH = "/icons/settings.png"
    private val mySettingsAction: SettingsAction

    init {
        tabbedActionPanel = JPanel(CardLayout())

        runBlocking {

            val table =  async { TableView(myData) }
            val bar =  async { BarView(myData) }
            val scatter = async { ScatterView(myData) }
            val line = async { LineView(myData) }

            dataViewKinds = mutableListOf(table.await(), bar.await(), scatter.await(), line.await())

        }

        dataViewKinds.forEach { view -> tabbedActionPanel.add(view.DATA_VIEW_ID, view.myViewPanel) }
        currentOpenedView = dataViewKinds[0]
        mySettingsAction = SettingsAction()
        add(createToolbar().component, BorderLayout.NORTH)
        add(tabbedActionPanel, BorderLayout.CENTER)
    }

    // todo: figure out how to manage toolbar view
    private fun createToolbar(): ActionToolbar {
        val toolbarGroup = DefaultActionGroup()
        toolbarGroup.addAction(mySettingsAction)
        toolbarGroup.addSeparator("Data views:")
        dataViewKinds.forEach { view -> toolbarGroup.add(viewAction(view)) }
        return ActionManager.getInstance().createActionToolbar("DataViewActions", toolbarGroup, true)
    }

    private fun viewAction(view: AbstractView) : AnAction {
        return object : AnAction(view.DATA_VIEW_ID, "Show dataFile as " + view.DATA_VIEW_ID, view.actionIcon) {

            override fun actionPerformed(e: AnActionEvent) {
                val cl = tabbedActionPanel.getLayout() as CardLayout
                cl.show(tabbedActionPanel, view.DATA_VIEW_ID)
                currentOpenedView = view

            }
        }
    }


    private inner class SettingsAction : AnAction("Settings", "Settings", AbstractView.scaleIcon(ImageIcon(javaClass.getResource(SETTINGS_ICON_PATH)))) {
        // quite stupid way to know, which dataView is open, but cardlayout doesn't allow to get this information soo
        override fun actionPerformed(e: AnActionEvent?) {
            currentOpenedView?.changeVisibilityOfSettings()
            // todo: make settingsAction disable in case of not having settings?
        }

    }

}
