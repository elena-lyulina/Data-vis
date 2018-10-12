package org.intellij.datavis.extensions

import com.intellij.openapi.extensions.ExtensionPointName


interface Parent {

    companion object {
        val name : ExtensionPointName<Parent> = ExtensionPointName.create("org.intellij.datavis.parent")
    }
}
