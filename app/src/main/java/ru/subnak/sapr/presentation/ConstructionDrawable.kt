package ru.subnak.sapr.presentation

import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import ru.subnak.sapr.domain.model.Construction

class ConstructionDrawable(private val construction: Construction) : Drawable() {

    private val blackPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 5F
    }

    override fun draw(canvas: Canvas) {

        for ((i, value) in construction.knotValues.withIndex()) {
            var absoluteX = 10
            val x = if (construction.knotValues[value.knotNumber - 1].x == 0) {
                absoluteX *= 5
                (construction.knotValues[value.knotNumber - 1].x + 1) * absoluteX

            } else {
                absoluteX *= 5
                construction.knotValues[value.knotNumber - 1].x * absoluteX
            }



            if (value.knotNumber < construction.knotValues.size) {
                if (value.prop) {
                    drawLeftProp(canvas, x)
                }
            } else {
                if (value.prop) {
                    drawRightProp(canvas, x)
                }
            }

            if (value.knotNumber <= construction.rodValues.size) {
                val x1 = construction.knotValues[value.knotNumber].x * absoluteX
                val square = construction.rodValues[value.knotNumber - 1].square
                val loadRunning = construction.rodValues[value.knotNumber - 1].loadRunning
                drawRod(canvas, x, x1, (square + 10) * 10)
                if (loadRunning != 0) {
                    drawRunningLoad(canvas, x, x1, loadRunning)
                }
                Log.d("kek", loadRunning.toString())
            }

            val loadConcentrated = construction.knotValues[value.knotNumber - 1].loadConcentrated
            drawConcentratedLoad(canvas, x, loadConcentrated)


        }

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
                canvas.drawLine(i.toFloat(), height, (i + 40).toFloat(), height, blackPaint)
                canvas.drawLine(
                    (i + 40).toFloat(),
                    height,
                    (i + 20).toFloat(),
                    height - 10,
                    blackPaint
                )
                canvas.drawLine(
                    (i + 40).toFloat(),
                    height,
                    (i + 20).toFloat(),
                    height + 10,
                    blackPaint
                )
            }
        } else if (load < 0) {
            val range = xl.toInt() + 80..xl2.toInt() + 20
            for (i in range step 80) {
                canvas.drawLine((i).toFloat(), height, (i - 40).toFloat(), height, blackPaint)
                canvas.drawLine(
                    (i - 40).toFloat(),
                    height,
                    (i - 20).toFloat(),
                    height - 10,
                    blackPaint
                )
                canvas.drawLine(
                    (i - 40).toFloat(),
                    height,
                    (i - 20).toFloat(),
                    height + 10,
                    blackPaint
                )
            }
        }


    }

    private fun drawConcentratedLoad(canvas: Canvas, x: Int, load: Int) {
        val xl = x.toFloat()
        val height = bounds.height() / 2.toFloat()
        if (load > 0) {
            canvas.drawLine(xl, height, xl + 100, height, blackPaint)
            canvas.drawLine(xl + 100, height, xl + 100 - 20, height - 20, blackPaint)
            canvas.drawLine(xl + 100, height, xl + 100 - 20, height + 20, blackPaint)
        } else if (load < 0) {
            canvas.drawLine(xl, height, xl - 100, height, blackPaint)
            canvas.drawLine(xl - 100, height, xl - 100 + 20, height - 20, blackPaint)
            canvas.drawLine(xl - 100, height, xl - 100 + 20, height + 20, blackPaint)
        }

    }

    private fun drawRod(canvas: Canvas, x1: Int, x2: Int, square: Int) {
        val rodRect = Rect()
        val heightTop = bounds.height() / 2 + square
        val heightBottom = bounds.height() / 2 - square
        rodRect.set(x1, heightTop, x2, heightBottom)
        canvas.drawRect(rodRect, blackPaint)
    }

    private fun drawLeftProp(canvas: Canvas, x: Int) {
        val xl = x.toFloat()
        val heightStart = bounds.height() / 2 + 200
        val heightStop = bounds.height() / 2 - 200
        canvas.drawLine(xl, heightStart.toFloat(), xl, heightStop.toFloat(), blackPaint)
        for (i in 50..400 step 50) {
            canvas.drawLine(
                xl,
                heightStart.toFloat() - i,
                xl - 20,
                heightStop.toFloat() - i + 430,
                blackPaint
            )
        }
    }

    private fun drawRightProp(canvas: Canvas, x: Int) {
        val xl = x.toFloat()
        val heightStart = bounds.height() / 2 + 200
        val heightStop = bounds.height() / 2 - 200
        canvas.drawLine(xl, heightStart.toFloat(), xl, heightStop.toFloat(), blackPaint)
        for (i in 0..350 step 50) {
            canvas.drawLine(
                xl,
                heightStop.toFloat() - i + 400,
                xl + 20,
                heightStart.toFloat() - i - 30,
                blackPaint
            )
        }
    }
}