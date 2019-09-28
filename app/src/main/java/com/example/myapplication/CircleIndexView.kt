package com.example.myapplication

import android.content.Context
import android.graphics.*

import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.minus


//扩展出连接点的方法
fun Path.line2p(p: PointF) {
    this.lineTo(p.x, p.y)
}

fun Path.move2p(p: PointF) {
    this.moveTo(p.x, p.y)
}

fun Path.line2p(vararg p: PointF) {
    p.forEach {
        this.lineTo(it.x, it.y)
    }
}


class CircleView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val TAG = "CircleView"

    private val r = 20f
    private val strokeW = 18f

    //百分比偏移，为了计算的时候能达到最大值
    private val PERCENT_OFFSET = 5

    //圆圈离边界的距离
    private val OFFSET = 176 + r

    private var isCanMoveP0 = false
    private var isCanMoveP02 = false

    //圆心 第一个
    private var p0: PointF = PointF(0F, 0F)

    //圆心 第二个
    private var p02: PointF = PointF(0F, 0F)

    private var paintCircle: Paint
    private var paintLine: Paint
    private var paintLine2: Paint
    private var rectLine: Paint
    private val textPaint: Paint
    private val minMaxTextPaint: Paint
    private val path = Path()
    private var gWidth = 0


    //最大值的显示文案
    var maxString = "100+"
    //最小值
    var minValue = 0
    //最大值
    var maxValue = 100

    //最小值最初的位置
    var minInitPosition = 8

    //最大子最初的位置
    var maxIntPosition = 91

    //没有移动过， 为了第一次的算 标签值得时候不会出现1的误差
    var isNoMove0 = true
    var isNoMove02 = true

    init {
        paintCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.strokeWidth = strokeW
            this.style = Paint.Style.STROKE//画笔属性是空心圆
            this.isAntiAlias = true
            this.color = Color.parseColor("#FB4830")
        }

        paintLine = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.strokeWidth = 4f
            this.style = Paint.Style.STROKE//画笔属性是空心圆
            this.isAntiAlias = true
            this.color = Color.parseColor("#D8D8D8")
            this.pathEffect = CornerPathEffect(80f);
        }

        paintLine2 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.strokeWidth = 4f
            this.style = Paint.Style.STROKE//画笔属性是空心圆
            this.isAntiAlias = true
            this.color = Color.parseColor("#D8D8D8")
            this.pathEffect = CornerPathEffect(60f);
        }

        rectLine = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.strokeWidth = 6f
            this.style = Paint.Style.FILL//画笔属性是空心圆
            this.isAntiAlias = true
            this.color = Color.parseColor("#FB4830")
        }

        textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = Color.WHITE
            this.strokeWidth = 2f
            this.textSize = 16F * 2
        }

        minMaxTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = Color.parseColor("#bbbbbb")
            this.strokeWidth = 2f
            this.textSize = 16F * 2
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        //  Log.d(TAG, "onLayout  width=$width  height=$height")
        //计算一次长度

        gWidth = width

        val validLen = gWidth - OFFSET - OFFSET

        //算出第一次  第一个圈圈的位置
        p0.x = minInitPosition * validLen / (maxValue - minValue + PERCENT_OFFSET) + OFFSET

        p02.x = maxIntPosition * validLen / (maxValue - minValue + PERCENT_OFFSET) + OFFSET


        p0.y = height / 2.toFloat()
        p02.y = p0.y
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return true

        val p = PointF(event.x, event.y)

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {

                if (
                    p.x > p0.x - 2 * r
                    && p.x < p0.x + 2 * r
                    && p.y > p0.y - 2 * r
                    && p.y < p0.y + 2 * r
                ) {
                    isCanMoveP0 = true
                }

                if (
                    p.x > p02.x - 2 * r
                    && p.x < p02.x + 2 * r
                    && p.y > p02.y - 2 * r
                    && p.y < p02.y + 2 * r
                ) {
                    isCanMoveP02 = true
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (!isCanMoveP0 && !isCanMoveP02) {
                    return true
                }
                //限制滑动距离
                if (
                    p.x >= OFFSET
                    && p.x <= gWidth - OFFSET
                ) {

                    if (isCanMoveP0 && p.x < p02.x - 3.5 * r) {
                        p0.x = p.x
                        //p0.y = p.y
                        isNoMove0 = false
                        invalidate()
                    } else if (isCanMoveP02 && p.x > p0.x + 3.5 * r) {
                        p02.x = p.x
                        //  p02.y = p.y

                        isNoMove02 = false
                        invalidate()
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                isCanMoveP0 = false
                isCanMoveP02 = false
            }
        }
        return true
    }

    //    重写draw方法
    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        //     Log.d(TAG, "draw  width=$width  height=$height")

        //画最大最小值
        drawMinMaxText(canvas)

        //画两个指示器
        drawCircleIndex(canvas, p0)
        drawCircleIndex(canvas, p02)


        //得到指示器上平滑点
        val p0List = calculatePonits(p0)
        val p02List = calculatePonits(p02)

        //把各个点连接起来
        path.reset()
        //左边起点
        path.moveTo(0F, p0List[1].y)
        val path2 = Path()


        //绘制交叉点
        if (p0List[9].x > p02List[1].x) {
            //第三交叉点
            val p3 = calculate3ponit(p0List[5], p02List[5], p0List[5], p0List[1].y)
            //第一指示器
            path.line2p(p0List[1], p0List[2], p0List[3])
            path2.move2p(p0List[3])
            path2.line2p(p0List[3], p0List[4], p0List[5], p3, p02List[5], p02List[6], p02List[7])

            //第二指示器
            path.move2p(p02List[7])
            path.line2p(p02List[7], p02List[8], p02List[9])

        } else {
            //第一指示器
            path.line2p(p0List[1], p0List[2], p0List[3])
            path2.move2p(p0List[3])
            path2.line2p(p0List[3], p0List[4], p0List[5], p0List[6], p0List[7])
            path.move2p(p0List[7])
            path.line2p(p0List[7], p0List[8], p0List[9])

            //第二指示器
            path.line2p(p02List[1], p02List[2], p02List[3])
            path2.move2p(p02List[3])
            path2.line2p(p02List[3], p02List[4], p02List[5], p02List[6], p02List[7])
            path.move2p(p02List[7])
            path.line2p(p02List[7], p02List[8], p02List[9])
        }


        //右边末点
        path.line2p(PointF(width.toFloat(), p0List[1].y))

        canvas.drawPath(path, paintLine)
        canvas.drawPath(path2, paintLine2)

    }

    /**
     * 画指示器
     */
    private fun drawCircleIndex(canvas: Canvas, p: PointF) {
        //画圆圈
        canvas.drawCircle(p.x, p.y, r, paintCircle)

        //画矩形，写文字
        val textRectF = RectF(p.x - 45, p.y - 110, p.x + 45, p.y - 164)
        //画框
        canvas.drawRoundRect(textRectF, 10F, 10F, rectLine)
        //得到文字
        val text = getShowText(p)
        val textLen = textPaint.measureText(text)
        val textLeft = textRectF.left + (textRectF.width() - textLen) / 2
        val textBottom = textRectF.bottom - (textRectF.height() - textPaint.textSize) / 2
        canvas.drawText(text, textLeft, textBottom, textPaint)
    }


    private fun calculatePonits(p: PointF): List<PointF> {
        val pt1 = PointF(p.x - 56, p.y - 35)
        val pt2 = PointF(p.x - 40, p.y - 36)
        val pt3 = PointF(p.x - 32, p.y - 40)
        val pt4 = PointF(p.x - 18, p.y - 48)
        val pt5 = PointF(p.x, p.y - 52)
        val pt6 = PointF(p.x + 18, p.y - 48)
        val pt7 = PointF(p.x + 32, p.y - 40)
        val pt8 = PointF(p.x + 40, p.y - 36)
        val pt9 = PointF(p.x + 56, p.y - 35)

        return mutableListOf(PointF(), pt1, pt2, pt3, pt4, pt5, pt6, pt7, pt8, pt9)
    }

    //交叉时算出第三个点
    /**
     * pL: PointF 左边点
     * pR: PointF 右边点
     * pT: PointF 最高点
     */
    private fun calculate3ponit(pL: PointF, pR: PointF, pT: PointF, offset: Float): PointF {
        // val h1 = pL.y
        val dh = pT.y - offset
        val ds = 4 * r
        val d0 = pR.x - pL.x
        val x = d0 / 2
        val y = dh * x / ds


        // Log.d(TAG, "x= $x  y=$y")

        return PointF(pT.x + x, pT.y - y)

    }

    /**
     * 画最大最小值
     */
    private fun drawMinMaxText(canvas: Canvas) {
        canvas.drawText(minValue.toString(), OFFSET - 3 * r, p0.y, minMaxTextPaint)
        canvas.drawText(maxString, gWidth - OFFSET + 2 * r, p0.y, minMaxTextPaint)
    }

    private fun getShowText(p: PointF): String {

        if (isNoMove0 && p.x == p0.x) {
            return minInitPosition.toString()
        }

        if (isNoMove02 && p.x == p02.x) {
            return maxIntPosition.toString()
        }

        val validLen = gWidth - OFFSET - OFFSET
        //当前位置
        val currentLen = p.x - OFFSET
        if (currentLen < 0) {
            return minValue.toString()
        }

        var currentProcess =
            (currentLen * (maxValue - minValue + PERCENT_OFFSET) / validLen).toInt()
        if (currentProcess >= maxValue) {
            return maxString
        }
        return currentProcess.toString()
    }

}


