package com.webclues.IPPSManager.Modelclass

import android.content.Context
import com.webclues.IPPSManager.R

class FaqResponseItem {

    private var context: Context? = null

    constructor(context: Context) {
        this.context = context
    }


    fun getData(): HashMap<String, List<String>> {
        val expandableListDetail = HashMap<String, List<String>>()

        val FaqItem = ArrayList<String>()
        FaqItem.add(context!!.resources.getString(R.string.faq_desc))
        /*     cricket.add("Pakistan")
             cricket.add("Australia")
             cricket.add("England")
             cricket.add("South Africa")*/

        val FaqItem1 = ArrayList<String>()
        FaqItem1.add(context!!.resources.getString(R.string.faq_desc))
        /*    football.add("Spain")
            football.add("Germany")
            football.add("Netherlands")
            football.add("Italy")*/

        val FaqItem2 = ArrayList<String>()
        FaqItem2.add(context!!.resources.getString(R.string.faq_desc))
/*
        basketball.add("Spain")
        basketball.add("Argentina")
        basketball.add("France")
        basketball.add("Russia")


*/



        expandableListDetail[context!!.resources.getString(R.string.faq_title1)] = FaqItem
        expandableListDetail[context!!.resources.getString(R.string.faq_title2)] = FaqItem
        expandableListDetail[context!!.resources.getString(R.string.faq_title3)] = FaqItem
        expandableListDetail[context!!.resources.getString(R.string.faq_title4)] = FaqItem
        expandableListDetail[context!!.resources.getString(R.string.faq_title5)] = FaqItem
        expandableListDetail[context!!.resources.getString(R.string.faq_title6)] = FaqItem
        expandableListDetail[context!!.resources.getString(R.string.faq_title7)] = FaqItem
        expandableListDetail[context!!.resources.getString(R.string.faq_title8)] = FaqItem

        return expandableListDetail
    }
}