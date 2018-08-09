package ui

import com.intellij.openapi.actionSystem.*
import data.DataWrapper
import dataView.*

import javax.swing.*
import java.awt.*
import java.util.Arrays


// todo: open immediately when user clicks on data variable
class DataViewPanel(myFile: DataWrapper) : JPanel(BorderLayout()) {
    private val tabbedActionPanel: JPanel
    private val dataViewKinds: List<AbstractView>
    private var currentOpenedView: AbstractView?

    private val SETTINGS_ICON_PATH = "/icons/settings.png"
    private val mySettingsAction: SettingsAction

    init {
        tabbedActionPanel = JPanel(CardLayout())
        dataViewKinds = Arrays.asList(TableView(myFile), BarView(myFile), ScatterView(myFile), LineView(myFile))
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
