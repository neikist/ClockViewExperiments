package dev.neikist.clockviewexperiments.views.renderers

import android.graphics.Color
import dev.neikist.clockviewexperiments.views.renderers.contracts.HourClockHandRenderer
import kotlin.math.max
import kotlin.math.min

class DefaultHourClockHandRenderer : BaseClockHandRenderer(Color.BLUE), HourClockHandRenderer {

    override var hours: Int
        get() = angle.toInt() / 30
        set(value) {
            angle = min(max(value, 0), 11) * 30 + (minutes / 60f) * 30
        }

    override var minutes: Int
        get() = (((angle - hours * 30) / 30.0) * 60).toInt()
        set(value) {
            angle = min(max(hours, 0), 11) * 30 + (value / 60f) * 30
        }

    override fun handLength(canvasSize: Int): Int {
        return canvasSize / 3
    }
}