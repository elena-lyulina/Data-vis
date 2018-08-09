package data

import com.intellij.openapi.vfs.VirtualFile
import javax.swing.*
import java.util.ArrayList

class DataProvider private constructor() {
    val supportedFileFormats = hashMapOf<String, Char>(Pair("csv", ','), Pair("tsv", '\t'))

    private val dataVariables: MutableList<DataWrapper>
    private var modelToNotify: DefaultListModel<DataWrapper>? = null


    val data: List<DataWrapper>
        get() = dataVariables

    init {
        dataVariables = ArrayList<DataWrapper>()
    }

    fun addData(data: String, name: String = "Name", separator: Char = ',') : DataWrapper {
        val wrapper = DataWrapper(data, name, separator)
        add(wrapper)
        return wrapper
    }

    fun addData(file: VirtualFile, separator: Char?) : DataWrapper {
        val wrapper = DataWrapper(file, separator!!)
        add(wrapper)
        return wrapper
    }

    internal fun setListModel(listModel: DefaultListModel<DataWrapper>) {
        modelToNotify = listModel
    }

    private fun notifyModelAboutAdding(toAdd: DataWrapper) {
        modelToNotify!!.addElement(toAdd)
    }

    private fun notifyModelAboutRemoving() {
        modelToNotify!!.removeAllElements()
    }


    private fun add(data: DataWrapper) {
        if (isUnique(data)) {
            dataVariables.add(data)
            notifyModelAboutAdding(data)
        }
    }


    // i can check uniqueness only if DataWrapper was created from VirtualFile
    private fun isUnique(data: DataWrapper): Boolean {
        return data.virtualFile == null || !dataVariables
                .filter { dv -> dv.virtualFile != null }
                .map { dv -> dv.virtualFile }
                .contains(data.virtualFile)
    }

    internal fun removeAll() {
        dataVariables.clear()
        notifyModelAboutRemoving()
    }

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


