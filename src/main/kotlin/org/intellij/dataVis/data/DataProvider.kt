package org.intellij.dataVis.data

import com.intellij.openapi.vfs.VirtualFile
import javax.swing.*

class DataProvider private constructor() {
    val supportedFileFormats = hashMapOf<String, Char>(Pair("csv", ','), Pair("tsv", '\t'))

    private val dataVariables: HashMap<String, org.intellij.dataVis.data.DataWrapper> = HashMap()

    // private val dataVariables: MutableList<DataWrapper>
    private var modelToNotify: DefaultListModel<org.intellij.dataVis.data.DataWrapper>? = null


    val data: List<org.intellij.dataVis.data.DataWrapper>
        get() = dataVariables.values as List<org.intellij.dataVis.data.DataWrapper>

    fun addData(id: String, name: String, data: String, separator: Char = ',') : org.intellij.dataVis.data.DataWrapper {
        if (supportedFileFormats.containsValue(separator)) {
            val wrapper = org.intellij.dataVis.data.DataWrapper(data, name, separator)
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

    fun addData(file: VirtualFile, separator: Char?) : org.intellij.dataVis.data.DataWrapper {
        if (supportedFileFormats.containsValue(separator)) {
            val wrapper = org.intellij.dataVis.data.DataWrapper(file, separator!!)
            add(file.hashCode().toString(), wrapper)
            return wrapper
        }
        else {
            throw UnsupportedSeparatorException()
        }
    }

    internal fun setListModel(listModel: DefaultListModel<org.intellij.dataVis.data.DataWrapper>) {
        modelToNotify = listModel
    }

    private fun notifyModelAboutAdding(toAdd: org.intellij.dataVis.data.DataWrapper) {
        modelToNotify!!.addElement(toAdd)
    }

    private fun notifyModelAboutRemovingAll() {
        modelToNotify!!.removeAllElements()
    }

    private fun notifyModelAboutRemoving(toRemove: org.intellij.dataVis.data.DataWrapper) {
        modelToNotify!!.removeElement(toRemove)
    }

    private fun add(id: String, data: org.intellij.dataVis.data.DataWrapper) {
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
        private var myProvider: org.intellij.dataVis.data.DataProvider? = null

        public val provider: org.intellij.dataVis.data.DataProvider
            get() {
                var provider = org.intellij.dataVis.data.DataProvider.Companion.myProvider
                if (provider == null) {
                    synchronized(org.intellij.dataVis.data.DataProvider::class.java) {
                        provider = org.intellij.dataVis.data.DataProvider.Companion.myProvider
                        if (provider == null) {
                            provider = org.intellij.dataVis.data.DataProvider()
                            org.intellij.dataVis.data.DataProvider.Companion.myProvider = provider
                        }
                    }
                }
                return provider!!
            }
    }
}


