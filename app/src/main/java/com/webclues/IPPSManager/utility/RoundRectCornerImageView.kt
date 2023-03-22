package com.webclues.IPPSManager.utility

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.util.AttributeSet
import android.widget.ImageView


@SuppressLint("AppCompatCustomView")
class RoundRectCornerImageView : ImageView {
    private val radius = 36.0f
    private var path: Path? = null
    private var rect: RectF? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
    }

    private fun init() {
        path = Path()
    }

    override protected fun onDraw(canvas: Canvas) {
        rect = RectF(0F, 0F, getWidth().toFloat(), getHeight().toFloat())
        path!!.addRoundRect(rect, radius, radius, Path.Direction.CW)
        canvas.clipPath(path!!)
        super.onDraw(canvas)
    }
}