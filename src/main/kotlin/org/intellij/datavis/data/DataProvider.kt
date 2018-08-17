package org.intellij.datavis.data

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.*

/**
 * All data variables are stored here and should be adding only by using {@link org.intellij.datavis.data.DataProvider#addData} method
 */
class DataProvider {

    companion object {
        fun getInstance(project: Project): DataProvider {
            return ServiceManager.getService(project, DataProvider::class.java)
        }
    }

    val supportedFileFormats = hashMapOf(Pair("csv", ','), Pair("tsv", '\t'))

    private val dataVariables: HashMap<String, DataWrapper> = HashMap()

    private var modelToNotify: DefaultListModel<DataWrapper>? = null


    val data: List<DataWrapper>
        get() = dataVariables.values as List<DataWrapper>


    /**
     * To add data representing as string
     */
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

    /**
     * To add data representing as virtual file
     */
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

    /**
     * To make list of variables up-to-date with data provider, provider should notify model list about adding/removing variables
     */
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

    class UnsupportedSeparatorException(override val message: String = "This separator is unsupported") : RuntimeException()

    class AddingExistentElementException(override val message: String = "Data variable with such ID already exist") : RuntimeException()

    class RemovingNonexistentElementException(override val message: String = "Data variable with such ID doesn't exist") : RuntimeException()
}


