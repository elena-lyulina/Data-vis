package org.intellij.datavis.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.extensions.Extensions
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.tabs.TabInfo
import com.intellij.ui.tabs.impl.JBEditorTabs
import org.intellij.datavis.extensions.Parent
import java.security.cert.Extension
import javax.swing.JPanel


class DataViewTabbedPanel(val myProject: Project) {

    private var myTabs: JBEditorTabs = JBEditorTabs(myProject, ActionManager.getInstance(), IdeFocusManager.findInstance(), myProject)

    init {
        myTabs.isTabDraggingEnabled = true
    }

    internal fun init(toolWindow: ToolWindow) {


        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory.createContent(myTabs, "", false)
        content.isCloseable = true
        toolWindow.contentManager.addContent(content)
    }

//
//    fun show(myProject: Project) {
//        if (ToolWindowManager.getInstance(myProject).getToolWindow(DATA_VIEWER_ID) != null) {
//            showInToolwindow(value)
//        } else {
//            ApplicationManager.getApplication().invokeLater {
//                val dialog = PyDataViewDialog(myProject, value)
//                dialog.show()
//            }
//        }
//    }
//
//    private fun showInToolwindow(value: PyDebugValue) {
//        val window = ToolWindowManager.getInstance(myProject).getToolWindow(DATA_VIEWER_ID)
//        if (window == null) {
//            LOG.error("Tool window '$DATA_VIEWER_ID' is not found")
//            return
//        }
//        window.contentManager.getReady(this).doWhenDone {
//            val selectedInfo = addTab(value.getFrameAccessor())
//            val dataViewerPanel = selectedInfo.getComponent() as PyDataViewerPanel
//            dataViewerPanel.apply(value)
//        }
//        window.show(null)
//        val dataView = window.contentManager.getContent(0)
//        if (dataView != null) {
//            window.contentManager.setSelectedContent(dataView)
//        }
//    }

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
        val DATA_VIEWER_ID = "Data Vis"

        fun getInstance(project: Project): DataViewTabbedPanel {
            return ServiceManager.getService(project, DataViewTabbedPanel::class.java)
        }
    }
    
}
