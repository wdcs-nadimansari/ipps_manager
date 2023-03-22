package com.webclues.IPPSManager.activity

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.webclues.IPPSManager.Modelclass.*
import com.webclues.IPPSManager.R
import com.webclues.IPPSManager.service.APIClient
import com.webclues.IPPSManager.utility.*
import com.webclues.IPPSManager.service.ApiInterface
import kotlinx.android.synthetic.main.activity_sensor_detail_history.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_report.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SensorDetailHistoryActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var mContext: Context
    private var filterList: ArrayList<String>? = arrayListOf()
    private var sensorList: ArrayList<SensorHistory> = arrayListOf()
    var BeaconSensorList: ArrayList<BeaconSensorModel> = ArrayList()
    var AccelerometerSensorList: ArrayList<EnergyMeterSensorClass> = ArrayList()
    var xAxisList: ArrayList<BeaconSensorModel> = ArrayList()
    var yAxisList: ArrayList<BeaconSensorModel> = ArrayList()
    var zAxisList: ArrayList<BeaconSensorModel> = ArrayList()
    private lateinit var filterDropdownView: ListPopupWindow
    lateinit var apiInterface: ApiInterface
    val dateFormat = SimpleDateFormat("MM-dd-yyyy")
    lateinit var sensorModel: SensorResponseItem
    lateinit var xAxis: AxisBase
    lateinit var yAxis: AxisBase
    var filterType: Int = 2
    private var mac: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor_detail_history)
        mContext = this
        sensorModel =
            Gson().fromJson(intent.getStringExtra("sensorModel"), SensorResponseItem::class.java)
        apiInterface = APIClient.getretrofit(mContext).create(ApiInterface::class.java)
        initview()
    }

    private fun initview() {

        txtDate.text = dateFormat.format(Date().time)
        txtDateEnd.text = dateFormat.format(Date().time)
        txtSensorName.text = sensorModel.sensor_name
        /*   if (intent.hasExtra("from") && intent.getStringExtra("from").equals("notification")) {
               if (sensorModel.sensor_type.equals(Content.SENSOR_ENERGY_METER)) {
                   filterList = getResources().getStringArray(R.array.energy_meter_filter)
                       .toCollection(ArrayList())

                   filterType = intent.getIntExtra("filter", 2)
               } else {
                   filterList =
                       getResources().getStringArray(R.array.beacon_filter).toCollection(ArrayList())

                   filterType = intent.getIntExtra("filter", 4)
               }

           } else {
               if (sensorModel.sensor_type.equals(Content.SENSOR_ENERGY_METER)) {
                   filterList = getResources().getStringArray(R.array.energy_meter_filter)
                       .toCollection(ArrayList())

                   filterType = 2
               } else {
                   filterList =
                       getResources().getStringArray(R.array.beacon_filter).toCollection(ArrayList())

                   filterType = 4
               }

           }

           when (filterType) {
               1 -> {
                   txtFilter.text = getString(R.string.voltage)
               }
               2 -> {
                   txtFilter.text = getString(R.string.current)
               }
               3 -> {
                   txtFilter.text = getString(R.string.power)
               }
               4 -> {
                   txtFilter.text = getString(R.string.temprature)
               }
               5 -> {
                   txtFilter.text = getString(R.string.axis)
               }
           }*/



        setClickLIstner()

//        getSensorHistory1(sensorModel.sensor_type!!,txtDate.text.toString(),txtDateEnd.text.toString(),sensorModel.mac!!)//"25/01/2021"
        getSensorHistory2(sensorModel.sensor_type!!)//"25/01/2021"
    }

    fun setClickLIstner() {
        txtDate.setOnClickListener(this)
        txtDateEnd.setOnClickListener(this)
        ivBack.setOnClickListener(this)
//        btnSetAlarm.setOnClickListener(this)
        txtGo.setOnClickListener(this)
//        txtAccelerometer.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.txtDate -> {
                showDatePicker()
            }
            R.id.txtDateEnd -> {
                showDatePickerEnd()
            }
            R.id.btnSetAlarm -> {
                if (isLimitValid()) {
                    if (sensorModel.sensor_type.equals(Content.SENSOR_ENERGY_METER)) {
                        setAccelerationAlarm()
                    } else {
                        setBeaconAlarm()
                    }
                    Log.e("Limit -->", "Success")
                }
            }
            R.id.txtGo -> {
                getChart(
                    sensorModel.sensor_type!!,
                    txtDate.text.toString(),
                    txtDateEnd.text.toString(),
                    mac,
                    "Asia/Singapore"
                )
            }

        }
    }

    private fun initChart(data: JSONObject) {

        chart.description.isEnabled = false
        // enable touch gestures
        chart.setTouchEnabled(false)
        // enable scaling and dragging
        chart.isDragEnabled = false
        chart.setScaleEnabled(true)
        chart.setDrawGridBackground(false)
        chart.legend.isEnabled = false
        // set an alternative background color
        chart.setBackgroundColor(Color.WHITE)
//        chart.setViewPortOffsets(0f, 0f, 0f, 0f);
        //        chart.setViewPortOffsets(0f, 0f, 0f, 0f);
        chart.axisRight.isEnabled = false

        yAxis = chart.axisLeft
        xAxis = chart.xAxis
        (xAxis as XAxis?)?.position = XAxis.XAxisPosition.BOTTOM
        xAxis.disableGridDashedLine()
        xAxis.disableAxisLineDashedLine()
//        xAxis.mAxisRange = 24f
//        xAxis.granularity = 8f
        xAxis.valueFormatter = MyXAxisValueFormatter()
        xAxis.isGranularityEnabled = true

        if (data.optBoolean("is_ble_chart")) {
            val blechart = data.optJSONObject("ble_chart_data")
            val xAxis = blechart.optJSONArray("xAxis")
            val yAxis = blechart.optJSONArray("yAxis")
            val zAxis = blechart.optJSONArray("zAxis")
            xAxisList.clear()
            yAxisList.clear()
            zAxisList.clear()
            xAxisList.addAll(
                Gson().fromJson(
                    xAxis.toString(),
                    Array<BeaconSensorModel>::class.java
                ).toList()
            )

            yAxisList.addAll(
                Gson().fromJson(
                    yAxis.toString(),
                    Array<BeaconSensorModel>::class.java
                ).toList()
            )

            zAxisList.addAll(
                Gson().fromJson(
                    zAxis.toString(),
                    Array<BeaconSensorModel>::class.java
                ).toList()
            )

            if (xAxisList.size != 0) {
                chart.visibility = View.VISIBLE
                txtXAxis.visibility = View.VISIBLE
            } else {
                chart.visibility = View.GONE
                txtXAxis.visibility = View.GONE

            }

            if (yAxisList.size != 0) {
                txtyAxis.visibility = View.VISIBLE
                ychart.visibility = View.VISIBLE
            } else {
                txtyAxis.visibility = View.GONE
                ychart.visibility = View.GONE
            }

            if (zAxisList.size != 0) {
                txtZAxis.visibility = View.VISIBLE
                zchart.visibility = View.VISIBLE
            } else {
                txtZAxis.visibility = View.GONE
                zchart.visibility = View.GONE
            }

        } else {
            val emeterdata = data.optJSONObject("emeter_chart_data")
            val chpower = emeterdata.optJSONArray("chpower")
            AccelerometerSensorList.clear()
            AccelerometerSensorList.addAll(
                Gson().fromJson(
                    chpower.toString(),
                    Array<EnergyMeterSensorClass>::class.java
                ).toList()
            )
            txtXAxis.visibility = View.GONE
            txtyAxis.visibility = View.GONE
            txtZAxis.visibility = View.GONE
            ychart.visibility = View.GONE
            zchart.visibility = View.GONE
        }

        if (AccelerometerSensorList.size==0){
            tvNoRecord.visibility=View.VISIBLE
            chart.visibility=View.GONE
        }else{
            tvNoRecord.visibility=View.GONE
            chart.visibility=View.VISIBLE
        }


        if (data.optBoolean("is_ble_chart")) {
            setBeaconMeterData1(data)

        } else {

            SetEnergyMeterData1(data)

        }


    }

    private fun SetEnergyMeterData(data: JSONObject) {
        lnBettery.visibility = View.VISIBLE
        lnEnergyAlarm.visibility = View.VISIBLE
        btnSetAlarm.visibility = View.VISIBLE
        txtExtraDetails.text = getString(R.string.running_hour)
        txtBattery.text = data.optString("running_hour_data")
        chart.clear()
        edHighLimit.text.clear()
        edLowLimit.text.clear()
        var ll1: LimitLine? = null
        var ll2: LimitLine? = null
        var formatt: LargeValueFormatter = LargeValueFormatter()

        val values = ArrayList<Entry>()

        for (i in 0 until AccelerometerSensorList.size) {
//            val Yval = (Math.random() * range).toFloat() - 30
            val Yval = AccelerometerSensorList[i].edt.toFloat()
            Log.e(
                "Chart Entry-->",
                "X= " + AccelerometerSensorList[i].value.toFloat() + " & Y= " + Yval
            )
            values.add(Entry(AccelerometerSensorList[i].value.toFloat(), Yval, null))
        }

        when {
            txtFilter.text.toString().equals(getString(R.string.current), true) -> {
                if (data.optString("max_current").isNotBlank() && data.optString("min_current")
                        .isNotBlank()
                ) {
                    yAxis.axisMinimum = data.optString("min_current").toFloat() - 1f
                    yAxis.axisMaximum = data.optString("max_current").toFloat() + 1f
                    ll1 = LimitLine(data.optString("max_current").toFloat(), "Upper Limit")
                    ll2 = LimitLine(data.optString("min_current").toFloat(), "Lower Limit")
                    edHighLimit.setText(data.optString("max_current"))
                    edLowLimit.setText(data.optString("min_current"))
                } else {
                    yAxis.axisMinimum = 0f
                    yAxis.axisMaximum = 60f
                }

                formatt.setAppendix("A")
                for (i in 0 until AccelerometerSensorList.size) {
//            val Yval = (Math.random() * range).toFloat() - 30
                    val Yval = AccelerometerSensorList[i].current.toFloat()
                    Log.e(
                        "Chart Entry-->",
                        "X= " + AccelerometerSensorList[i].time.toFloat() + " & Y= " + Yval
                    )
                    values.add(Entry(AccelerometerSensorList[i].time.toFloat(), Yval, null))
                }

            }
            txtFilter.text.toString().equals(getString(R.string.voltage), true) -> {
                if (data.optString("max_voltage").isNotBlank() && data.optString("min_voltage")
                        .isNotBlank()
                ) {
                    yAxis.axisMinimum = data.optString("min_voltage").toFloat() - 1f
                    yAxis.axisMaximum = data.optString("max_voltage").toFloat() + 1f
                    ll1 = LimitLine(data.optString("max_voltage").toFloat(), "Upper Limit")
                    ll2 = LimitLine(data.optString("min_voltage").toFloat(), "Lower Limit")
                    edHighLimit.setText(data.optString("max_voltage"))
                    edLowLimit.setText(data.optString("min_voltage"))
                } else {
                    yAxis.axisMinimum = 230f
                    yAxis.axisMaximum = 240f
                }

                formatt.setAppendix("V")
                for (i in 0 until AccelerometerSensorList.size) {
//            val Yval = (Math.random() * range).toFloat() - 30
                    val Yval = AccelerometerSensorList[i].voltage.toFloat()
                    Log.e(
                        "Chart Entry-->",
                        "X= " + AccelerometerSensorList[i].time.toFloat() + " & Y= " + Yval
                    )
                    values.add(Entry(AccelerometerSensorList[i].time.toFloat(), Yval, null))
                }

            }
            txtFilter.text.toString().equals(getString(R.string.power), true) -> {
                if (data.optString("max_power").isNotBlank() && data.optString("min_power")
                        .isNotBlank()
                ) {
                    yAxis.axisMinimum = data.optString("min_power").toFloat() - 5f
                    yAxis.axisMaximum = data.optString("max_power").toFloat() + 5f
                    ll1 = LimitLine(data.optString("max_power").toFloat(), "Upper Limit")
                    ll2 = LimitLine(data.optString("min_power").toFloat(), "Lower Limit")
                    edHighLimit.setText(data.optString("max_power"))
                    edLowLimit.setText(data.optString("min_power"))
                } else {
                    yAxis.axisMinimum = 1000f
                    yAxis.axisMaximum = 15000f
                }

                formatt.setAppendix("W")
                for (i in 0 until AccelerometerSensorList.size) {
//            val Yval = (Math.random() * range).toFloat() - 30
                    val Yval = AccelerometerSensorList[i].power.toFloat()
                    Log.e(
                        "Chart Entry-->",
                        "X= " + AccelerometerSensorList[i].time.toFloat() + " & Y= " + Yval
                    )
                    values.add(Entry(AccelerometerSensorList[i].time.toFloat(), Yval, null))
                }

            }

        }
//        formatt.setAppendix("W")
        yAxis.valueFormatter = formatt
        var set1: LineDataSet


        if (chart.data != null && chart.data.dataSetCount > 0
        ) {
            set1 = chart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            set1.notifyDataSetChanged()
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {
            // Create Limit Lines
            if (ll1 != null && ll2 != null) {
                ll1.lineWidth = 2f
                ll1.enableDashedLine(10f, 10f, 0f)
                ll1.labelPosition = LimitLabelPosition.RIGHT_TOP
                ll1.textSize = 10f

                ll2.lineWidth = 2f
                ll2.enableDashedLine(10f, 10f, 0f)
                ll2.labelPosition = LimitLabelPosition.RIGHT_BOTTOM
                ll2.textSize = 10f

                // draw limit lines behind data instead of on top
                yAxis.setDrawLimitLinesBehindData(false)
                xAxis.setDrawLimitLinesBehindData(false)

                // add limit lines
                yAxis.removeAllLimitLines()
                yAxis.addLimitLine(ll1)
                yAxis.addLimitLine(ll2)
            }


            // create a dataset and give it a type
            set1 = LineDataSet(values, "")
            set1.setDrawIcons(false)

            // draw dashed line
//            set1.enableDashedLine(10f, 5f, 0f)
            set1.disableDashedLine()
            // black lines and points

            // line thickness and point size
            set1.lineWidth = 1.5f
//            set1.circleRadius = 3f

            // draw points as solid circles
            set1.setDrawCircleHole(false)
            set1.setDrawCircles(false)

            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f)

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the data sets

            // create a data object with the data sets
            val data = LineData(dataSets)
            data.setValueTextColor(Color.BLACK);
            data.setValueTextSize(12f);
            // set data
            chart.data = data
        }

    }


    private fun SetEnergyMeterData1(data: JSONObject) {
        lnBettery.visibility = View.VISIBLE
        lnEnergyAlarm.visibility = View.VISIBLE
        btnSetAlarm.visibility = View.VISIBLE
        txtExtraDetails.text = getString(R.string.running_hour)
        txtBattery.text = data.optString("running_hour_data")

        chart.clear()
        edHighLimit.text.clear()
        edLowLimit.text.clear()


        val values = ArrayList<Entry>()

        val datapoints = ArrayList<String>()

        val doublearray = ArrayList<Double>()

        for (i in 0 until AccelerometerSensorList.size) {
            val data = getDate(AccelerometerSensorList[i].edt.toLong());
            datapoints.add(data!!)
        }


        for (i in 0 until AccelerometerSensorList.size) {
            val doublevalue = AccelerometerSensorList[i].value.toDouble()
            doublearray.add(doublevalue)
        }



        for (i in 0 until AccelerometerSensorList.size) {
//            val Yval = (Math.random() * range).toFloat() - 30
            val Yval = getDate(AccelerometerSensorList[i].edt.toLong())
            Log.e(
                "Chart Entry-->",
                "X= " + i.toFloat() + " & Y= " + AccelerometerSensorList[i].value.toFloat()
            )
            values.add(
                Entry(
                    i.toFloat(),
                    doublearray.get(i).toFloat(),
                    null
                )
            )
        }


        var set1: LineDataSet


        if (chart.data != null && chart.data.dataSetCount > 0
        ) {
            set1 = chart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            set1.notifyDataSetChanged()
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {

            set1 = LineDataSet(values, "Label")
            set1.setDrawValues(false)
            set1.setDrawCircles(false)
            set1.setDrawCircleHole(false)
            set1.lineWidth = 1.5f
            set1.setColor(Color.GRAY)

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the data sets


            val data = LineData(dataSets)
            data.setValueTextColor(Color.BLACK);
            data.setValueTextSize(12f);
            chart.isHorizontalScrollBarEnabled = true

            chart.data = data


            chart.legend.isEnabled = false
            chart.setTouchEnabled(true);
            chart.isScaleXEnabled = true


            chart.xAxis.valueFormatter = IndexAxisValueFormatter(datapoints)
            chart.xAxis.setDrawGridLines(false)
            chart.xAxis.setAvoidFirstLastClipping(false)
            chart.xAxis.setAxisMaxValue(12F);
            chart.xAxis.position = XAxis.XAxisPosition.BOTTOM

            chart.axisRight.setDrawAxisLine(false)
            chart.axisRight.setDrawLabels(false)

            chart.axisLeft.setDrawAxisLine(false)
            chart.setPinchZoom(true)
            chart.animateX(1500);
            chart.isDoubleTapToZoomEnabled = false


        }

    }


    fun setBeaconMeterData(data: JSONObject) {
        lnBettery.visibility = View.VISIBLE

        txtExtraDetails.text = getString(R.string.battery)
        txtBattery.text = data.optString("battery_level") + "%"

        chart.clear()
        edHighLimit.text.clear()
        edLowLimit.text.clear()

        var ll1: LimitLine? = null
        var ll2: LimitLine? = null
        var formatt: LargeValueFormatter = LargeValueFormatter()


        var set1: LineDataSet
        var setX: LineDataSet
        var setY: LineDataSet
        var setZ: LineDataSet
        val dataSets = ArrayList<ILineDataSet>()

        when {
            txtFilter.text.toString().equals(getString(R.string.temprature), true) -> {
                btnSetAlarm.visibility = View.VISIBLE
                lnAxisAlarm.visibility = View.GONE
                lnEnergyAlarm.visibility = View.VISIBLE
                val values = ArrayList<Entry>()
                if (data.optString("maximum_temperature")
                        .isNotBlank() && data.optString("minimum_temperature").isNotBlank()
                ) {
                    yAxis.axisMinimum = data.optString("minimum_temperature").toFloat() - 1f
                    yAxis.axisMaximum = data.optString("maximum_temperature").toFloat() + 1f
                    ll1 = LimitLine(data.optString("maximum_temperature").toFloat(), "Upper Limit")
                    ll2 = LimitLine(data.optString("minimum_temperature").toFloat(), "Lower Limit")
                    edHighLimit.setText(data.optString("maximum_temperature"))
                    edLowLimit.setText(data.optString("minimum_temperature"))
                } else {
                    yAxis.axisMinimum = 0f
                    yAxis.axisMaximum = 80f
                }

                formatt.setAppendix("F")
                for (i in 0 until BeaconSensorList.size) {
//            val Yval = (Math.random() * range).toFloat() - 30

                    //   val Yval = BeaconSensorList[i].temperature?.toFloat()
//                    Log.e(
//                        "Chart Entry-->",
//                        "X= " + BeaconSensorList[i].timestamp.toFloat() + " & Y= " + Yval
//                    )
//                    values.add(Entry(BeaconSensorList[i].timestamp.toFloat(), Yval, null))

                    if (BeaconSensorList[i].temperature.equals("")) {
                        val Yval = 0f
                        values.add(Entry(BeaconSensorList[i].timestamp.toFloat(), Yval, null))
                    } else {
                        val Yval = BeaconSensorList[i].temperature.toFloat()
                        values.add(Entry(BeaconSensorList[i].timestamp.toFloat(), Yval, null))
                    }
                }
                yAxis.valueFormatter = formatt
                if (ll1 != null && ll2 != null) {
                    ll1.lineWidth = 4f
                    ll1.enableDashedLine(10f, 10f, 0f)
                    ll1.labelPosition = LimitLabelPosition.RIGHT_TOP
                    ll1.textSize = 10f

                    ll2.lineWidth = 4f
                    ll2.enableDashedLine(10f, 10f, 0f)
                    ll2.labelPosition = LimitLabelPosition.RIGHT_BOTTOM
                    ll2.textSize = 10f

                    // draw limit lines behind data instead of on top
                    yAxis.setDrawLimitLinesBehindData(true)
                    xAxis.setDrawLimitLinesBehindData(true)

                    // add limit lines
                    yAxis.removeAllLimitLines()
                    yAxis.addLimitLine(ll1)
                    yAxis.addLimitLine(ll2)
                }


                set1 = LineDataSet(values, "")
                set1.setDrawIcons(false)
                set1.disableDashedLine()
                set1.lineWidth = 1.5f
                set1.setDrawCircleHole(false)
                set1.setDrawCircles(false)
                set1.enableDashedHighlightLine(10f, 5f, 0f)
                dataSets.add(set1) // add the data sets
            }
            txtFilter.text.toString().equals(getString(R.string.axis), true) -> {
                lnAxisAlarm.visibility = View.VISIBLE
                lnEnergyAlarm.visibility = View.GONE

                yAxis.axisMinimum = -1f
                yAxis.axisMaximum = 1f

                edXHigh.setText(data.optString("x_high"))
                edXLow.setText(data.optString("x_low"))
                edYHigh.setText(data.optString("y_high"))
                edYLow.setText(data.optString("y_low"))
                edZHigh.setText(data.optString("z_high"))
                edZLow.setText(data.optString("z_low"))

                yAxis.valueFormatter = null
                val Xvalues = ArrayList<Entry>()
                val Yvalues = ArrayList<Entry>()
                val Zvalues = ArrayList<Entry>()
                for (i in 0 until BeaconSensorList.size) {
                    val Xval = BeaconSensorList[i].x_axis.toFloat()
                    val Yval = BeaconSensorList[i].y_axis.toFloat()
                    val Zval = BeaconSensorList[i].z_axis.toFloat()

                    Xvalues.add(Entry(BeaconSensorList[i].timestamp.toFloat(), Xval, null))
                    Yvalues.add(Entry(BeaconSensorList[i].timestamp.toFloat(), Yval, null))
                    Zvalues.add(Entry(BeaconSensorList[i].timestamp.toFloat(), Zval, null))
                }

//                X-Axis
                setX = LineDataSet(Xvalues, "X-Axis")
                setX.setDrawIcons(false)
                setX.disableDashedLine()
                setX.lineWidth = 1.5f
                setX.setDrawCircleHole(false)
                setX.setDrawCircles(false)
                setX.color = ContextCompat.getColor(mContext, R.color.red)
                setX.enableDashedHighlightLine(10f, 5f, 0f)

//                YAxis
                setY = LineDataSet(Yvalues, "Y-Axis")
                setY.setDrawIcons(false)
                setY.disableDashedLine()
                setY.lineWidth = 1.5f
                setY.setDrawCircleHole(false)
                setY.setDrawCircles(false)
                setY.color = ContextCompat.getColor(mContext, R.color.green)
                setY.enableDashedHighlightLine(10f, 5f, 0f)

//                ZAxis
                setZ = LineDataSet(Zvalues, "Z-Axis")
                setZ.setDrawIcons(false)
                setZ.disableDashedLine()
                setZ.lineWidth = 1.5f
                setZ.setDrawCircleHole(false)
                setZ.setDrawCircles(false)
                setZ.color = ContextCompat.getColor(mContext, R.color.blue)
                setZ.enableDashedHighlightLine(10f, 5f, 0f)

                dataSets.add(setX) // add the data sets
                dataSets.add(setY)
                dataSets.add(setZ)
                chart.legend.isEnabled = true
                val l = chart.legend
                l.orientation = Legend.LegendOrientation.VERTICAL
                l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                l.setDrawInside(false)
            }


        }
        val data = LineData(dataSets)
        chart.data = data


    }


    fun setBeaconMeterData1(data: JSONObject) {
        lnBettery.visibility = View.VISIBLE
        lnEnergyAlarm.visibility = View.VISIBLE
        btnSetAlarm.visibility = View.VISIBLE
        txtExtraDetails.text = getString(R.string.running_hour)
        txtBattery.text = data.optString("running_hour_data")

        chart.clear()
        edHighLimit.text.clear()
        edLowLimit.text.clear()


        val xvalues = ArrayList<Entry>()
        val yvalues = ArrayList<Entry>()
        val zvalues = ArrayList<Entry>()

        val xdatapoints = ArrayList<String>()
        val ydatapoints = ArrayList<String>()
        val zdatapoints = ArrayList<String>()

        val xdoublearray = ArrayList<Double>()
        val ydoublearray = ArrayList<Double>()
        val zdoublearray = ArrayList<Double>()

        for (i in 0 until xAxisList.size) {
            val data = getDate(xAxisList[i].bdt.toLong());
            xdatapoints.add(data!!)
        }

        for (i in 0 until yAxisList.size) {
            val data = getDate(yAxisList[i].bdt.toLong());
            ydatapoints.add(data!!)
        }

        for (i in 0 until zAxisList.size) {
            val data = getDate(zAxisList[i].bdt.toLong());
            zdatapoints.add(data!!)
        }



        for (i in 0 until xAxisList.size) {
            val doublevalue = xAxisList[i].value.toDouble()
            xdoublearray.add(doublevalue)
        }

        for (i in 0 until yAxisList.size) {
            val doublevalue = yAxisList[i].value.toDouble()
            ydoublearray.add(doublevalue)
        }

        for (i in 0 until zAxisList.size) {
            val doublevalue = zAxisList[i].value.toDouble()
            zdoublearray.add(doublevalue)
        }



        for (i in 0 until xAxisList.size) {
            Log.e(
                "Chart Entry-->",
                "X= " + i.toFloat() + " & Y= " + xAxisList[i].value.toFloat()
            )
            xvalues.add(
                Entry(
                    i.toFloat(),
                    xdoublearray.get(i).toFloat(),
                    null
                )
            )
        }

        for (i in 0 until yAxisList.size) {
            Log.e(
                "Chart Entry-->",
                "X= " + i.toFloat() + " & Y= " + yAxisList[i].value.toFloat()
            )
            yvalues.add(
                Entry(
                    i.toFloat(),
                    ydoublearray.get(i).toFloat(),
                    null
                )
            )
        }


        for (i in 0 until zAxisList.size) {
            Log.e(
                "Chart Entry-->",
                "X= " + i.toFloat() + " & Y= " + zAxisList[i].value.toFloat()
            )
            zvalues.add(
                Entry(
                    i.toFloat(),
                    zdoublearray.get(i).toFloat(),
                    null
                )
            )
        }

        var setx: LineDataSet
        var sety: LineDataSet
        var setz: LineDataSet


        //X-Axis graph draw

        if (chart.data != null && chart.data.dataSetCount > 0
        ) {
            setx = chart.data.getDataSetByIndex(0) as LineDataSet
            setx.values = xvalues
            setx.notifyDataSetChanged()
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {

            setx = LineDataSet(xvalues, "X-Axis")
            setx.setDrawValues(false)
            setx.setDrawCircles(false)
            setx.setDrawCircleHole(false)
            setx.lineWidth = 1.5f
            setx.setColor(Color.GRAY)

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(setx) // add the data sets


            val data = LineData(dataSets)
            data.setValueTextColor(Color.BLACK);
            data.setValueTextSize(12f);
            chart.isHorizontalScrollBarEnabled = true

            chart.data = data


            chart.legend.isEnabled = false
            chart.setTouchEnabled(true);
            chart.isScaleXEnabled = true


            chart.xAxis.valueFormatter = IndexAxisValueFormatter(xdatapoints)
            chart.xAxis.setDrawGridLines(false)
            chart.xAxis.setAvoidFirstLastClipping(false)
            chart.xAxis.setAxisMaxValue(12F);
            chart.xAxis.position = XAxis.XAxisPosition.BOTTOM

            chart.axisRight.setDrawAxisLine(false)
            chart.axisRight.setDrawLabels(false)

            chart.axisLeft.setDrawAxisLine(false)
            chart.setPinchZoom(true)
            chart.animateX(1500);
            chart.isDoubleTapToZoomEnabled = false


        }

        //Y-Axis graph draw

        if (ychart.data != null && ychart.data.dataSetCount > 0
        ) {
            sety = ychart.data.getDataSetByIndex(0) as LineDataSet
            sety.values = yvalues
            sety.notifyDataSetChanged()
            ychart.data.notifyDataChanged()
            ychart.notifyDataSetChanged()
        } else {

            sety = LineDataSet(yvalues, "Y-Axis")
            sety.setDrawValues(false)
            sety.setDrawCircles(false)
            sety.setDrawCircleHole(false)
            sety.lineWidth = 1.5f
            sety.setColor(Color.GRAY)

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(sety) // add the data sets


            val data = LineData(dataSets)
            data.setValueTextColor(Color.BLACK);
            data.setValueTextSize(12f);
            ychart.isHorizontalScrollBarEnabled = true

            ychart.data = data


            ychart.legend.isEnabled = false
            ychart.setTouchEnabled(true);
            ychart.isScaleXEnabled = true


            ychart.xAxis.valueFormatter = IndexAxisValueFormatter(ydatapoints)
            ychart.xAxis.setDrawGridLines(false)
            ychart.xAxis.setAvoidFirstLastClipping(false)
            ychart.xAxis.setAxisMaxValue(12F);
            ychart.xAxis.position = XAxis.XAxisPosition.BOTTOM

            ychart.axisRight.setDrawAxisLine(false)
            ychart.axisRight.setDrawLabels(false)

            ychart.axisLeft.setDrawAxisLine(false)
            ychart.setPinchZoom(true)
            ychart.animateX(1500);
            ychart.isDoubleTapToZoomEnabled = false


        }

        //Z-Axis graph draw


        if (zchart.data != null && zchart.data.dataSetCount > 0
        ) {
            setz = zchart.data.getDataSetByIndex(0) as LineDataSet
            setz.values = zvalues
            setz.notifyDataSetChanged()
            zchart.data.notifyDataChanged()
            zchart.notifyDataSetChanged()
        } else {

            setz = LineDataSet(zvalues, "Z-Axis")
            setz.setDrawValues(false)
            setz.setDrawCircles(false)
            setz.setDrawCircleHole(false)
            setz.lineWidth = 1.5f
            setz.setColor(Color.GRAY)

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(setz) // add the data sets


            val data = LineData(dataSets)
            data.setValueTextColor(Color.BLACK);
            data.setValueTextSize(12f);
            zchart.isHorizontalScrollBarEnabled = true

            zchart.data = data


            zchart.legend.isEnabled = false
            zchart.setTouchEnabled(true);
            zchart.isScaleXEnabled = true


            zchart.xAxis.valueFormatter = IndexAxisValueFormatter(zdatapoints)
            zchart.xAxis.setDrawGridLines(false)
            zchart.xAxis.setAvoidFirstLastClipping(false)
            zchart.xAxis.setAxisMaxValue(12F);
            zchart.xAxis.position = XAxis.XAxisPosition.BOTTOM

            zchart.axisRight.setDrawAxisLine(false)
            zchart.axisRight.setDrawLabels(false)

            zchart.axisLeft.setDrawAxisLine(false)
            zchart.setPinchZoom(true)
            zchart.animateX(1500);
            zchart.isDoubleTapToZoomEnabled = false


        }


    }


    private fun isLimitValid(): Boolean {
        var valid: Boolean = false;
        if (txtFilter.text.toString().equals(getString(R.string.axis))) {
            if (edXHigh.text.isBlank()) {
                Utility().showOkDialog(
                    mContext,
                    resources.getString(R.string.app_name),
                    resources.getString(R.string.error_add_x_high_limits)
                )
            } else if (edXLow.text.isBlank()) {
                Utility().showOkDialog(
                    mContext,
                    resources.getString(R.string.app_name),
                    resources.getString(R.string.error_add_x_low_limits)
                )
            } else if (edXHigh.text.toString().toFloat() < edXLow.text.toString().toFloat()) {
                Utility().showOkDialog(
                    mContext,
                    resources.getString(R.string.app_name),
                    resources.getString(R.string.error_invalid_xlimits)
                )
            } else if (edYHigh.text.isBlank()) {
                Utility().showOkDialog(
                    mContext,
                    resources.getString(R.string.app_name),
                    resources.getString(R.string.error_add_y_high_limits)
                )
            } else if (edYLow.text.isBlank()) {
                Utility().showOkDialog(
                    mContext,
                    resources.getString(R.string.app_name),
                    resources.getString(R.string.error_add_y_low_limits)
                )
            } else if (edYHigh.text.toString().toFloat() < edYLow.text.toString().toFloat()) {
                Utility().showOkDialog(
                    mContext,
                    resources.getString(R.string.app_name),
                    resources.getString(R.string.error_invalid_ylimits)
                )
            } else if (edZHigh.text.isBlank()) {
                Utility().showOkDialog(
                    mContext,
                    resources.getString(R.string.app_name),
                    resources.getString(R.string.error_add_z_high_limits)
                )
            } else if (edZLow.text.isBlank()) {
                Utility().showOkDialog(
                    mContext,
                    resources.getString(R.string.app_name),
                    resources.getString(R.string.error_add_z_low_limits)
                )
            } else if (edZHigh.text.toString().toFloat() < edZLow.text.toString().toFloat()) {
                Utility().showOkDialog(
                    mContext,
                    resources.getString(R.string.app_name),
                    resources.getString(R.string.error_invalid_zlimits)
                )
            } else {
                valid = true

            }

        } else {
            if (edHighLimit.text.isBlank()) {
                Utility().showOkDialog(
                    mContext,
                    resources.getString(R.string.app_name),
                    resources.getString(R.string.error_add_high_limit)
                )
            } else if (edLowLimit.text.isBlank()) {
                Utility().showOkDialog(
                    mContext,
                    resources.getString(R.string.app_name),
                    resources.getString(R.string.error_add_low_limit)
                )
            } else if (edHighLimit.text.toString().toFloat() < edLowLimit.text.toString()
                    .toFloat()
            ) {
                Utility().showOkDialog(
                    mContext,
                    resources.getString(R.string.app_name),
                    resources.getString(R.string.error_invalid_limits)
                )
            } else {
                valid = true
            }
        }

        return valid
    }

    lateinit var dpd: DatePickerDialog;
    lateinit var dpd1: DatePickerDialog;
    private fun showDatePicker() {
        val calendar: Calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        dpd = DatePickerDialog(
            mContext,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

                var selectDate: Calendar = Calendar.getInstance()
                selectDate.set(Calendar.YEAR, year)
                selectDate.set(Calendar.MONTH, month)
                selectDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                Log.e("Selected Date==", dateFormat.format(selectDate.time))
                txtDate.text = dateFormat.format(selectDate.time)
                getChart(
                    sensorModel.sensor_type!!,
                    txtDate.text.toString(),
                    txtDateEnd.text.toString(),
                    mac,
                    "Asia/Singapore"
                )

            },
            year,
            month,
            day
        )
        dpd.datePicker.maxDate = System.currentTimeMillis() - 1000
//        dpd.datePicker.maxDate = System.currentTimeMillis() - 1000
        /*  var cal2 = Calendar.getInstance()
          cal2.add(Calendar.DAY_OF_MONTH, -7)
          val minDate: Date = cal2.time
          dpd.datePicker.minDate = minDate.time*/
        if (!dpd.isShowing) {
            dpd.show()
        }

    }

    private fun showDatePickerEnd() {
        val calendar: Calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        dpd1 = DatePickerDialog(
            mContext,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

                var selectDate: Calendar = Calendar.getInstance()
                selectDate.set(Calendar.YEAR, year)
                selectDate.set(Calendar.MONTH, month)
                selectDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                Log.e("Selected Date==", dateFormat.format(selectDate.time))
                txtDateEnd.text = dateFormat.format(selectDate.time)
                getChart(
                    sensorModel.sensor_type!!,
                    txtDate.text.toString(),
                    txtDateEnd.text.toString(),
                    mac,
                    "Asia/Singapore"
                )

                /* getSensorHistory1(
                     sensorModel.sensor_type!!,
                     txtDate.text.toString(),
                     txtDateEnd.text.toString(),
                     sensorModel.mac!!
                 )*/
            },
            year,
            month,
            day
        )
        dpd1.datePicker.maxDate = System.currentTimeMillis() - 1000
        /*  var cal2 = Calendar.getInstance()
          cal2.add(Calendar.DAY_OF_MONTH, -7)
          val minDate: Date = cal2.time
          dpd1.datePicker.minDate = minDate.time*/
        if (!dpd1.isShowing) {
            dpd1.show()
        }

    }


    var i = 0


    private fun SetFilter(sensorList: ArrayList<SensorHistory>) {

        for (i in 0..sensorList.size - 1) {
            filterList!!.add(sensorList.get(i).sensor_name)
        }
        if (sensorList.size > 0) {
            txtFilter.text = sensorList.get(0).sensor_name

        } else {
            txtFilter.text = "No Data"
        }

        filterDropdownView = ListPopupWindow(mContext)
        filterDropdownView.setAdapter(
            ArrayAdapter(
                mContext,
                android.R.layout.simple_spinner_dropdown_item,
                filterList!!
            )
        )
        filterDropdownView.anchorView = txtFilter
        filterDropdownView.setModal(true)
        txtFilter.post {
            filterDropdownView.setDropDownGravity(Gravity.END)
            filterDropdownView.width = txtFilter.width
//            priorityDropdownView.height = 300
            filterDropdownView.horizontalOffset = -3
        }
        filterDropdownView.setOnItemClickListener { adapterView, view, position, viewId ->
            txtFilter.setText(filterList!!.get(position))
//            sensorModel.mac = sensorList.get(position).mac
            mac = sensorList.get(position).mac
            filterDropdownView.dismiss()
            getChart(
                sensorModel.sensor_type!!,
                txtDate.text.toString(),
                txtDateEnd.text.toString(),
                mac,
                "Asia/Singapore"
            )

/*
            if (filterList!![position].equals(getString(R.string.axis), true)) {

                edHighLimit.text.clear()
                edLowLimit.text.clear()
            } else {

                edXHigh.text.clear()
                edXLow.text.clear()
                edYHigh.text.clear()
                edYLow.text.clear()
                edZHigh.text.clear()
                edZLow.text.clear()
            }
*/
            /*     if (filterList!!.get(position).equals(getString(R.string.current))) {
                     filterType = 2
                     getSensorHistory(sensorModel._id!!, "25/01/2021")
                     lnAxisAlarm.visibility = View.GONE
                 } else if (filterList!!.get(position).equals(resources.getString(R.string.power))) {
                     filterType = 3
                     getSensorHistory(sensorModel._id!!, "25/01/2021")
                     lnAxisAlarm.visibility = View.GONE
                 } else if (filterList!!.get(position).equals(resources.getString(R.string.voltage))) {
                     filterType = 1
                     getSensorHistory(sensorModel._id!!, "25/01/2021")
                     lnAxisAlarm.visibility = View.GONE
                 } else if (filterList!!.get(position)
                         .equals(resources.getString(R.string.temprature))
                 ) {
                     getSensorHistory(sensorModel._id!!, "25/01/2021")
                     lnAxisAlarm.visibility = View.GONE
                     filterType = 4
                 } else {
                     filterType = 5
                     getSensorHistory(sensorModel._id!!, "25/01/2021")
                 }*/


        }
        txtFilter.setOnClickListener {
            Utility.hideKeyboard(mContext)
            filterDropdownView.show()
        }


    }

    private fun getSensorHistory(sensorId: String, s: String) {


    }

    /*checking new code for created api newly */
    fun getSensorHistory1(sensor_type: String, dateStart: String, dateEnd: String, mac: String) {

        CustomProgress.instance.showProgress(mContext, getString(R.string.please_wait), false)
        val call: Call<JsonObject> = apiInterface.sensor_history(
            sensor_type,
            dateStart,
            dateEnd,
            mac,
            Utility.getCurrentTimeZone()
        )
        call.enqueue(object : Callback<JsonObject> {

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                CustomProgress.instance.hideProgress()
                val jsonObject = JSONObject(response.body().toString())
                val statuscode = jsonObject.optInt("status_code")
                if (statuscode == 200) {

                    if (jsonObject.optBoolean("status")) {
                        val data = jsonObject.optJSONObject("data")
//                        initChart(data)
                    }


                } else if (statuscode == 403) {

                    Utility().showInactiveDialog(
                        mContext,
                        resources.getString(R.string.app_name),
                        jsonObject.optString("message")
                    )

                } else {
                    Utility().showOkDialog(
                        mContext,
                        resources.getString(R.string.app_name),
                        jsonObject.optString("message")
                    )
                }


            }


            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                Utility().showOkDialog(
                    mContext,
                    resources.getString(R.string.app_name),
                    getString(R.string.error_something_is_wrong_ln)
                )
            }

        })
    }

    //code 21feb 2022 changes

    fun getSensorHistory2(sensor_type: String) {
        sensorList.clear()
        CustomProgress.instance.showProgress(mContext, getString(R.string.please_wait), false)
        val call: Call<JsonObject> = apiInterface.sensor_history(sensor_type)
        call.enqueue(object : Callback<JsonObject> {

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                CustomProgress.instance.hideProgress()
                val jsonObject = JSONObject(response.body().toString())
                val statuscode = jsonObject.optInt("status_code")
                if (statuscode == 200) {

                    if (jsonObject.optBoolean("status")) {
                        val data = jsonObject.optJSONObject("data")
                        val jsonArray = data.optJSONArray("sensor_history_data")
                        Log.e("JSONarray", "=" + jsonArray);
                        sensorList.addAll(
                            Gson().fromJson(
                                jsonArray.toString(),
                                Array<SensorHistory>::class.java
                            ).toList()
                        )
//                        initChart(data)
                    }
                    SetFilter(sensorList)

                    if (sensorList.size != 0) {
                        mac = sensorList.get(0).mac
                        getChart(
                            sensorModel.sensor_type!!,
                            txtDate.text.toString(),
                            txtDateEnd.text.toString(),
                            mac,
                            "Asia/Singapore"
                        )

                    } else {
                        getChart(
                            sensorModel.sensor_type!!,
                            txtDate.text.toString(),
                            txtDateEnd.text.toString(),
                            mac,
                            "Asia/Singapore"
                        )

                    }

                    /*    getChart(
                            "EMETER",
                            "03-08-2022",
                            "03-08-2022",
                            "B0F8932BC0EE",
                            "Asia/Singapore"
                        )*/

                    /*   getChart(
                           "BLE",
                           "03-15-2022",
                           "03-15-2022",
                           "AC233FA21569",
                           "Asia/Singapore"
                       )*/

                } else if (statuscode == 403) {

                    Utility().showInactiveDialog(
                        mContext,
                        resources.getString(R.string.app_name),
                        jsonObject.optString("message")
                    )

                } else {
                    Utility().showOkDialog(
                        mContext,
                        resources.getString(R.string.app_name),
                        jsonObject.optString("message")
                    )
                }


            }


            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                Utility().showOkDialog(
                    mContext,
                    resources.getString(R.string.app_name),
                    getString(R.string.error_something_is_wrong_ln)
                )
            }

        })
    }


    fun getChart(
        sensortype: String,
        dateStart: String,
        dateEnd: String,
        mac: String,
        timezone: String
    ) {

        CustomProgress.instance.showProgress(mContext, getString(R.string.please_wait), false)
        val call: Call<JsonObject> = apiInterface.sensorChart1(
            sensortype,
            dateStart,
            dateEnd,
            mac,
            timezone
        )
        call.enqueue(object : Callback<JsonObject> {

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                CustomProgress.instance.hideProgress()
                val jsonObject = JSONObject(response.body().toString())
                val statuscode = jsonObject.optInt("status_code")
                if (statuscode == 200) {

                    if (jsonObject.optBoolean("status")) {
                        val data = jsonObject.optJSONObject("data")
                        initChart(data)


                    }


                } else if (statuscode == 403) {

                    Utility().showInactiveDialog(
                        mContext,
                        resources.getString(R.string.app_name),
                        jsonObject.optString("message")
                    )

                } else {
                    Utility().showOkDialog(
                        mContext,
                        resources.getString(R.string.app_name),
                        jsonObject.optString("message")
                    )
                }


            }


            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                Utility().showOkDialog(
                    mContext,
                    resources.getString(R.string.app_name),
                    getString(R.string.error_something_is_wrong_ln)
                )
            }

        })
    }


    fun setAccelerationAlarm() {
        Log.e("AccelerationAlarm>sensor_id>", sensorModel._id)
        Log.e("AccelerationAlarm>type>", filterType)
        Log.e("AccelerationAlarm>limit_high>", edHighLimit.text.toString().trim())
        Log.e("AccelerationAlarm>limit_low>", edLowLimit.text.toString().trim())

        val call: Call<JsonObject> = apiInterface.acceleration_alarm(
            sensorModel._id!!,
            filterType,
            edLowLimit.text.toString().trim(),
            edHighLimit.text.toString().trim()
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.e("AccelerationAlarm>Response", "=" + response.body().toString());
                if (response.code() == 200) {
                    val jsonObject = JSONObject(response.body().toString())
                    if (jsonObject.optBoolean("status")) {
                        Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG)
                            .show()
                        getSensorHistory(
                            sensorModel._id!!, txtDate.text.toString()

                        )//"25/01/2021"
//                        Utility().showOkDialog(mContext, resources.getString(R.string.app_name), jsonObject.optString("message"))
                    } else {
                        Utility().showOkDialog(
                            mContext,
                            resources.getString(R.string.app_name),
                            jsonObject.optString("message")
                        )
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Utility().showOkDialog(
                    mContext,
                    resources.getString(R.string.app_name),
                    getString(R.string.error_something_is_wrong_ln)
                )
            }
        })


    }

    fun setBeaconAlarm() {
        Log.e("BeaconAlarm>sensor_id>", sensorModel._id)
        Log.e("BeaconAlarm>type>", filterType)
        Log.e("BeaconAlarm>limit_high>", edHighLimit.text.toString().trim())
        Log.e("BeaconAlarm>limit_low>", edLowLimit.text.toString().trim())
        Log.e("BeaconAlarm>x_low>", edXLow.text.toString().trim())
        Log.e("BeaconAlarm>x_high>", edXHigh.text.toString().trim())
        Log.e("BeaconAlarm>y_low>", edYLow.text.toString().trim())
        Log.e("BeaconAlarm>y_high>", edYHigh.text.toString().trim())
        Log.e("BeaconAlarm>z_low>", edZLow.text.toString().trim())
        Log.e("BeaconAlarm>z_high>", edZHigh.text.toString().trim())

        val call: Call<JsonObject> = apiInterface.temperature_alarm(
            sensorModel._id!!,
            filterType,
            edLowLimit.text.toString().trim(),
            edHighLimit.text.toString().trim(),
            edXLow.text.toString().trim(),
            edXHigh.text.toString().trim(),
            edYLow.text.toString().trim(),
            edYHigh.text.toString().trim(),
            edZLow.text.toString().trim(),
            edZHigh.text.toString().trim()
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.e("BeaconAlarm>Response", "=" + response.body().toString());
                if (response.code() == 200) {
                    val jsonObject = JSONObject(response.body().toString())
                    if (jsonObject.optBoolean("status")) {
                        Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG)
                            .show()
                        getSensorHistory(
                            sensorModel._id!!,/*txtDate.text.toString()*/
                            "25/01/2021"
                        )
//                        Utility().showOkDialog(mContext, resources.getString(R.string.app_name), jsonObject.optString("message"))
                    } else {
                        Utility().showOkDialog(
                            mContext,
                            resources.getString(R.string.app_name),
                            jsonObject.optString("message")
                        )
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Utility().showOkDialog(
                    mContext,
                    resources.getString(R.string.app_name),
                    getString(R.string.error_something_is_wrong_ln)
                )
            }
        })
    }

    fun getDate(milliSeconds: Long): String? {
        // Create a DateFormatter object for displaying date in specified format.
        val myDate = Date(milliSeconds)
        val formatter = SimpleDateFormat("hh:mm a")
        val myTime = formatter.format(myDate)
        return myTime;
    }
}
