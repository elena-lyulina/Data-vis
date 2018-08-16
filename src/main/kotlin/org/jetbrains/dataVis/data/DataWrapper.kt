package org.jetbrains.dataVis.data

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.vfs.VirtualFile
import com.opencsv.CSVReader
import org.apache.commons.lang.StringUtils
import org.jetbrains.dataVis.ui.DataToolWindowFactory
import java.io.*
import java.util.*
import java.util.stream.IntStream


/**
 * Class for wrapping and parsing data
 * You can create it by passing your data as a String or as a VirtualFile
 * @param name that name will be showing at dataVariables panel, for virtual file it's file's name
 * @param separator by default it's comma, but it can be tab '\t' also
 */

class DataWrapper (data: String, val name: String = "Name", val separator: Char = ',') {
    companion object {
        private val LOG = Logger.getInstance(DataToolWindowFactory::class.java)
    }

    lateinit var headers: MutableList<String>
    lateinit var columns: MutableList<Column>
    var parsed: Boolean = false
    var virtualFile: VirtualFile? = null

    init {
        parsed = parseCSV(StringReader(data))
    }

    constructor (file: VirtualFile, separator: Char) : this(String(file.contentsToByteArray()), file.name, separator) {
        virtualFile = file
    }


    private fun parseCSV(file: VirtualFile) : Boolean {
        LOG.info("file type as string ${file.fileType.description}")
        return parseCSV(StringReader(String(file.contentsToByteArray())))
    }



    /**
     * parsing string into columns using OpenCSV lib
     */
    private fun parseCSV(reader: Reader) : Boolean {
        var csvReader: CSVReader? = null

        try {
            csvReader = CSVReader(reader, separator)
            var record: Array<String>?
            headers = csvReader.readNext().toMutableList()

            if (headers.isEmpty())
                return false
            else {
                columns = ArrayList()

                // to replace blank headers:
                IntStream.range(0, headers.size).forEach { i: Int ->
                    if (StringUtils.isBlank(headers[i])) headers[i] = "column${i}"
                    columns.add(Column(headers[i]))
                }

                record = csvReader.readNext()
                while (record != null) {
                    if (parseLine(record)) record = csvReader.readNext() else return false
                }
            }

        } catch (e: Exception) {
            LOG.warn("ParsingCSV Error")
            e.printStackTrace()
            return false
        } finally {
            try {
                reader.close()
                csvReader?.close()
            } catch (e: IOException) {
                LOG.warn("Closing fileReader/csvParser Error")
                e.printStackTrace()
                return false
            }
        }
        return true;
    }



    /**
     *  Parsing line and adding values to columns
     */
    private fun parseLine(line: Array<String>) : Boolean {
        if (line.size != columns.size) {
            // or add extra elements?
            LOG.warn("Amount of columns has changed due CSV parsing")
            return false
        }

        IntStream.range(0, columns.size).forEach { i -> columns[i].addValue(line[i]) }

        return true
    }



    /**
     *    If at least one header is number (doesnt contain any letters), i presume there is no headers at all
     *    and the given line is the first row of table
     */

//    private fun checkHeaders(headers: List<String>) {
//        headers.all { }
//    }


}


// todo : think about private access
data class Column constructor (val header: String, val values: MutableList<String> = ArrayList()) {
    val doubleValues = ArrayList<Double>()
    var canBeCastedToDouble: Boolean = true

    fun addValue(value: String) {
        values.add(value)

        val doubleOrNull = value.toDoubleOrNull()
        if (doubleOrNull == null) {
            canBeCastedToDouble = false
            doubleValues.clear()
        }
        else {
            doubleValues.add(doubleOrNull)
        }
    }
    override fun toString() : String {
        return header
    }
}


