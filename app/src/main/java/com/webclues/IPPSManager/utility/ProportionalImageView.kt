package com.webclues.IPPSManager.utility

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View.MeasureSpec
import android.widget.ImageView


@SuppressLint("AppCompatCustomView")
class ProportionalImageView : ImageView {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
    }

    protected override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val d: Drawable = getDrawable()
        if (d != null) { //   int w = MeasureSpec.getSize(widthMeasureSpec);
//  int h = w * d.getIntrinsicHeight() / d.getIntrinsicWidth();
            val h = MeasureSpec.getSize(heightMeasureSpec)
            val w = h * d.intrinsicWidth / d.intrinsicHeight
            setMeasuredDimension(w, h)
        } else super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}