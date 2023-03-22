package com.webclues.IPPSManager.activity

import android.content.Context
import android.content.Intent
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.github.chrisbanes.photoview.OnMatrixChangedListener
import com.github.chrisbanes.photoview.OnPhotoTapListener
import com.github.chrisbanes.photoview.OnSingleFlingListener
import com.webclues.IPPSManager.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_image_view.*
import kotlinx.android.synthetic.main.activity_job_detail.ivBack


class ImageViewActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var selected: String
    lateinit var name: String
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)
        context = this
        initview()
    }

    private fun initview() {

        val intent: Intent = getIntent();
        val message = intent.getStringExtra("imageUrl")
        name = intent.getStringExtra("name")
        ivBack.setOnClickListener(this)

        val uri: Uri? = Uri.parse(message)
        Picasso.get().load(uri)
                .placeholder(R.drawable.ic_placeholder_job)
                .into(mPhotoView)

        // Lets attach some listeners, not required though!
        mPhotoView.setOnMatrixChangeListener(MatrixChangeListener())
        mPhotoView.setOnPhotoTapListener(PhotoTapListener())
        mPhotoView.setOnSingleFlingListener(SingleFlingListener())
        ivBack.text = name
    }

    private class MatrixChangeListener : OnMatrixChangedListener {
        override fun onMatrixChanged(rect: RectF) {
         //   mCurrMatrixTv.setText(rect.toString())
        }
    }

    private class SingleFlingListener : OnSingleFlingListener {
        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            val FLING_LOG_STRING = "Fling velocityX: %.2f, velocityY: %.2f"
            Log.d("PhotoView", String.format(FLING_LOG_STRING, velocityX, velocityY))
            return true
        }
    }

    private class PhotoTapListener : OnPhotoTapListener {
        override fun onPhotoTap(view: ImageView, x: Float, y: Float) {
            val xPercentage = x * 100f
            val yPercentage = y * 100f
//            showToast(String.format(com.github.chrisbanes.photoview.sample.SimpleSampleActivity.PHOTO_TAP_TOAST_STRING, xPercentage, yPercentage, view?.id
//                    ?: 0))
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                finish()
            }
        }
    }
}
