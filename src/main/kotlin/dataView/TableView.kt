package dataView

import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.table.JBTable
import ui.VirtualFileWrapper

import javax.swing.*
import javax.swing.table.TableCellRenderer
import java.awt.*

class TableView(var file: VirtualFileWrapper, panel: JPanel) : AbstractView(file, panel) {
    override fun completeSettingsPanel() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val IMAGE_PATH = "/icons/table.png"

    init {
        DATA_VIEW_ID = "Table"
        actionIcon = scaleIcon(ImageIcon(javaClass.getResource(IMAGE_PATH)))
        addSettings()
        completePlotPanel()
    }


    override fun completePlotPanel() {
        if (file.parsed) {
            val model = DataTableModel(file.headers, file.columns)
            // todo: headers
            val table = object : JBTable(model) {
                override fun prepareRenderer(renderer: TableCellRenderer, row: Int, column: Int): Component {
                    val component = super.prepareRenderer(renderer, row, column)
                    val rendererWidth = component.preferredSize.width
                    val tableColumn = getColumnModel().getColumn(column)

                    tableColumn.preferredWidth = Math.max(rendererWidth + intercellSpacing.width, tableColumn.preferredWidth)
                    return component
                }
            }
            table.autoResizeMode = JTable.AUTO_RESIZE_OFF
            //table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
            //JBScrollPane pane = new JBScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );
            //myPanel.add(pane);
            myViewPanel.add(ScrollPaneFactory.createScrollPane(table))
        //    myPlotPanel.add(ScrollPaneFactory.createScrollPane(table))

        }


    }

}
