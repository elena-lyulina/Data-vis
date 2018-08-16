package org.intellij.dataVis.ui

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory

class DataToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        org.intellij.dataVis.ui.DataVariablesPanel.Companion.getInstance(project).init(toolWindow)
    }

    companion object {
        private val LOG = Logger.getInstance(org.intellij.dataVis.ui.DataToolWindowFactory::class.java)
    }

}
