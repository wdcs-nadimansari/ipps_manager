package com.webclues.IPPSManager.utility

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View.MeasureSpec
import android.widget.ImageView


@SuppressLint("AppCompatCustomView")
class ScaledImageView(context: Context?, attrs: AttributeSet?) :
    ImageView(context, attrs) {
    protected override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val d: Drawable = getDrawable()
        if (d != null) {
            val width: Int
            val height: Int
            if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
                height = MeasureSpec.getSize(heightMeasureSpec)
                width =
                    Math.ceil(height * d.intrinsicWidth.toFloat() / d.intrinsicHeight.toDouble())
                        .toInt()
            } else {
                width = MeasureSpec.getSize(widthMeasureSpec)
                height =
                    Math.ceil(width * d.intrinsicHeight.toFloat() / d.intrinsicWidth.toDouble())
                        .toInt()
            }
            setMeasuredDimension(width, height)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}