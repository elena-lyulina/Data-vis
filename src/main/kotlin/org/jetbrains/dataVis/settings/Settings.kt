package org.jetbrains.dataVis.settings

import org.jetbrains.dataVis.dataView.AbstractView

class Settings (val dataView: AbstractView) {
    var TITLE: String = ""
        set(value)  {
            field = value
            dataView.updatePlotPanel()
        }
}