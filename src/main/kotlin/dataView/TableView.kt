package dataView

import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.table.JBTable
import ui.VirtualFileWrapper

import javax.swing.*
import javax.swing.table.TableCellRenderer
import java.awt.*

class TableView(var file: VirtualFileWrapper, panel: JPanel) : AbstractView(file, panel) {
    private val IMAGE_PATH = "/icons/table.png"

    init {
        DATA_VIEW_ID = "Table"
        actionIcon = scaleIcon(ImageIcon(javaClass.getResource(IMAGE_PATH)))
    }

    override fun show() {
        plotPanel.removeAll()
        // 2 cause headers + data, else it will be error :(
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
            plotPanel.add(ScrollPaneFactory.createScrollPane(table))

        }
    }

}
