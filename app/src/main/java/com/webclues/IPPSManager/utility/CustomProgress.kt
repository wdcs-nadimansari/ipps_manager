package com.webclues.IPPSManager.utility

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.webclues.IPPSManager.R

class CustomProgress {

    private var mDialog: Dialog? = null

    fun showProgress(context: Context, message: String, cancelable: Boolean) {
        if (mDialog == null) {

            mDialog = Dialog(context)
            // no tile for the dialog
            mDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            mDialog!!.setContentView(R.layout.progressbar)
            var mProgressBar = mDialog!!.findViewById(R.id.progressBar) as ProgressBar
            //  mProgressBar.getIndeterminateDrawable().setColorFilter(context.getResources()
            // .getColor(R.color.material_blue_gray_500), PorterDuff.Mode.SRC_IN);
            val progressText = mDialog!!.findViewById(R.id.ProgressBarTitle) as TextView
            progressText.text = "" + message
            progressText.visibility = View.VISIBLE
            mProgressBar.setVisibility(View.VISIBLE)
            mProgressBar.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            // you can change or add this line according to your need
            mProgressBar.setIndeterminate(true)
            mDialog!!.setCancelable(cancelable)
            mDialog!!.setCanceledOnTouchOutside(cancelable)
            mDialog!!.show()
        }
    }


    fun hideProgress() {
        if (mDialog != null && mDialog!!.isShowing) {
            mDialog!!.dismiss()
            mDialog = null
        }
    }

    companion object {

        var customProgress: CustomProgress? = null

        val instance: CustomProgress
            get() {
                if (customProgress == null) {
                    customProgress = CustomProgress()
                }
                return customProgress!!
            }
    }
}