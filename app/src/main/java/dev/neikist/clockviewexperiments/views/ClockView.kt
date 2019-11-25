package dev.neikist.clockviewexperiments.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import dev.neikist.clockviewexperiments.views.renderers.DefaultClockFaceRenderer
import dev.neikist.clockviewexperiments.views.renderers.DefaultHourClockHandRenderer
import dev.neikist.clockviewexperiments.views.renderers.DefaultMinuteClockHandRenderer
import dev.neikist.clockviewexperiments.views.renderers.contracts.ClockFaceRenderer
import dev.neikist.clockviewexperiments.views.renderers.contracts.ClockHandRenderer
import dev.neikist.clockviewexperiments.views.renderers.contracts.HourClockHandRenderer
import dev.neikist.clockviewexperiments.views.renderers.contracts.MinuteClockHandRenderer
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.max
import kotlin.math.min

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

    var onTimeChangeListener: OnTimeChangeListener? = null

    var clockFaceRenderer: ClockFaceRenderer = DefaultClockFaceRenderer()
        set(value) {
            field = value
            value.prepare(width, height)
        }
    var hourClockHandRenderer: HourClockHandRenderer = DefaultHourClockHandRenderer()
        set(value) {
            if (nearestClockHand == field) {
                nearestClockHand = value
            }
            field = value
            value.hours = _hour
            value.minutes = _minute
            value.prepare(width, height)
        }
    var minuteClockHandRenderer: MinuteClockHandRenderer = DefaultMinuteClockHandRenderer()
        set(value) {
            if (nearestClockHand == field) {
                nearestClockHand = value
            }
            field = value
            value.minutes = _minute
            value.prepare(width, height)
        }

    /* Возможная проблема - рассинхронизация часов и минут между вью и рендерерами, если кто то извне
     в них поменяет значения, стоит подумать над защитой. Как вариант реализовать метод вроде
     onChangeTime(ClockView), запретить изменение времени извне, только изнутри рендерера, но выглядит кривовато
     и полной защиты не гарантирует все же.
     */
    private var _hour: Int = 0
    val hour: Int
        get() = _hour

    private var _minute: Int = 0
    val minute: Int
        get() = _minute

    private var nearestClockHand: ClockHandRenderer? = null

    fun setTime(hour: Int, minute: Int) {
        setTime(hour, minute, true)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        clockFaceRenderer.prepare(w, h)
        hourClockHandRenderer.prepare(w, h)
        minuteClockHandRenderer.prepare(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        clockFaceRenderer.render(canvas)
        hourClockHandRenderer.render(canvas)
        minuteClockHandRenderer.render(canvas)
    }

    // Стоило бы и MotionEvent.ACTION_POINTER_DOWN обрабатывать, но пока и так сойдет
    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        val handle = event.action != MotionEvent.ACTION_CANCEL
        Log.d(TAG, "onTouchEvent $handle")
        if (handle) {

            val x = event.x.toInt()
            val y = event.y.toInt()
            val centerX = width / 2
            val centerY = height / 2

            val angle = angleBetweenPoints(centerX to centerY, x to y)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val hourClockHandDistance =
                        distanceBetweenAngles(angle, hourClockHandRenderer.angle)

                    val minuteClockHandDistance =
                        distanceBetweenAngles(angle, minuteClockHandRenderer.angle)

                    nearestClockHand = if (minuteClockHandDistance <= hourClockHandDistance) {
                        minuteClockHandRenderer
                    } else {
                        hourClockHandRenderer
                    }
                    changeAngleNearestClockHand(angle)
                }
                MotionEvent.ACTION_MOVE -> changeAngleNearestClockHand(angle)
                MotionEvent.ACTION_UP -> {
                    nearestClockHand = null
                }
            }

        }
        return handle
    }

    private fun setTime(hour: Int, minute: Int, fromOutside: Boolean) {
        this._minute = max(0, min(minute, 59))
        this._hour = max(0, min(hour, 11))
        hourClockHandRenderer.hours = hour
        if (fromOutside) {
            // Для случая когда меняем изнутри - нужно устанавливать минуты только когда отпускаем палец
            hourClockHandRenderer.minutes = minute
        }
        minuteClockHandRenderer.minutes = minute
        hourClockHandRenderer.prepare(width, height)
        minuteClockHandRenderer.prepare(width, height)
        invalidate()
        if (!fromOutside) onTimeChangeListener?.onTimeChange(hour, minute)
    }

    private fun changeAngleNearestClockHand(newAngle: Float) {
        nearestClockHand?.angle = newAngle
        val (newHour, newMinute) = when (nearestClockHand) {
            hourClockHandRenderer -> {
                hourClockHandRenderer.hours to minute
            }
            minuteClockHandRenderer -> {
                hourClockHandRenderer.minutes = minuteClockHandRenderer.minutes
                hour to minuteClockHandRenderer.minutes
            }
            else -> hour to minute
        }
        setTime(newHour, newMinute, false)
    }

    private fun angleBetweenPoints(center: Pair<Int, Int>, point: Pair<Int, Int>): Float {
        val (x, y) = point
        val (centerX, centerY) = center

        /* тангенс угла - отношение противолежащего катета к прилежащему */
        return when {
            y == centerY && x > centerX -> 90f
            y == centerY && x <= centerX -> 270f
            x == centerX && y <= centerY -> 0f
            x == centerX && y > centerY -> 180f
            x > centerX && y < centerY -> /*первая четверть*/ 90f * atan((x - centerX).toFloat() / (centerY - y)) / (PI / 2).toFloat()
            x > centerX && y > centerY -> /*вторая четверть*/ 180f - 90f * atan((x - centerX).toFloat() / (y - centerY)) / (PI / 2).toFloat()
            x < centerX && y > centerY -> /*третья четверть*/ 180f + 90f * atan((centerX - x).toFloat() / (y - centerY)) / (PI / 2).toFloat()
            x < centerX && y < centerY -> /*четвертая четверть*/ 360f - 90f * atan((centerX - x).toFloat() / (centerY - y)) / (PI / 2).toFloat()
            else -> error("unbelievable value")
        }
    }

    private fun distanceBetweenAngles(angle1: Float, angle2: Float): Float {
        val maxAngle = max(angle1, angle2)
        val minAngle = min(angle1, angle2)

        return if (maxAngle - minAngle <= 180) {
            maxAngle - minAngle
        } else {
            minAngle + (360 - maxAngle)
        }
    }

}