package dev.neikist.clockviewexperiments.views.renderers

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import dev.neikist.clockviewexperiments.views.renderers.contracts.ClockFaceRenderer
import kotlin.math.min

class DefaultClockFaceRenderer : ClockFaceRenderer {
    private lateinit var bitmap: Bitmap
    private var paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        flags = Paint.ANTI_ALIAS_FLAG
    }

    private var canvasWidth: Int = 0
    private var canvasHeight: Int = 0

    override fun render(canvas: Canvas) {
        val canvasSize = min(canvasHeight, canvasWidth)

        canvas.drawBitmap(
            bitmap,
            (canvasWidth - canvasSize) / 2f,
            (canvasHeight - canvasSize) / 2f,
            null
        )
    }

    override fun prepare(canvasWidth: Int, canvasHeight: Int) {
        this.canvasWidth = canvasWidth
        this.canvasHeight = canvasHeight
        prepareBitmap()
    }

    private fun prepareBitmap() {
        val size = min(canvasWidth, canvasHeight)
        val strokeWidth = size * 0.05f
        val halsStroke = strokeWidth / 2
        paint.strokeWidth = strokeWidth
        bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        with(Canvas(bitmap)) {
            for (minute in 0..60) {
                val angle = -1.5f + minute * 6f
                drawArc(
                    halsStroke,
                    halsStroke,
                    size.toFloat() - halsStroke,
                    size.toFloat() - halsStroke,
                    angle,
                    3f,
                    false,
                    paint
                )
            }
        }
    }
}