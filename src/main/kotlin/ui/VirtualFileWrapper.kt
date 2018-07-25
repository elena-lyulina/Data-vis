package ui

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.vfs.VirtualFile
import com.opencsv.CSVReader
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException

data class Column @JvmOverloads constructor (var header: String, var values: MutableList<String> = ArrayList()) {
    var canBeCastedToDouble: Boolean = true
        set(isDouble: Boolean) {
            if (!isDouble) field = false
        }
}

public class VirtualFileWrapper (val myFile: VirtualFile) {
    companion object {
        private val LOG = Logger.getInstance(DataToolWindowFactory::class.java)
    }

    lateinit var headers: List<String>
    lateinit var columns: List<Column>
    var parsed: Boolean = false
    init {
        parsed = parseCSV()
    }



    // using OpenCSV
    //todo: create tests
    private fun parseCSV() : Boolean {
        var fileReader: BufferedReader? = null
        var csvReader: CSVReader? = null
        var path = myFile.path

        try {
            fileReader = BufferedReader(FileReader(path))
            csvReader = CSVReader(fileReader)

            var record: Array<String>?
            headers = csvReader.readNext().toList()
            if (headers == null)
                return false
            else {
                val size = headers!!.size
                columns = ArrayList()
                headers!!.forEach { h ->  (columns as ArrayList<Column>).add(Column(h)) }


                record = csvReader.readNext()
                while (record != null) {
                    if (record.size != size) {
                        // or add extra elements?
                        LOG.warn("Amount of columns have changed due CSV parsing")
                        return false
                    }
                    var i = 0

                    (columns as ArrayList<Column>).stream().forEach {
                        c -> c.values.add(record!![i])
                        c.canBeCastedToDouble = isDouble(record!![i])
                        i++
                    }
                    record = csvReader.readNext()
                }
            }

        } catch (e: Exception) {
            LOG.warn("Parsing CSV error")
            e.printStackTrace()
            return false
        } finally {
            try {
                fileReader!!.close()
                csvReader!!.close()
            } catch (e: IOException) {
                LOG.warn("Closing fileReader/csvParser error")
                e.printStackTrace()
                return false
            }
        }
        return true;
    }

    private fun isDouble (toCheck: String) : Boolean {
        return toCheck.toDoubleOrNull() != null
    }
}


