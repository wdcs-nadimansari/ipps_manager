package com.webclues.IPPSManager.utility

import android.graphics.*
import com.squareup.picasso.Picasso
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth


class RoundedTransformationPicasso
/**
 * Creates rounded transformation for all corners.
 *
 * @param radius radius is corner radii in dp
 * @param margin margin is the board in dp
 */
    (
    private val radius: Int  // dp
    , private val margin: Int  // dp
) : com.squareup.picasso.Transformation {
    private val KEY: String
    private var topCorners = true
    private var bottomCorners = false

    init {
        KEY = "rounded_$radius$margin"
    }


    /**
     * Creates rounded transformation for top or bottom corners.
     *
     * @param radius radius is corner radii in dp
     * @param margin margin is the board in dp
     * @param topCornersOnly Rounded corner for top corners only.
     * @param bottomCornersOnly Rounded corner for bottom corners only.
     */
    constructor(
        radius: Int, margin: Int, topCornersOnly: Boolean,
        bottomCornersOnly: Boolean
    ) : this(radius, margin) {
        topCorners = topCornersOnly
        bottomCorners = bottomCornersOnly
    }

    override fun transform(source: Bitmap): Bitmap {
        val paint = Paint()
        paint.setAntiAlias(true)
        paint.setShader(BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP))

        val output = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        if (topCorners && bottomCorners) {
            // Uses native method to draw symmetric rounded corners
            canvas.drawRoundRect(
                RectF(
                    margin.toFloat(), margin.toFloat(), (source.width - margin).toFloat(),
                    (source.height - margin).toFloat()
                ), radius.toFloat(), radius.toFloat(), paint
            )
        } else {
            // Uses custom path to generate rounded corner individually
            canvas.drawPath(
                RoundedRect(
                    margin.toFloat(),
                    margin.toFloat(),
                    (source.width - margin).toFloat(),
                    (source.height - margin).toFloat(),
                    radius.toFloat(),
                    radius.toFloat(),
                    topCorners,
                    false,
                    false,
                    true
                ), paint
            )
        }


        if (source != output) {
            source.recycle()
        }

        return output
    }

    override fun key(): String {
        return KEY
    }

    companion object {

        /**
         * Prepares a path for rounded corner selectively.
         *
         *
         * Source taken from http://stackoverflow.com/a/35668889/6635889 <br></br>
         * Usage:
         * <pre>
         * Path path = RoundedRect(0, 0, fwidth , fheight , 5,5, false, true, true, false);
         * canvas.drawPath(path, myPaint);
        </pre> *
         *
         * @param leftX The X coordinate of the left side of the rectangle
         * @param topY The Y coordinate of the top of the rectangle
         * @param rightX The X coordinate of the right side of the rectangle
         * @param bottomY The Y coordinate of the bottom of the rectangle
         * @param rx The x-radius of the oval used to round the corners
         * @param ry The y-radius of the oval used to round the corners
         * @param topLeft
         * @param topRight
         * @param bottomRight
         * @param bottomLeft
         * @return
         */
        fun RoundedRect(
            leftX: Float,
            topY: Float,
            rightX: Float,
            bottomY: Float,
            rx: Float,
            ry: Float,
            topLeft: Boolean,
            topRight: Boolean,
            bottomRight: Boolean,
            bottomLeft: Boolean
        ): Path {
            var rx = rx
            var ry = ry
            val path = Path()
            if (rx < 0) rx = 0f
            if (ry < 0) ry = 0f
            val width = rightX - leftX
            val height = bottomY - topY
            if (rx > width / 2) rx = width / 2
            if (ry > height / 2) ry = height / 2
            val widthMinusCorners = width - 2 * rx
            val heightMinusCorners = height - 2 * ry

            path.moveTo(rightX, topY + ry)
            if (topRight)
                path.rQuadTo(0F, -ry, -rx, -ry)//top-right corner
            else {
                path.rLineTo(0F, -ry)
                path.rLineTo(-rx, 0F)
            }
            path.rLineTo(-widthMinusCorners, 0F)
            if (topLeft)
                path.rQuadTo(-rx, 0F, -rx, ry) //top-left corner
            else {
                path.rLineTo(-rx, 0F)
                path.rLineTo(0F, ry)
            }
            path.rLineTo(0F, heightMinusCorners)

            if (bottomLeft)
                path.rQuadTo(0F, ry, rx, ry)//bottom-left corner
            else {
                path.rLineTo(0F, ry)
                path.rLineTo(rx, 0F)
            }

            path.rLineTo(widthMinusCorners, 0F)
            if (bottomRight)
                path.rQuadTo(rx, 0F, rx, -ry) //bottom-right corner
            else {
                path.rLineTo(rx, 0F)
                path.rLineTo(0F, -ry)
            }

            path.rLineTo(0F, -heightMinusCorners)

            path.close()//Given close, last lineto can be removed.

            return path
        }
    }

}