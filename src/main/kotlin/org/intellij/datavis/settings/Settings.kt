package org.intellij.datavis.settings

import org.intellij.datavis.dataView.AbstractView
import org.intellij.datavis.visualization.GgplotVisualizer
import org.intellij.datavis.visualization.Visualizer
import java.awt.Color
import java.awt.Dimension


open class Settings(private val dataView: AbstractView) : Cloneable {

    var plotSize: Dimension = Dimension(1760, 870)

    var title: String = ""

    var chartColor : Color = Color(107, 136, 150)

    var xTitle: String = ""
    var xLabels: Boolean = true
    var xTicks: Boolean = true
    var xLines: Boolean = true


    var yTitle: String = ""
    var yLabels: Boolean = true
    var yTicks: Boolean = true
    var yLines: Boolean = true

    var visualizer: Visualizer = GgplotVisualizer

    fun updateView() {
        dataView.updatePlotPanel()
    }


    public override fun clone() : Any = super.clone()

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

