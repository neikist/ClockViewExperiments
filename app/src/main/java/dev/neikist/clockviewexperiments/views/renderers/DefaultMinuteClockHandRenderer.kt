package dev.neikist.clockviewexperiments.views.renderers

import android.graphics.Color
import dev.neikist.clockviewexperiments.views.renderers.contracts.MinuteClockHandRenderer

class DefaultMinuteClockHandRenderer : BaseClockHandRenderer(Color.BLACK), MinuteClockHandRenderer {
    override var minutes: Int
        get() = angle.toInt() / 6
        set(value) {
            angle = value * 6f
        }
}