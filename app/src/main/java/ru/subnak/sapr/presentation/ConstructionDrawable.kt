package ru.subnak.sapr.presentation

import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import ru.subnak.sapr.domain.model.Construction
import kotlin.math.abs
import kotlin.math.roundToInt

class ConstructionDrawable(private val construction: Construction) : Drawable() {

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

        val constructionHeight = construction.rodValues.maxOf {
            it.square
        }.roundToInt()


        val xFirst = construction.nodeValues.first().x
        val xLast = construction.nodeValues.last().x

        val constructionWidth = abs(xFirst - xLast).roundToInt()


        val ratioWidth = bounds.width() / constructionWidth
        val ratioHeight = bounds.height() / constructionHeight * 0.75

        Log.d("kek1", constructionHeight.toString())
        Log.d("kek2", constructionWidth.toString())
        Log.d("kek3", ratioWidth.toString())
        Log.d("kek4", ratioHeight.toString())

        construction.nodeValues.forEachIndexed { index, node ->
            val x = (construction.nodeValues[index].x * ratioWidth).toInt()
            val x1 = if (x < 100) {
                100
            } else if (x >= bounds.width() - 100) {
                bounds.width() - 100
            } else {
                x
            }
            if (index + 1 <= construction.rodValues.size) {
                val xNext = (construction.nodeValues[index + 1].x * ratioWidth).toInt()
                val x2 = if (xNext < 100) {
                    100
                } else if (xNext >= bounds.width() - 100) {
                    bounds.width() - 100
                } else {
                    xNext
                }
                val square = (construction.rodValues[index].square * ratioHeight).toInt()
                val loadRunning = construction.rodValues[index].loadRunning.toInt()
                drawRod(canvas, x1, x2, square)

                if (loadRunning != 0) {
                    drawRunningLoad(canvas, x1, x2, loadRunning)
                }

                val loadConcentrated = construction.nodeValues[index].loadConcentrated.toInt()
                drawConcentratedLoad(canvas, x1, loadConcentrated)

                if (node.prop) {
                    drawLeftProp(canvas, x1)
                }
            } else {
                Log.d("kek1", x1.toString())
                if (node.prop) {
                    drawRightProp(canvas, x1)
                }

                val loadConcentrated = construction.nodeValues[index].loadConcentrated.toInt()
                drawConcentratedLoad(canvas, x1, loadConcentrated)
            }
        }


//        for ((i, value) in construction.nodeValues.withIndex()) {
//            var absoluteX = 10
//            val x = if (construction.nodeValues[i].x == 0.0) {
//                absoluteX *= 5
//                ((construction.nodeValues[i].x + 1) * absoluteX).toInt()
//
//            } else {
//                absoluteX *= 5
//                (construction.nodeValues[i].x * absoluteX).toInt()
//            }
//
//
//            if (i + 1 < construction.nodeValues.size) {
//                if (value.prop) {
//                    drawLeftProp(canvas, x)
//                }
//            } else {
//                if (value.prop) {
//                    drawRightProp(canvas, x)
//                }
//            }
//
//            if (i + 1 <= construction.rodValues.size) {
//                val x1 = (construction.nodeValues[i + 1].x * absoluteX).toInt()
//                val square = construction.rodValues[i].square
//                val loadRunning = construction.rodValues[i].loadRunning
//                drawRod(canvas, x, x1, (square.toInt() + 10) * 10)
//                if (loadRunning.toInt() != 0) {
//                    drawRunningLoad(canvas, x, x1, loadRunning.toInt())
//                }
//                Log.d("kek", loadRunning.toString())
//            }
//
//            val loadConcentrated = construction.nodeValues[vi].loadConcentrated
//            drawConcentratedLoad(canvas, x, loadConcentrated.toInt())
//
//
//        }

    }

    override fun setAlpha(alpha: Int) {

    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }

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