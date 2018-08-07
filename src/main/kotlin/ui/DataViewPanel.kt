package ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.util.Disposer
import com.intellij.ui.tabs.JBTabsPosition
import com.intellij.ui.tabs.TabInfo
import com.intellij.ui.tabs.TabsListener
import com.intellij.ui.tabs.impl.JBEditorTabs
import com.intellij.ui.tabs.impl.JBTabsImpl
import dataView.*

import javax.swing.*
import java.awt.*
import java.util.Arrays


// todo: open immediately when user clicks on data variable
class DataViewPanel(myFile: VirtualFileWrapper) : JPanel(BorderLayout()) {
    private val tabbedActionPanel: JPanel
    private val dataViewKinds: List<AbstractView>
    private var currentOpenedView: AbstractView?

    private val SETTINGS_ICON_PATH = "/icons/settings.png"

    init {
        tabbedActionPanel = JPanel(CardLayout())
        dataViewKinds = Arrays.asList(TableView(myFile, tabbedActionPanel), BarView(myFile, tabbedActionPanel), ScatterView(myFile, tabbedActionPanel), LineView(myFile, tabbedActionPanel))
        dataViewKinds.forEach { view -> tabbedActionPanel.add(view.DATA_VIEW_ID, view.myViewPanel) }
        currentOpenedView = dataViewKinds[0]
        add(createToolbar().component, BorderLayout.NORTH)
        add(tabbedActionPanel, BorderLayout.CENTER)
    }

    // todo: figure out how to manage toolbar view
    private fun createToolbar(): ActionToolbar {
        val toolbarGroup = DefaultActionGroup()
        toolbarGroup.addAction(settingsAction())
        toolbarGroup.addSeparator("Data views:")
        dataViewKinds.forEach { view -> toolbarGroup.add(viewAction(view)) }
        return ActionManager.getInstance().createActionToolbar("DataViewActions", toolbarGroup, true)
    }

    private fun viewAction(view: AbstractView) : AnAction {
        return object : AnAction(view.DATA_VIEW_ID, "Show dataFile as " + view.DATA_VIEW_ID!!, view.actionIcon) {

            override fun actionPerformed(e: AnActionEvent) {
                val cl = tabbedActionPanel.getLayout() as CardLayout
                cl.show(tabbedActionPanel, view.DATA_VIEW_ID)
                currentOpenedView = view
            }
        }
    }
    private inner class settingsAction : AnAction("settings", "settings", AbstractView.scaleIcon(ImageIcon(javaClass.getResource(SETTINGS_ICON_PATH)))) {
       // quite stupid way to know, which dataView is open, but cardlayout doesn't allow to get this information soo
        override fun actionPerformed(e: AnActionEvent?) {
            currentOpenedView?.changeVisibilityOfSettings()
        }

    }

}
