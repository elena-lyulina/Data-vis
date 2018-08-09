package ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.components.JBList
import com.intellij.ui.content.ContentFactory
import data.DataProvider
import data.DataWrapper

import javax.swing.*
import java.awt.*
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

class DataVariablesPanel(private val myProject: Project) : JPanel(BorderLayout()) {
    private val dataVarList: JBList<DataWrapper>
    private val myListModel: DefaultListModel<DataWrapper>
    private val myPlotPanel: DataViewTabbedPanel
    private val provider: DataProvider


    init {
        provider = DataProvider.provider
        myPlotPanel = DataViewTabbedPanel.getInstance(myProject)
        this.add(createToolbar().component, BorderLayout.WEST)
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
        this.add(ScrollPaneFactory.createScrollPane(dataVarList), BorderLayout.CENTER)
    }

    fun init(toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory.createContent(this, "", false)
        content.isCloseable = false
        toolWindow.contentManager.addContent(content)
    }

    private fun createToolbar(): ActionToolbar {
        val toolbarGroup = DefaultActionGroup()
        toolbarGroup.addAll(LoadDataAction(), RemoveAllAction())
        return ActionManager.getInstance().createActionToolbar("Data variables panel", toolbarGroup, false)
    }

    //    private class MyListModel extends AbstractListModel<DataWrapper> {
    //        DataProvider provider = DataProvider.getProvider();
    //        @Override
    //        public int getSize() {
    //            return provider.getData().size();
    //        }
    //
    //        @Override
    //        public DataWrapper getElementAt(int i) {
    //            return provider.getData().get(i);
    //        }
    //    }


    private inner class LoadDataAction internal constructor() : AnAction("Load data", "Load data", AllIcons.Actions.Download) {

        override fun actionPerformed(e: AnActionEvent) {
            val descriptor = FileChooserDescriptor(true, false, false, false, false, false)
                    .withFileFilter { virtualFile -> provider.supportedFileFormats.keys.contains(virtualFile.extension) }
            val virtualFile= FileChooser.chooseFile(descriptor, myProject, null) ?: return

            provider.addData(virtualFile, provider.supportedFileFormats[virtualFile.extension])
        }
    }

    private inner class RemoveAllAction internal constructor() : AnAction("Remove all", "Remove all data variables", AllIcons.Actions.CloseNew) {

        override fun actionPerformed(e: AnActionEvent) {
            provider.removeAll()
        }
    }


    private fun selectVariable() {
        val selected = dataVarList.selectedValue
        myPlotPanel.addTab(selected.name, DataViewPanel(selected))
        val size = myPlotPanel.tabCount
        myPlotPanel.selectedIndex = size - 1

        LOG.info("${selected.name} selected")
    }


    private inner class EnterPressingListener : KeyAdapter() {

        override fun keyReleased(e: KeyEvent) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                selectVariable()
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