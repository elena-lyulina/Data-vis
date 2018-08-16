package org.intellij.dataVis.settings

import org.intellij.dataVis.dataView.AbstractView

class Settings (val dataView: org.intellij.dataVis.dataView.AbstractView) {
    var TITLE: String = ""
        set(value)  {
            field = value
            dataView.updatePlotPanel()
        }
}