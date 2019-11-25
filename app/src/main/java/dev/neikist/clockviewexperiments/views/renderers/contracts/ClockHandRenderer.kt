package dev.neikist.clockviewexperiments.views.renderers.contracts

import android.graphics.Canvas

interface ClockHandRenderer {
    var angle: Float
    fun prepare(canvasWidth: Int, canvasHeight: Int)
    fun render(canvas: Canvas)
}