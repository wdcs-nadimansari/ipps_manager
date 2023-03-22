package com.webclues.IPPSManager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.webclues.IPPSManager.Modelclass.Image
import com.webclues.IPPSManager.R
import com.webclues.IPPSManager.activity.JobDetailActivity
import com.webclues.IPPSManager.utility.ProportionalImageView
import com.webclues.IPPSManager.utility.RoundRectCornerImageView
import com.webclues.IPPSManager.utility.RoundedTransformation
import com.webclues.IPPSManager.utility.ScaledImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class JobImageViewpagerAdapter(
    private val context: JobDetailActivity,
    val JobImagearray: List<Image>
) : PagerAdapter() {
    lateinit var layoutInflater: LayoutInflater


    override fun getCount(): Int {
        return JobImagearray.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val jobDetailModel: Image = JobImagearray.get(position)

        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.adp_jobdetail, null)
        val imageView = view.findViewById(R.id.ivJobImage) as RoundRectCornerImageView
        val progress = view.findViewById(R.id.progress) as ProgressBar
        Picasso.get().load(jobDetailModel.image_url).transform(RoundedTransformation(10, 10))
            .placeholder(R.drawable.ic_placeholder_job)
            .error(R.drawable.ic_placeholder_job)
            .into(imageView, object : Callback {
                override fun onError(e: Exception?) {
                    Toast.makeText(context, e?.message, Toast.LENGTH_LONG).show()
                }

                override fun onSuccess() {
                    progress.visibility = View.GONE
                    imageView.visibility = View.VISIBLE
                }

            })


        val vp = container as ViewPager
        vp.addView(view, 0)
        return view

    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)

    }
}