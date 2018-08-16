package org.intellij.dataVis.visualization

import com.intellij.openapi.diagnostic.Logger
import org.intellij.dataVis.data.Column


import javax.swing.event.TableModelListener
import javax.swing.table.AbstractTableModel

class DataTableModel (private val myHeaders: List<String>, private val myColumns: List<org.intellij.dataVis.data.Column>) : AbstractTableModel() {

//    /** takes at most [DEFAULT_COLUMN_NUMBER] of columns and at most [DEFAULT_ROW_NUMBER] of rows */
//    var myHeaders = header.subList(0, minOf(DEFAULT_COLUMN_NUMBER, header.size))
//    var myColumns = columns.subList(0, minOf(DEFAULT_COLUMN_NUMBER, columns.size)).
//            map { c -> Column(c.header, c.values.subList(0, minOf(DEFAULT_ROW_NUMBER, c.values.size))) }


    companion object {
        private const val DEFAULT_COLUMN_NUMBER = 100;
        private val DEFAULT_ROW_NUMBER = 100

        private val LOG = Logger.getInstance(DataTableModel::class.java)
    }


    override fun getRowCount(): Int {
        return myColumns[0].values.size
    }

    override fun getColumnCount(): Int {
        return myHeaders.size
    }

    override fun getColumnName(i: Int): String {
        return myHeaders[i]
    }

    override fun getColumnClass(i: Int): Class<*> {
        return getValueAt(0, i).javaClass
    }

    override fun isCellEditable(i: Int, i1: Int): Boolean {
        return false
    }

    override fun getValueAt(i: Int, i1: Int): Any {
        return myColumns[i1].values[i]
    }

}
