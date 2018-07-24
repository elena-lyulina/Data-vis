package dataView

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.vfs.VirtualFile


import javax.swing.event.TableModelListener
import javax.swing.table.AbstractTableModel
import java.io.*
import java.util.ArrayList

class DataTableModel internal constructor(dataList: List<Array<String>>) : AbstractTableModel() {
    private val myFile: VirtualFile? = null
    private val columnNames: Array<String>
    private val data: List<Array<String>>


    init {
        // supposing it has header
        columnNames = dataList[0]
        data = dataList.subList(1, minOf(DEFAULT_ROW_NUMBER, dataList.size))
    }

    override fun getRowCount(): Int {
        return data.size
    }

    override fun getColumnCount(): Int {
        return columnNames.size
    }

    override fun getColumnName(i: Int): String {
        return columnNames[i]
    }

    override fun getColumnClass(i: Int): Class<*> {
        return getValueAt(0, i).javaClass
    }

    override fun isCellEditable(i: Int, i1: Int): Boolean {
        return false
    }

    override fun getValueAt(i: Int, i1: Int): Any {
        return data[i][i1]
    }

    override fun setValueAt(o: Any?, i: Int, i1: Int) {

    }

    override fun addTableModelListener(tableModelListener: TableModelListener) {

    }

    override fun removeTableModelListener(tableModelListener: TableModelListener) {

    }

    companion object {

        private val DEFAULT_SEPARATOR = ","
        private val DEFAULT_ROW_NUMBER = 100

        private val LOG = Logger.getInstance(DataTableModel::class.java)
    }
}
