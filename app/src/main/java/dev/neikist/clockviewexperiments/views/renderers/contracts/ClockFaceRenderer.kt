package dev.neikist.clockviewexperiments.views.renderers.contracts

import android.graphics.Canvas

interface ClockFaceRenderer {
    fun prepare(canvasWidth: Int, canvasHeight: Int)
    fun render(canvas: Canvas)
}