package ui

import com.intellij.openapi.vfs.VirtualFile

import javax.swing.*
import java.util.ArrayList
import java.util.stream.Collectors

class DataProvider private constructor() {
    private val dataVariables: MutableList<VirtualFileWrapper>
    private var modelToNotify: DefaultListModel<VirtualFileWrapper>? = null

    val data: List<VirtualFileWrapper>
        get() = dataVariables

    init {
        dataVariables = ArrayList<VirtualFileWrapper>()
    }

    internal fun setListModel(listModel: DefaultListModel<VirtualFileWrapper>) {
        modelToNotify = listModel
    }

    private fun notifyModelAboutAdding(toAdd: VirtualFileWrapper) {
        modelToNotify!!.addElement(toAdd)
    }

    private fun notifyModelAboutRemoving() {
        modelToNotify!!.removeAllElements()
    }


    fun add(file: VirtualFileWrapper) {
        if (isUnique(file)) {
            dataVariables.add(file)
            notifyModelAboutAdding(file)
        }
    }

    private fun isUnique(file: VirtualFileWrapper): Boolean {
        return !dataVariables.map { dv -> dv.myFile }.contains(file.myFile)
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


