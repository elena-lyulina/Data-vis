package ui

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.content.ContentFactory


// todo: tabs with remove sign
class DataViewTabbedPanel : JBTabbedPane() {

    internal fun init(toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory.createContent(this, "", false)
        content.isCloseable = false
        toolWindow.contentManager.addContent(content)
    }

    companion object {

        fun getInstance(project: Project): DataViewTabbedPanel {
            return ServiceManager.getService(project, DataViewTabbedPanel::class.java)
        }
    }

}
