package com.webclues.IPPSManager.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.webclues.IPPSManager.R
import com.webclues.IPPSManager.utility.Content
import com.webclues.IPPSManager.utility.Log
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.adp_addphotos.view.*
import java.io.File
import java.util.ArrayList


class AddPhotosAdapter(
    var context: Activity?,
    var imagePath: ArrayList<String>,
    var photos: AddPhotos?
) : RecyclerView.Adapter<AddPhotosAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.adp_addphotos, parent, false)
        return MyViewHolder(view)
    }

    class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {


        var ivAddPhotos = itemView.ivAddPhotos
        var ivPhoto = itemView.ivPhoto
        var ivClear = itemView.ivClear
        var frmPhoto = itemView.frmPhoto

    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Log.e("Image :$position", imagePath)
        if (imagePath.get(position).equals("+")) {
            holder.ivAddPhotos.visibility = View.VISIBLE
            holder.frmPhoto.visibility = View.GONE
        } else {

            holder.ivAddPhotos.visibility = View.GONE
            holder.frmPhoto.visibility = View.VISIBLE
            Picasso.get().load(File(imagePath.get(position)))
                .placeholder(R.drawable.ic_placeholder_job)
                .into(holder.ivPhoto)

        }
        //        txtCount.setText("" + (imagePath.size()-1));
/*  Log.e("Image :"+position, imagePath.get(position));
        holder.txtAddPhotos.setVisibility(View.GONE);
        holder.imgMenu.setVisibility(View.VISIBLE);
        Glide.with(context).load( imagePath.get(position)).into(holder.imgMenu);*/
        holder.ivAddPhotos.setOnClickListener(
            View.OnClickListener {
                /*   Intent intent = new Intent(context, AlbumSelectActivity.class);
        //                intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 6 - imagePath.size());
                        context.startActivityForResult(intent, Constants.REQUEST_CODE);*/
                photos!!.addphoto()

            })

        holder.ivClear.setOnClickListener(View.OnClickListener {

            photos!!.removephoto(position)
        })
    }

    override fun getItemCount(): Int {

        return imagePath.size
    }

    interface AddPhotos {
        fun addphoto()
        fun removephoto(position: Int)
    }


}