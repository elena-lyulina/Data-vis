package ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.components.JBList
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory

import javax.swing.*
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.Objects

class DataVariablesPanel(private val myProject: Project) : JPanel(BorderLayout()) {
    private val dataVarList: JBList<VirtualFileWrapper>
    private val myListModel: DefaultListModel<VirtualFileWrapper>
    private val myPlotPanel: DataViewTabbedPanel
    private val provider: DataProvider

    init {
        provider = DataProvider.provider
        myPlotPanel = DataViewTabbedPanel.getInstance(myProject)
        this.add(createToolbar().component, BorderLayout.WEST)
        myListModel = DefaultListModel()
        provider.setListModel(myListModel)
        dataVarList = JBList(myListModel)
        dataVarList.cellRenderer = object : ColoredListCellRenderer<VirtualFileWrapper>() {
            override fun customizeCellRenderer(list: JList<out VirtualFileWrapper>, value: VirtualFileWrapper, index: Int, selected: Boolean, hasFocus: Boolean) {
                append(value.myFile.name)
            }
        }

        dataVarList.addMouseListener(DoubleClickMouseListener())
        //not sure do i need scroll pane or it s added automatically todo: check it
        this.add(ScrollPaneFactory.createScrollPane(dataVarList), BorderLayout.CENTER)
    }

    fun init(toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory.createContent(this, "Data variables", false)
        content.isCloseable = false
        toolWindow.contentManager.addContent(content)
    }

    private fun createToolbar(): ActionToolbar {
        val toolbarGroup = DefaultActionGroup()
        toolbarGroup.addAll(LoadDataAction(), RemoveAllAction())
        return ActionManager.getInstance().createActionToolbar("Data variables panel", toolbarGroup, false)
    }

    //    private class MyListModel extends AbstractListModel<VirtualFileWrapper> {
    //        DataProvider provider = DataProvider.getProvider();
    //        @Override
    //        public int getSize() {
    //            return provider.getData().size();
    //        }
    //
    //        @Override
    //        public VirtualFileWrapper getElementAt(int i) {
    //            return provider.getData().get(i);
    //        }
    //    }


    private inner class LoadDataAction internal constructor() : AnAction("Load data", "Load data", AllIcons.Actions.Download) {

        override fun actionPerformed(e: AnActionEvent) {
            println("nautilus s opening")
            val descriptor = FileChooserDescriptor(true, false, false, false, false, false)
                    .withFileFilter { virtualFile -> virtualFile.extension == "csv" }
            val virtualFile= FileChooser.chooseFile(descriptor, myProject, null) ?: return
            provider.add(VirtualFileWrapper(virtualFile))
        }
    }

    private inner class RemoveAllAction internal constructor() : AnAction("Remove all", "Remove all data variables", AllIcons.Actions.CloseNew) {

        override fun actionPerformed(e: AnActionEvent) {
            provider.removeAll()
        }
    }

    private inner class DoubleClickMouseListener : MouseAdapter() {
        override fun mouseClicked(e: MouseEvent?) {
            if (e!!.clickCount == 2) {
                val selected = dataVarList.selectedValue
                myPlotPanel.addTab(selected.myFile.name, DataViewPanel(selected))
                val size = myPlotPanel.tabCount
                myPlotPanel.selectedIndex = size - 1
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