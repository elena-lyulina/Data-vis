package org.intellij.dataVis.ui

import com.intellij.execution.ui.layout.impl.JBRunnerTabs
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.tabs.TabInfo
import com.intellij.ui.tabs.impl.JBEditorTabs
import org.intellij.dataVis.dataView.AbstractView
import javax.swing.Icon
import javax.swing.ImageIcon
import javax.swing.JPanel


// todo: tabs with remove sign
class DataViewTabbedPanel {

    // todo: remove public and create fun
    private lateinit var myTabs: JBEditorTabs

    internal fun init(toolWindow: ToolWindow, myProject: Project) {
        myTabs = JBEditorTabs(myProject, ActionManager.getInstance(), IdeFocusManager.findInstance(), myProject)
       // myTabs.setPopupGroup(DefaultActionGroup(ColoredAction()), ActionPlaces.UNKNOWN, true)
        myTabs.setTabDraggingEnabled(true)
        
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory.createContent(myTabs, "", false)
        content.isCloseable = true
        toolWindow.contentManager.addContent(content)
    }

    internal fun addTab(name: String, panel: JPanel, parsed: Boolean) {
        val tabInfo = TabInfo(panel)
        tabInfo.text = name

        val IMAGE_PATH = "/icons/table.png"
        val actionIcon: Icon
        if (parsed) {
            actionIcon = IconLoader.getIcon(IMAGE_PATH)
            //actionIcon = ImageIcon(javaClass.getResource(IMAGE_PATH))
        } else {
            actionIcon = AllIcons.Actions.Help
        }

        tabInfo.icon = actionIcon
        tabInfo.setTabLabelActions(DefaultActionGroup(CloseViewerAction(tabInfo)), ActionPlaces.UNKNOWN)
        myTabs.addTab(tabInfo)
        myTabs.select(tabInfo, true)
    }

    fun getCurrentOpenTabInfo(): TabInfo? {
        return myTabs.selectedInfo
    }

    fun update() {
        myTabs.revalidate()
        myTabs.repaint()
    }

//    fun repaint() {
//        myTabs.repaint()
//    }

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
