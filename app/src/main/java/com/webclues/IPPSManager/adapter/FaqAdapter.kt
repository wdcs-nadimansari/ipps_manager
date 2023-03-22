package com.webclues.IPPSManager.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.webclues.IPPSManager.R


class FaqAdapter(
    var context: Context,
    var ExpandableListTitle: List<String>,
    var ExpandableListDetail: HashMap<String, List<String>>
) : BaseExpandableListAdapter() {


    override fun getChildrenCount(groupPosition: Int): Int {
        return ExpandableListDetail.get(ExpandableListTitle.get(groupPosition))!!.size
//        return 1
    }

    override fun getGroup(groupPosition: Int): Any {

        return ExpandableListTitle.get(groupPosition)
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return ExpandableListDetail.get(ExpandableListTitle.get(groupPosition))!!.get(
            childPosition
        );
    }

    override fun getGroupId(groupPosition: Int): Long {

        return groupPosition.toLong()
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var view: View? = convertView
        val ExpandableListText: String = getChild(groupPosition, childPosition) as String
        if (view == null) {
//            view = convertView
            val layoutInflater: LayoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.faq_list_item, null)
        }


        var ExpandableListTextview: TextView =
            view!!.findViewById(R.id.txtChildItem) as TextView
        var rrItem = view.findViewById(R.id.llItem) as LinearLayout
        if (isLastChild) {
            rrItem.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlightBckground))
        } else {
            rrItem.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
        ExpandableListTextview.setText(
            HtmlCompat.fromHtml(
                ExpandableListText,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        )
        return view
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val listTitle = getGroup(groupPosition) as String
        var view: View? = convertView

        if (view == null) {
            val layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.faq_list_group, null)
        }

        val listTitleTextView = view!!.findViewById(R.id.txtGroupItem) as TextView
//        listTitleTextView.setTypeface(null, Typeface.BOLD)
        listTitleTextView.text = HtmlCompat.fromHtml(
            listTitle,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        var rrGroup: RelativeLayout = view.findViewById(R.id.rrGroup) as RelativeLayout
        val ind: View = view.findViewById(R.id.Groupview) as View
        val txtGroupItem = view.findViewById(R.id.txtGroupItem) as TextView
        val Indicator: ImageView = view.findViewById(R.id.ivIndicator) as ImageView
        if (isExpanded) {
            rrGroup.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlightBckground))
            ind.visibility = View.GONE
            Indicator.setImageResource(R.drawable.ic_down)
        } else {
//            rrGroup.setBackgroundResource(context.r)
//            rrGroup.setBackgroundColor(ContextCompat.getColor(context, ))
            rrGroup.setBackgroundColor(ContextCompat.getColor(context, R.color.white_30))

            ind.visibility = View.VISIBLE
            Indicator.setImageResource(R.drawable.ic_dropdown)


        }

        return view

    }

    override fun getGroupCount(): Int {


        return ExpandableListTitle.size
//        return ExpandableListTitle.size
    }


}