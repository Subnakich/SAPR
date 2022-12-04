package ru.subnak.sapr.presentation

import android.graphics.*
import android.graphics.drawable.Drawable
import ru.subnak.sapr.domain.model.Node
import ru.subnak.sapr.domain.model.Rod
import kotlin.math.abs
import kotlin.math.roundToInt

class ConstructionDrawable(
    private val nodeValues: List<Node>,
    private val rodValues: List<Rod>,
) : Drawable() {

    private val rodPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 5F
    }

    private val blueLoadPaint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.FILL
        strokeWidth = 5F
    }

    private val redLoadPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
        strokeWidth = 5F
    }

    override fun draw(canvas: Canvas) {

        val constructionHeight = rodValues.maxOf {
            it.square
        }.roundToInt()


        val xFirst = nodeValues.first().x
        val xLast = nodeValues.last().x

        val constructionWidth = abs(xFirst - xLast).roundToInt()


        val ratioWidth = bounds.width() / constructionWidth
        val ratioHeight = bounds.height() / constructionHeight * 0.7
        var xModifier = 0
        var latestXNext: Int? = null

        nodeValues.forEachIndexed { index, node ->
            val x = ((nodeValues[index].x) * ratioWidth).toInt() + xModifier
            val x1 = if (latestXNext != null) {
                latestXNext!!
            } else if (x < 100) {
                100
            } else if (x >= bounds.width() - 100) {
                bounds.width() - 100
            } else {
                x
            }
            if (index + 1 <= rodValues.size) {
                val xNext = ((nodeValues[index + 1].x) * ratioWidth).toInt() + xModifier
                if (xNext - x1 < 100) {
                    xModifier += 100
                }
                val x2 = if (xNext < 100) {
                    100 + xModifier
                } else if (xNext >= bounds.width() - 100) {
                    bounds.width() - 100
                } else if (xNext - x1 < 100) {
                    xNext + xModifier
                } else {
                    xNext + xModifier
                }
                latestXNext = x2
                val square = (rodValues[index].square * ratioHeight).toInt()
                val loadRunning = rodValues[index].loadRunning.toInt()
                drawRod(canvas, x1, x2, square)

                if (loadRunning != 0) {
                    drawRunningLoad(canvas, x1, x2, loadRunning)
                }

                val loadConcentrated = nodeValues[index].loadConcentrated.toInt()
                drawConcentratedLoad(canvas, x1, loadConcentrated)

                if (node.prop) {
                    drawLeftProp(canvas, x1)
                }
            } else {
                if (node.prop) {
                    drawRightProp(canvas, x1)
                }

                val loadConcentrated = nodeValues[index].loadConcentrated.toInt()
                drawConcentratedLoad(canvas, x1, loadConcentrated)
            }
        }
    }

    override fun setAlpha(alpha: Int) {

    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("PixelFormat.OPAQUE", "android.graphics.PixelFormat")
    )
    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    private fun drawRunningLoad(canvas: Canvas, x: Int, x2: Int, load: Int) {
        val xl = x.toFloat()
        val xl2 = x2.toFloat()
        val height = bounds.height() / 2.toFloat()

        if (load > 0) {
            val range = xl.toInt()..xl2.toInt() - 40
            for (i in range step 80) {
                canvas.drawLine(i.toFloat(), height, (i + 40).toFloat(), height, blueLoadPaint)
                canvas.drawLine(
                    (i + 40).toFloat(),
                    height,
                    (i + 20).toFloat(),
                    height - 10,
                    blueLoadPaint
                )
                canvas.drawLine(
                    (i + 40).toFloat(),
                    height,
                    (i + 20).toFloat(),
                    height + 10,
                    blueLoadPaint
                )
            }
        } else if (load < 0) {
            val range = xl.toInt() + 80..xl2.toInt() + 20
            for (i in range step 80) {
                canvas.drawLine((i).toFloat(), height, (i - 40).toFloat(), height, redLoadPaint)
                canvas.drawLine(
                    (i - 40).toFloat(),
                    height,
                    (i - 20).toFloat(),
                    height - 10,
                    redLoadPaint
                )
                canvas.drawLine(
                    (i - 40).toFloat(),
                    height,
                    (i - 20).toFloat(),
                    height + 10,
                    redLoadPaint
                )
            }
        }


    }

    private fun drawConcentratedLoad(canvas: Canvas, x: Int, load: Int) {
        val xl = x.toFloat()
        val height = bounds.height() / 2.toFloat()
        if (load > 0) {
            canvas.drawLine(xl, height, xl + 100, height, blueLoadPaint)
            canvas.drawLine(xl + 100, height, xl + 100 - 20, height - 20, blueLoadPaint)
            canvas.drawLine(xl + 100, height, xl + 100 - 20, height + 20, blueLoadPaint)
        } else if (load < 0) {
            canvas.drawLine(xl, height, xl - 100, height, redLoadPaint)
            canvas.drawLine(xl - 100, height, xl - 100 + 20, height - 20, redLoadPaint)
            canvas.drawLine(xl - 100, height, xl - 100 + 20, height + 20, redLoadPaint)
        }

    }

    private fun drawRod(canvas: Canvas, x1: Int, x2: Int, square: Int) {
        val rodRect = Rect()
        val heightTop = bounds.height() / 2 + square / 2
        val heightBottom = bounds.height() / 2 - square / 2
        rodRect.set(x1, heightTop, x2, heightBottom)
        canvas.drawRect(rodRect, rodPaint)
    }

    private fun drawLeftProp(canvas: Canvas, x: Int) {
        val xl = x.toFloat()
        val heightStart = bounds.height() / 2 + 200
        val heightStop = bounds.height() / 2 - 200
        canvas.drawLine(xl, heightStart.toFloat(), xl, heightStop.toFloat(), rodPaint)
        for (i in 50..400 step 50) {
            canvas.drawLine(
                xl,
                heightStart.toFloat() - i,
                xl - 20,
                heightStop.toFloat() - i + 430,
                rodPaint
            )
        }
    }

    private fun drawRightProp(canvas: Canvas, x: Int) {
        val xl = x.toFloat()
        val heightStart = bounds.height() / 2 + 200
        val heightStop = bounds.height() / 2 - 200
        canvas.drawLine(xl, heightStart.toFloat(), xl, heightStop.toFloat(), rodPaint)
        for (i in 0..350 step 50) {
            canvas.drawLine(
                xl,
                heightStop.toFloat() - i + 400,
                xl + 20,
                heightStart.toFloat() - i - 30,
                rodPaint
            )
        }
    }
}