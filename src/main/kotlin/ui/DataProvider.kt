package ui

import com.intellij.openapi.vfs.VirtualFile

import javax.swing.*
import java.util.ArrayList
import java.util.stream.Collectors

class DataProvider private constructor() {
    private val dataVariables: MutableList<VirtualFile>
    private var modelToNotify: DefaultListModel<VirtualFile>? = null

    val data: List<VirtualFile>
        get() = dataVariables

    init {
        dataVariables = ArrayList<VirtualFile>()
    }

    internal fun setListModel(listModel: DefaultListModel<VirtualFile>) {
        modelToNotify = listModel
    }

    private fun notifyModelAboutAdding(toAdd: VirtualFile) {
        modelToNotify!!.addElement(toAdd)
    }

    private fun notifyModelAboutRemoving() {
        modelToNotify!!.removeAllElements()
    }

    fun add(file: VirtualFile) {
        if (isUnique(file)) {
            dataVariables.add(file)
            notifyModelAboutAdding(file)
        }
    }

    private fun isUnique(file: VirtualFile): Boolean {
        return !dataVariables.contains(file);
    }

    internal fun removeAll() {
        dataVariables.clear()
        notifyModelAboutRemoving()
    }

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


