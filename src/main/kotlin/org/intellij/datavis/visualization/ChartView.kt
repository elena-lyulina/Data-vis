package org.intellij.datavis.visualization

import org.intellij.datavis.settings.Settings

abstract class ChartView(val settings : Settings)

class LineChart(val xData: List<Double>, val yData: List<Double>, settings: Settings) : ChartView(settings)

class ScatterChart(val xData: List<Double>, val yData: List<Double>, settings: Settings) : ChartView(settings)

class BarChart(val data: List<String>, settings: Settings) : ChartView(settings)

