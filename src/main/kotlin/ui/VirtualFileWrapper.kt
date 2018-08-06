package ui

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.vfs.VirtualFile
import com.opencsv.CSVReader
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.util.stream.IntStream

data class Column @JvmOverloads constructor (var header: String, var values: MutableList<String> = ArrayList()) {
    var doubleValues = ArrayList<Double>()
    var canBeCastedToDouble: Boolean = true
    override fun toString() : String {
        return header
    }
}

class VirtualFileWrapper (val myFile: VirtualFile) {
    companion object {
        private val LOG = Logger.getInstance(DataToolWindowFactory::class.java)
    }

    lateinit var headers: List<String>
    lateinit var columns: ArrayList<Column>
    var parsed: Boolean = false
    init {
        parsed = parseCSV()
    }


    /**
     * parsing file into columns using OpenCSV lib
     */
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
            if (headers.isEmpty())
                return false
            else {
                val size = headers.size
                columns = ArrayList()


                // to replace empty headers:
                IntStream.range(0, headers.size).forEach { i: Int ->
                    if (headers[i].isEmpty()) columns.add(Column("column ${i}"))
                    else columns.add(Column(headers[i]))
                }
               // headers.forEach { h ->  (columns as ArrayList<Column>).add(Column(h)) }


                record = csvReader.readNext()
                while (record != null) {
                    if (record.size != size) {
                        // or add extra elements?
                        LOG.warn("Amount of columns has changed due CSV parsing")
                        return false
                    }
                    var i = 0

                    (columns as ArrayList<Column>).stream().forEach {
                        c -> c.values.add(record!![i])
                        val doubleOrNull = record!![i].toDoubleOrNull()
                        if (doubleOrNull == null) {
                            c.canBeCastedToDouble = false
                            c.doubleValues.clear()
                        }
                        else {
                            c.doubleValues.add(doubleOrNull)
                        }
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

}


