package org.intellij.dataVis.visualization

interface VisFactory {
    fun createVisualizer() : Visualizer
}

class Data2visFactory : VisFactory {
    override fun createVisualizer() : Visualizer {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

class GgplotFactory (): VisFactory {
    override fun createVisualizer() : Visualizer {
        return GgplotVisualizer.visualizer
    }
}