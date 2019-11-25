package dev.neikist.clockviewexperiments.views.renderers

import android.graphics.Color
import dev.neikist.clockviewexperiments.views.renderers.contracts.MinuteClockHandRenderer
import kotlin.math.min

class DefaultMinuteClockHandRenderer : BaseClockHandRenderer(Color.BLACK), MinuteClockHandRenderer {
    override var minutes: Int
        get() = min(angle.toInt() / 6, 59)
        set(value) {
            angle = value * 6f
        }
}