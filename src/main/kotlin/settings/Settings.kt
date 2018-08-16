package settings

import dataView.AbstractView

class Settings (val dataView: AbstractView) {
    var TITLE: String = ""
        set(value)  {
            field = value
            dataView.updatePlotPanel()
        }
}