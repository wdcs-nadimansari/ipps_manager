package com.webclues.IPPSManager.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.webclues.IPPSManager.Modelclass.SensorResponseItem
import com.webclues.IPPSManager.R
import com.webclues.IPPSManager.adapter.SensorAdapter
import com.webclues.IPPSManager.adapter.SorterAdapter
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_sensor_and_sorter.*

class SensorAndSorterActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var ivBack: ImageView

    private var SorterHistorylist: ArrayList<SensorResponseItem>? = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor_and_sorter)

        initview()
    }

    private fun initview() {
        ivBack = findViewById(R.id.ivBack)
        ivBack?.setOnClickListener(this)

        rvSensorHistory.layoutManager = LinearLayoutManager(this)
        rvSensorHistory.setHasFixedSize(true)
//        rvSensorHistory.adapter = SensorAdapter(this,SensorHistoryList)

        rvSorterHistory.layoutManager = LinearLayoutManager(this)
        rvSorterHistory.setHasFixedSize(true)
//        rvSorterHistory.adapter = SorterAdapter(sorterHistoryList)
    }

/*
    private var SensorHistoryList = ArrayList<SensorResponseItem>().apply {

        add(SensorResponseItem("Fibre Cyclone 1", "History"))
        add(SensorResponseItem("Press", "History"))
        add(SensorResponseItem("Arcu odio", "History"))
        add(SensorResponseItem("Aliquam imperdict posuere", "History"))
        add(SensorResponseItem("Cras magna tristique", "History"))
        add(SensorResponseItem("Quam ipsum arcu", "History"))
        add(SensorResponseItem("Curabitur ipsum arcu", "History"))

    }*/
/*
    private fun SetSorterData(SorterArraylist: ArrayList<SensorResponseItem>) {

        SorterArraylist.add(SensorResponseItem("Total ETB", "3000"))
        SorterArraylist.add(SensorResponseItem("Unstripped Bunches", "300"))
        SorterArraylist.add(SensorResponseItem("Percentage", "10%"))

        rvSensorHistory.layoutManager = LinearLayoutManager(this)
        rvSensorHistory.setHasFixedSize(true)
        rvSensorHistory.adapter = SorterAdapter()
    }*/

     /*  private var sorterHistoryList = ArrayList<SensorResponseItem>().apply {

           add(SensorResponseItem("Total ETB", "3000"))
           add(SensorResponseItem("Unstripped Bunches", "300"))
           add(SensorResponseItem("Percentage", "10%"))

       }*/

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }
}
