package data

import com.intellij.openapi.vfs.VirtualFile
import javax.swing.*

class DataProvider private constructor() {
    val supportedFileFormats = hashMapOf<String, Char>(Pair("csv", ','), Pair("tsv", '\t'))

    private val dataVariables: HashMap<String, DataWrapper> = HashMap()

    // private val dataVariables: MutableList<DataWrapper>
    private var modelToNotify: DefaultListModel<DataWrapper>? = null


    val data: List<DataWrapper>
        get() = dataVariables.values as List<DataWrapper>

    fun addData(id: String, name: String, data: String, separator: Char = ',') : DataWrapper {
        if (supportedFileFormats.containsValue(separator)) {
            val wrapper = DataWrapper(data, name, separator)
            add(id, wrapper)
            return wrapper
        }
        else {
            throw UnsupportedSeparatorException()
        }
    }

    fun isExist(id: String) : Boolean {
        return dataVariables.containsKey(id)
    }

    fun removeData(id : String) {
        if(isExist(id)) {
            dataVariables.remove(id)
            dataVariables[id]?.let { notifyModelAboutRemoving(it) }
        }
        else {
            throw RemovingNonexistentElementException()
        }
    }

    fun addData(file: VirtualFile, separator: Char?) : DataWrapper {
        if (supportedFileFormats.containsValue(separator)) {
            val wrapper = DataWrapper(file, separator!!)
            add(file.hashCode().toString(), wrapper)
            return wrapper
        }
        else {
            throw UnsupportedSeparatorException()
        }
    }

    internal fun setListModel(listModel: DefaultListModel<DataWrapper>) {
        modelToNotify = listModel
    }

    private fun notifyModelAboutAdding(toAdd: DataWrapper) {
        modelToNotify!!.addElement(toAdd)
    }

    private fun notifyModelAboutRemovingAll() {
        modelToNotify!!.removeAllElements()
    }

    private fun notifyModelAboutRemoving(toRemove: DataWrapper) {
        modelToNotify!!.removeElement(toRemove)
    }

    private fun add(id: String, data: DataWrapper) {
        if (!isExist(id)) {
            dataVariables[id] = data
            notifyModelAboutAdding(data)
        }
        else {
            throw AddingExistentElementException()
        }
    }

    internal fun removeAll() {
        dataVariables.clear()
        notifyModelAboutRemovingAll()
    }

    inner class UnsupportedSeparatorException(override val message: String = "This separator is unsupported") : RuntimeException()

    inner class AddingExistentElementException(override val message: String = "Data variable with such ID already exist") : RuntimeException()

    inner class RemovingNonexistentElementException(override val message: String = "Data variable with such ID doesn't exist") : RuntimeException()

    //todo : create singleton not as class but as object?
    companion object {
        @Volatile
        private var myProvider: DataProvider? = null

        internal val provider: DataProvider
            get() {
                var provider = myProvider
                if (provider == null) {
                    synchronized(DataProvider::class.java) {
                        provider = myProvider
                        if (provider == null) {
                            provider = DataProvider()
                            myProvider = provider
                        }
                    }
                }
                return provider!!
            }
    }
}


