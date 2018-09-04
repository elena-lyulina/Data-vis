package org.intellij.datavis.settings

import org.intellij.datavis.dataView.AbstractView
import java.awt.Color
import java.awt.Dimension


//data class?
class Settings(private val dataView: AbstractView) {

    init {
        println("settings")
        println(javaClass.classLoader)
    }

    var plotSize: Dimension = Dimension(1760, 870)

    var title: String = ""
        set(value) {
            field = value
            dataView.updatePlotPanel()
        }

    var chartColor : Color = Color(98, 150, 85)
        set(value) {
            field = value
            dataView.updatePlotPanel()
        }

    var xTitle: String = ""
        set(value) {
            field = value;
            dataView.updatePlotPanel()
        }
    var xLabels: Boolean = true
        set(value) {
            field = value;
            dataView.updatePlotPanel()
        }
    var xTicks: Boolean = true
        set(value) {
            field = value;
            dataView.updatePlotPanel()
        }
    var xLines: Boolean = true
        set(value) {
            field = value;
            dataView.updatePlotPanel()
        }


    var yTitle: String = ""
        set(value) {
            field = value;
            dataView.updatePlotPanel()
        }
    var yLabels: Boolean = true
        set(value) {
            field = value;
            dataView.updatePlotPanel()
        }
    var yTicks: Boolean = true
        set(value) {
            field = value;
            dataView.updatePlotPanel()
        }

    var yLines: Boolean = true
        set(value) {
            field = value;
            dataView.updatePlotPanel()
        }



    //change it
    val gettersAxisTable = mutableListOf<MutableList<() -> Any>>(
            mutableListOf( { "title" }, { xTitle }, { yTitle } ),
            mutableListOf( { "labels" }, { xLabels }, { yLabels } ),
            mutableListOf( { "ticks" }, { xTicks }, { yTicks } ),
            mutableListOf( { "lines" }, { xLines }, { yLines } ))

    val settersAxisTable = mutableListOf<MutableList<(a: Any) -> Unit>>(
            mutableListOf( { _ -> }, { it -> if (it is String) xTitle = it }, { it -> if (it is String) yTitle = it } ),
            mutableListOf( { _ -> }, { it -> if (it is Boolean) xLabels = it }, { it -> if (it is Boolean) yLabels = it } ),
            mutableListOf( { _ -> }, { it -> if (it is Boolean) xTicks = it }, { it -> if (it is Boolean) yTicks = it } ),
            mutableListOf( { _ -> }, { it -> if (it is Boolean) xLines = it }, { it -> if (it is Boolean) yLines = it } ))

}

