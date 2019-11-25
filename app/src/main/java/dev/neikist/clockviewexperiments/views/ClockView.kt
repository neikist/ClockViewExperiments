package dev.neikist.clockviewexperiments.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import dev.neikist.clockviewexperiments.views.renderers.DefaultClockFaceRenderer
import dev.neikist.clockviewexperiments.views.renderers.DefaultHourClockHandRenderer
import dev.neikist.clockviewexperiments.views.renderers.DefaultMinuteClockHandRenderer
import dev.neikist.clockviewexperiments.views.renderers.contracts.ClockFaceRenderer
import dev.neikist.clockviewexperiments.views.renderers.contracts.HourClockHandRenderer
import dev.neikist.clockviewexperiments.views.renderers.contracts.MinuteClockHandRenderer

// Паддинги и маржины учитывать лень, как и добавлять кастомных атрибутов вроде цветов для разных элементов,
// или рендеринг настраивать снаружи возможность давать

class ClockView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attributeSet, defStyleAttr, defStyleRes) {

    companion object {
        private const val TAG = "ClockView"
    }


    var clockFaceRenderer: ClockFaceRenderer = DefaultClockFaceRenderer()
        set(value) {
            field = value
            value.prepare(width, height)
        }
    var hourClockHandRenderer: HourClockHandRenderer = DefaultHourClockHandRenderer()
        set(value) {
            field = value
            value.hours = hour
            value.minutes = minutes
            value.prepare(width, height)
        }
    var minuteClockHandRenderer: MinuteClockHandRenderer = DefaultMinuteClockHandRenderer()
        set(value) {
            field = value
            value.minutes = minutes
            value.prepare(width, height)
        }

    /* Возможная проблема - рассинхронизация часов и минут между вью и рендерерами, если кто то извне
     в них поменяет значения, стоит подумать над защитой. Как вариант реализовать метод вроде
     onChangeTime(ClockView), запретить изменение времени извне, только изнутри рендерера, но выглядит кривовато
     и полной защиты не гарантирует все же.
     */
    var hour: Int = 0
        set(value) {
            field = value
            hourClockHandRenderer.hours = value
            invalidate()
        }

    var minutes: Int = 0
        set(value) {
            field = value
            hourClockHandRenderer.minutes = value
            minuteClockHandRenderer.minutes = value
            invalidate()
        }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        clockFaceRenderer.prepare(w, h)
        hourClockHandRenderer.prepare(w, h)
        minuteClockHandRenderer.prepare(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        clockFaceRenderer.render(canvas)
        minuteClockHandRenderer.render(canvas)
        hourClockHandRenderer.render(canvas)
    }

}