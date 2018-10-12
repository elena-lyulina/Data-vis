package org.intellij.datavis.extensions

import com.intellij.openapi.extensions.Extensions

class Child1 : Parent {

}

class Child2 : Parent {

}

class Child3 : Parent {

}

fun main(args: Array<String>) {
    println(Extensions.getExtensions(Parent.name))
}