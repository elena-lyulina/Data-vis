package org.intellij.datavis.visualization

import org.intellij.datavis.settings.Settings

abstract class Chart(val settings : Settings)

class LineChart(val xData: List<Double>, val yData: List<Double>, settings: Settings) : Chart(settings)

class ScatterChart(val xData: List<Double>, val yData: List<Double>, settings: Settings) : Chart(settings)

class BarChart(val data: List<String>, settings: Settings) : Chart(settings)

