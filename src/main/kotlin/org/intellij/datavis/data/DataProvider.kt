package org.intellij.datavis.data

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import javax.swing.*

/**
 * All data variables are stored here and should be adding only by using {@link org.intellij.datavis.data.DataProvider#addData} method
 */
class DataProvider private constructor() {

    companion object {
        @JvmStatic fun getInstance(project: Project): DataProvider {
            return ServiceManager.getService(project, DataProvider::class.java)
        }
    }

    public val supportedFileFormats = hashMapOf(Pair("csv", ','), Pair("tsv", '\t'))

    private val dataVariables: HashMap<String, DataWrapper> = HashMap()

    private var modelToNotify: DefaultListModel<DataWrapper>? = null


    val data: List<DataWrapper>
        get() = dataVariables.values as List<DataWrapper>


    /**
     * To add data representing as string
     * @param id data id
     * @param name it will be shown on data variables panel
     * @param separator
     */
    @Throws(UnsupportedOperationException::class, AddingExistentElementException::class)
    public fun addData(id: String, name: String, data: String, separator: Char = ',') : DataWrapper {
        if (supportedFileFormats.containsValue(separator)) {
            val wrapper = DataWrapper(id, data, name, separator)
            add(id, wrapper)
            return wrapper
        }
        else {
            throw UnsupportedSeparatorException()
        }
    }

    public fun isExist(id: String) : Boolean {
        return dataVariables.containsKey(id)
    }


    public fun removeData(id : String) {
        if(isExist(id)) {
            dataVariables[id]?.let { notifyModelAboutRemoving(it) }
            dataVariables.remove(id)
        }
        else {
            throw RemovingNonexistentElementException()
        }
    }

    /**
     * To add data representing as virtual file
     * Its hashcode is used as data id
     * Its name will be shown on data variables panel
     * @param file file loaded by user
     * @param separator
     */
    @Throws(UnsupportedOperationException::class, AddingExistentElementException::class)
    public fun addData(file: File, separator: Char?) : DataWrapper {
        if (supportedFileFormats.containsValue(separator)) {
            val id = file.hashCode().toString()
            val wrapper = DataWrapper(id, file, separator!!)
            add(id, wrapper)
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

    @Throws(AddingExistentElementException::class)
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


