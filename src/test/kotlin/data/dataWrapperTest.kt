package data

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class dataWrapperTest {

    // dont know, can OpenCSV parse TSV?
    @Test
    fun parseCSVTest_tabDelimiter() {
        val path = "data/tabDelimiter.tsv"
        val wrapper = getWrapperFromFilePath(path, '\t')

        val expectedHeadersSize = 6
        val expectedColumnLength = 10

        assert(wrapper.parsed)
        assert(wrapper.headers.size == expectedHeadersSize)
        assert(wrapper.headers.size == wrapper.columns.size)
        wrapper.columns.forEach { c -> assert(c.values.size == expectedColumnLength) }
    }

    @Test
    fun parseCSV_commaDelimiter() {
        val path = "data/commaDelimiter.csv"
        val wrapper = getWrapperFromFilePath(path)

        val expectedHeadersSize = 3
        val expectedColumnLength = 2

        assert(wrapper.parsed)
        assert(wrapper.headers.size == expectedHeadersSize)
        assert(wrapper.headers.size == wrapper.columns.size)
        wrapper.columns.forEach { c -> assert(c.values.size == expectedColumnLength) }

    }

    @Test
    fun parseCSV_commaAndQuotes() {
        val path = "data/commaAndQuotes.csv"
        val wrapper = getWrapperFromFilePath(path)

        val expectedHeadersSize = 3
        val expectedColumnLength = 4

        assert(wrapper.parsed)
        assert(wrapper.headers.size == expectedHeadersSize)
        assert(wrapper.headers.size == wrapper.columns.size)
        wrapper.columns.forEach { c -> assert(c.values.size == expectedColumnLength) }
    }

    @Test
    fun parseCSVTest_emptyFile() {
        val path = "data/emptyFile.csv"
        val wrapper = getWrapperFromFilePath(path)

        assert(!wrapper.parsed)
    }

    @Test
    fun parseCSVTest_headers() {
        val path = "data/headers.csv"
        val wrapper = getWrapperFromFilePath(path)

        val expectedHeadersSize = 3
        val expectedColumnLength = 3
        val expectedHeaders = arrayListOf<String>("column0", "column1", "year")

        assert(wrapper.parsed)
        assert(wrapper.headers.size == expectedHeadersSize)
        assert(wrapper.headers.size == wrapper.columns.size)
        wrapper.columns.forEach { c -> assert(c.values.size == expectedColumnLength) }
        assertEquals(expectedHeaders, wrapper.headers)
    }

    @Test
    fun parseCSVTest_diffColAmount() {
        val path = "data/diffColAmount.csv"
        val wrapper = getWrapperFromFilePath(path)

        assert(!wrapper.parsed)
    }


    fun getWrapperFromFilePath(path: String, separator: Char = ',') : DataWrapper {
        val classLoader = javaClass.classLoader
        val file = File(classLoader.getResource(path)!!.file)
        val data = String(file.readBytes())
        return DataWrapper(data, separator = separator)
    }



}
