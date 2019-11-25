package dev.neikist.clockviewexperiments.views.renderers

import android.graphics.*
import dev.neikist.clockviewexperiments.views.renderers.contracts.ClockHandRenderer
import kotlin.math.min

abstract class BaseClockHandRenderer(color: Int) : ClockHandRenderer {

    private val clockHandPaint = Paint().apply {
        this.color = color
        style = Paint.Style.FILL
        flags = Paint.ANTI_ALIAS_FLAG
    }
    private lateinit var handBitmap: Bitmap
    private var bitmapRotationOffset: Pair<Float, Float> = 0f to 0f

    private var canvasWidth: Int = 0
    private var canvasHeight: Int = 0

    override var angle: Float = 0f
        set(value) {
            field = value
            prepareMatrix()
        }

    private lateinit var matrix: Matrix

    override fun render(canvas: Canvas) {
        canvas.drawBitmap(handBitmap, matrix, null)
    }

    override fun prepare(canvasWidth: Int, canvasHeight: Int) {
        this.canvasWidth = canvasWidth
        this.canvasHeight = canvasHeight

        prepareBitmap()
        prepareMatrix()
    }

    private fun prepareMatrix() {
        matrix = Matrix()
        with(matrix) {
            val (dx, dy) = bitmapRotationOffset
            preTranslate(dx, dy)
            postRotate(angle + 180f)
            postTranslate(canvasWidth / 2f, canvasHeight / 2f)
        }
    }

    protected open fun handLength(canvasSize: Int): Int {
        return canvasSize / 2
    }

    protected open fun handWidth(canvasSize: Int): Int {
        return (canvasSize * 0.05f).toInt()
    }

    protected open fun prepareBitmap() {
        val canvasSize = min(canvasWidth, canvasHeight)
        val width = handWidth(canvasSize)
        val height = handLength(canvasSize)


        handBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        with(Canvas(handBitmap)) {
            val widthFloat = width.toFloat()
            val heightFloat = height.toFloat()

            bitmapRotationOffset = -widthFloat / 2 to -heightFloat * 0.2f

            val path = Path()
            with(path) {
                moveTo(widthFloat / 2f, heightFloat)
                lineTo(widthFloat, heightFloat * 0.2f)
                lineTo(widthFloat, heightFloat * 0.2f)
                lineTo(widthFloat / 2f, 0f)
                lineTo(widthFloat / 2f, 0f)
                lineTo(0f, heightFloat * 0.2f)
                lineTo(0f, heightFloat * 0.2f)
                lineTo(widthFloat / 2f, heightFloat)
            }
            drawPath(path, clockHandPaint)

        }

    }
}