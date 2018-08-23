package org.intellij.datavis.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.intellij.datavis.data.DataWrapper
import org.intellij.datavis.dataView.*
import java.awt.CardLayout
import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.*
import java.awt.GridBagLayout
import javax.swing.JPanel

/**
 * Panel with toolbar of data views and cardlayout panel {@link org.intellij.datavis.ui.DataViewPanel#dataViewCardPanel} for chart drawing
 */
// todo: open immediately when user clicks on data variable
class DataViewPanel(myData: DataWrapper, private val tabbedPanel: DataViewTabbedPanel) : JPanel() {
    private val dataViewCardPanel: JPanel = JPanel(CardLayout())
    private lateinit var dataViewKinds: List<AbstractView>
    internal var currentOpenedView: AbstractView
    private val mySettingsAction: SettingsAction

    init {

        runBlocking {

            val table =  async { TableView(myData, this@DataViewPanel) }
            val bar =  async { BarView(myData, this@DataViewPanel) }
            val scatter = async { ScatterView(myData, this@DataViewPanel) }
            val line = async { LineView(myData, this@DataViewPanel) }

            dataViewKinds = mutableListOf(table.await(), bar.await(), scatter.await(), line.await())

        }

        dataViewKinds.forEach { view -> dataViewCardPanel.add(view.DATA_VIEW_ID, view.myViewPanel) }
        currentOpenedView = dataViewKinds[0]
//        viewSelected(dataViewKinds[0])
        mySettingsAction = SettingsAction()

        layout = GridBagLayout()
        val c = GridBagConstraints()

        c.fill = HORIZONTAL
        c.weightx = 0.0
        c.weighty = 0.0
        c.gridx = 0
        c.gridy = 0
        c.anchor = FIRST_LINE_START

        val toolbar = createToolbar().component
        add(toolbar, c)


        c.fill = BOTH
        c.weighty = 1.0
        c.weightx = 1.0
        c.gridx = 0
        c.gridy = 1
        add(dataViewCardPanel, c)
    }

    private fun createToolbar(): ActionToolbar {
        val toolbarGroup = DefaultActionGroup()
        toolbarGroup.addAction(mySettingsAction)
        toolbarGroup.addSeparator("Data views:")
        dataViewKinds.forEach { view -> toolbarGroup.add(viewAction(view)) }
        return ActionManager.getInstance().createActionToolbar("DataViewActions", toolbarGroup, true)
    }


    private fun viewAction(view: AbstractView) : AnAction {
        return object : AnAction(view.DATA_VIEW_ID, "Show dataFile as " + view.DATA_VIEW_ID, view.actionIcon) {
            override fun actionPerformed(e: AnActionEvent) {
                viewSelected(view)
            }
        }
    }

    /**
     * When view is selected, corresponding panel shows and tab icon changes
     */
    internal fun viewSelected(view: AbstractView) {
        val cl = dataViewCardPanel.layout as CardLayout
        cl.show(dataViewCardPanel, view.DATA_VIEW_ID)
        currentOpenedView = view
        tabbedPanel.getCurrentOpenTabInfo()!!.icon = view.actionIcon
    }

    fun update() {
        tabbedPanel.update()
    }


    private inner class SettingsAction : AnAction("Settings", "Settings", AllIcons.General.GearPlain) {
        override fun actionPerformed(e: AnActionEvent?) {
            currentOpenedView.changeVisibilityOfSettings()
            // todo: make settingsAction disable in case of not having settings?
        }

    }

}
