package org.intellij.datavis.ui

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory

class DataVariablesToolWindowFactory : ToolWindowFactory, DumbAware {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        DataVariablesPanel.getInstance(project).init(toolWindow)
    }

    companion object {
        private val LOG = Logger.getInstance(DataVariablesToolWindowFactory::class.java)
    }

}
