package dev.neikist.clockviewexperiments.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
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

    private val clockFaceRenderer = ClockFaceRenderer()
    private val hourClockHandRenderer = HourClockHandRenderer(Color.BLUE)
    private val minuteClockHandRenderer = MinuteClockHandRenderer(Color.BLACK)

    var hour: Int
        set(value) {
            hourClockHandRenderer.hours = value
            invalidate()
        }
        get() = hourClockHandRenderer.hours

    var minutes: Int
        set(value) {
            hourClockHandRenderer.minutes = value
            minuteClockHandRenderer.minutes = value
            invalidate()
        }
        get() = minuteClockHandRenderer.minutes

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

    abstract class ClockHandRenderer(color: Int) {

        private val clockHandPaint = Paint().apply {
            this.color = color
            style = Paint.Style.FILL
            flags = Paint.ANTI_ALIAS_FLAG
        }
        private lateinit var handBitmap: Bitmap
        private var bitmapRotationOffset: Pair<Float, Float> = 0f to 0f

        private var canvasWidth: Int = 0
        private var canvasHeight: Int = 0

        var angle: Float = 0f
            set(value) {
                field = value
                prepareMatrix()
            }

        private lateinit var matrix: Matrix

        fun render(canvas: Canvas) {
            canvas.drawBitmap(handBitmap, matrix, null)
        }

        open fun prepare(canvasWidth: Int, canvasHeight: Int) {
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

    class HourClockHandRenderer(color: Int) : ClockHandRenderer(color) {
        var hours: Int
            get() = angle.toInt() / 30
            set(value) {
                angle = min(max(value, 0), 11) * 30 + (minutes / 60f) * 30
            }

        var minutes: Int
            get() = (((angle - hours * 30) / 30.0) * 60).toInt()
            set(value) {
                angle = min(max(hours, 0), 11) * 30 + (value / 60f) * 30
            }

        override fun handLength(canvasSize: Int): Int {
            return canvasSize / 3
        }
    }

    class MinuteClockHandRenderer(color: Int) : ClockHandRenderer(color) {
        var minutes: Int
            get() = angle.toInt() / 6
            set(value) {
                angle = value * 6f
            }
    }

    class ClockFaceRenderer {

        private lateinit var bitmap: Bitmap
        private var paint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            flags = Paint.ANTI_ALIAS_FLAG
        }

        private var canvasWidth: Int = 0
        private var canvasHeight: Int = 0

        fun render(canvas: Canvas) {
            val canvasSize = min(canvasHeight, canvasWidth)

            canvas.drawBitmap(
                bitmap,
                (canvasWidth - canvasSize) / 2f,
                (canvasHeight - canvasSize) / 2f,
                null
            )
        }

        fun prepare(canvasWidth: Int, canvasHeight: Int) {
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

}