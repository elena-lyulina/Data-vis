package org.intellij.datavis.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.tabs.TabInfo
import com.intellij.ui.tabs.impl.JBEditorTabs
import javax.swing.JPanel


class DataViewTabbedPanel {

    private lateinit var myTabs: JBEditorTabs

    internal fun init(toolWindow: ToolWindow, myProject: Project) {
        myTabs = JBEditorTabs(myProject, ActionManager.getInstance(), IdeFocusManager.findInstance(), myProject)
        myTabs.isTabDraggingEnabled = true
        
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory.createContent(myTabs, "", false)
        content.isCloseable = true
        toolWindow.contentManager.addContent(content)
    }

    internal fun addTab(name: String, panel: JPanel, parsed: Boolean) {
        val tabInfo = TabInfo(panel)
        tabInfo.text = name
        if(!parsed) tabInfo.icon = AllIcons.Actions.Help
        tabInfo.setTabLabelActions(DefaultActionGroup(CloseViewerAction(tabInfo)), ActionPlaces.UNKNOWN)
        myTabs.addTab(tabInfo)
        myTabs.select(tabInfo, true)
    }

    internal fun getCurrentOpenTabInfo(): TabInfo? {
        return myTabs.selectedInfo
    }

    fun update() {
        myTabs.revalidate()
        myTabs.repaint()
    }


    private inner class CloseViewerAction(private val myInfo: TabInfo) : AnAction("Close Viewer", "Close selected viewer", AllIcons.Actions.Close) {

        override fun actionPerformed(e: AnActionEvent) {
            myTabs.removeTab(myInfo)
        }
    }


    companion object {

        fun getInstance(project: Project): DataViewTabbedPanel {
            return ServiceManager.getService(project, DataViewTabbedPanel::class.java)
        }
    }
    
}
