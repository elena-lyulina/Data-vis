package org.intellij.datavis.visualization


/**
 * There are two libraries for visualization: data2vis and ggplot
 * Currently only ggplot is used, but for easier switching there is a factory for them
 */
interface VisFactory {
    fun createVisualizer() : Visualizer
}


/**
 * Currently unused
 */
class Data2vizFactory : VisFactory {
    override fun createVisualizer() : Visualizer {
        return Data2VizVisualizer
    }

}

class GgplotFactory (): VisFactory {
    override fun createVisualizer() : Visualizer {
        return GgplotVisualizer
    }
}