package ui

import com.intellij.openapi.vfs.VirtualFile
import java.nio.file.Files
import java.nio.file.Paths

public class VirtualFileWrapper (val myFile: VirtualFile) {
    // parsed data for all types of data view
    val parsedForTable: ArrayList<Array<String>> = ArrayList()

    init {
        parseCSV()
    }

    // just splitting todo: check Scientific mode and OpenCSV
    private fun parseCSV() {
        val stream = Files.newInputStream(Paths.get(myFile.path))
        stream.buffered().reader().use { reader ->
            reader.readLines().map { l -> parsedForTable.add(l.split(DEFAULT_SEPARATOR.toRegex()).toTypedArray()) }
        }
    }

    companion object {
        private val DEFAULT_SEPARATOR = ","
    }
}