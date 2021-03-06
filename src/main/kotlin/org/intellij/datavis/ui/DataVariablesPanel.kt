package org.intellij.datavis.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.components.JBList
import com.intellij.ui.content.ContentFactory
import org.intellij.datavis.data.DataProvider
import org.intellij.datavis.data.DataWrapper
import org.intellij.datavis.ui.DataViewTabbedPanel.Companion.DATA_VIEWER_ID
import java.awt.BorderLayout
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.*
import javax.swing.filechooser.FileFilter


/**
 * Panel for toolwindow with data variables, contains list of loaded variables
 */
class DataVariablesPanel(private val myProject: Project) : JPanel(BorderLayout()) {
    private val dataVarList: JBList<DataWrapper>
    private val myListModel: DefaultListModel<DataWrapper>
    private val myPlotPanel: DataViewTabbedPanel = DataViewTabbedPanel.getInstance(myProject)
    private val provider: DataProvider = DataProvider.getInstance(myProject)


    init {
        add(createToolbar().component, BorderLayout.WEST)
        myListModel = DefaultListModel()
        provider.setListModel(myListModel)
        dataVarList = JBList(myListModel)
        dataVarList.cellRenderer = object : ColoredListCellRenderer<DataWrapper>() {
            override fun customizeCellRenderer(list: JList<out DataWrapper>, value: DataWrapper, index: Int, selected: Boolean, hasFocus: Boolean) {
                append(value.name)
            }
        }

        dataVarList.addMouseListener(DoubleClickMouseListener())
        dataVarList.addKeyListener(EnterPressingListener())

        //not sure do i need scroll pane or it s added automatically todo: check it
        val scrollPane = ScrollPaneFactory.createScrollPane(dataVarList);
        add(scrollPane, BorderLayout.CENTER)
    }

    fun init(toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory.createContent( this, "", false)
        content.isCloseable = false
        toolWindow.contentManager.addContent(content)
    }

    private fun createToolbar(): ActionToolbar {
        val toolbarGroup = DefaultActionGroup()
        toolbarGroup.addAll(LoadDataAction(), RemoveAllAction())
        return ActionManager.getInstance().createActionToolbar("Data variables panel", toolbarGroup, false)
    }


    /**
     * An action to load data from a local file system
     * Files filter according to {@link org.intellij.datavis.data.DataProvider#supportedFileFormats
     * After choosing, file is added to data provider
     */
    private inner class LoadDataAction internal constructor() : AnAction("Load data", "Load data", AllIcons.Actions.Download) {

        override fun actionPerformed(e: AnActionEvent) {
            dataVarList.clearSelection()

            val descriptor = FileChooserDescriptor(true, false, false, false, false, false)
                    .withFileFilter { virtualFile -> provider.supportedFileFormats.keys.contains(virtualFile.extension) }
            val virtualFile = FileChooser.chooseFile(descriptor, myProject, null) ?: return

            provider.addData(File(virtualFile.path), provider.supportedFileFormats[virtualFile.extension])
        }

    }



    /**
     * Action to remove all variables from list
     */
    private inner class RemoveAllAction internal constructor() : AnAction("Remove all", "Remove all data variables", AllIcons.Actions.CloseNew) {

        override fun actionPerformed(e: AnActionEvent) {
            provider.removeAll()
        }
    }


    /**
     * When variable is selected, new tab opens in DataView toolwindow
     * If data was parsed, it is new {@link org.intellij.datavis.ui.DataViewPanel} panel
     * Else it is {@link org.intellij.datavis.ui.DataVariablesPanel.DataNotParsedPanel} panel
     */
    private fun selectVariable() {
        if(dataVarList.selectedValue != null) {
            val selected = dataVarList.selectedValue
            val panelToAdd = if (selected.parsed) DataViewPanel(selected, myPlotPanel) else DataNotParsedPanel()

            ToolWindowManager.getInstance(myProject).getToolWindow(DATA_VIEWER_ID).show(null)

            myPlotPanel.addTab(selected.name, panelToAdd, selected.parsed)
            (panelToAdd as? DataViewPanel)?.viewSelected(panelToAdd.currentOpenedView)
            LOG.info("${selected.name} selected")
        }
    }

    private inner class DataNotParsedPanel : JPanel() {
        val MESSAGE = "Sorry, but that file can't be parsed"

        init {
            add(JLabel(MESSAGE))
        }
    }

    private fun removeVariable() {
        if (dataVarList.selectedValue != null) {
            provider.removeData(dataVarList.selectedValue.ID)
        }
    }

    private inner class EnterPressingListener : KeyAdapter() {

        override fun keyReleased(e: KeyEvent) {
            val code = e.keyCode
            when (code) {
                KeyEvent.VK_ENTER -> selectVariable()
                KeyEvent.VK_DELETE -> removeVariable()
            }
        }
    }

    private inner class DoubleClickMouseListener : MouseAdapter() {

        override fun mouseClicked(e: MouseEvent?) {
            if (e!!.clickCount == 2) {
                selectVariable()
            }
        }
    }

    companion object {
        private val LOG = Logger.getInstance(DataVariablesPanel::class.java)

        fun getInstance(project: Project): DataVariablesPanel {
            return ServiceManager.getService(project, DataVariablesPanel::class.java)
        }
    }
}