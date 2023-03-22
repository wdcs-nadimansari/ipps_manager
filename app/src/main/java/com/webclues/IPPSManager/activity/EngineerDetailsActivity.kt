package com.webclues.IPPSManager.activity

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentTransaction
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.webclues.IPPSManager.R
import com.webclues.IPPSManager.adapter.JobOrderAdapter
import com.webclues.IPPSManager.fragments.EngineerList.EngineerCompletedFragment
import com.webclues.IPPSManager.fragments.EngineerList.EngineerJobRequestFragment
import com.webclues.IPPSManager.fragments.EngineerList.EngineerWorkOrderFragment
import com.webclues.IPPSManager.utility.Content
import com.webclues.IPPSManager.utility.CustomViewPager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat.ivBack
import kotlinx.android.synthetic.main.activity_engineer_details.*
import kotlinx.android.synthetic.main.adp_engineerlist.txtEngineerName


class EngineerDetailsActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var edtEmail: EditText
    lateinit var edtPosition: EditText
    lateinit var edtPhoneNumber: EditText
    lateinit var edtCompny: EditText
    lateinit var fragment: Fragment
    lateinit var fragmentManager: FragmentManager
    lateinit var fragmentTransaction: FragmentTransaction
    lateinit var frameLayout: FrameLayout
    lateinit var tabLayout: TabLayout
    lateinit var viewpager: CustomViewPager
    lateinit var tvpriority: TextView
    lateinit var lnPriority: LinearLayout
    private var prioritytypes: ArrayList<String>? = arrayListOf()
    private lateinit var priorityDropdownView: ListPopupWindow
    lateinit var adapter: ViewpagerAdapter
    var engineer_id: Int = 0
    lateinit var engineer_name: String
    lateinit var engineer_position: String
    lateinit var engineer_pic: String
    lateinit var engineer_email: String
    lateinit var engineer_company: String
    lateinit var engineer_number: String
    lateinit var context: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_engineer_details)
        context = this
        initview()
    }

    private fun initview() {

        edtEmail = findViewById(R.id.edtEmailAddress)
        edtPosition = findViewById(R.id.edtPosition)
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber)
        edtCompny = findViewById(R.id.edtCompany)



        if (intent.extras != null) {
            engineer_id = intent.getIntExtra(Content.ENGINEER_ID, 0)
            engineer_name = intent.getStringExtra(Content.ENGINEER_NAME)
            engineer_number = intent.getStringExtra(Content.ENGINEER_NUMBER)
            engineer_position = intent.getStringExtra(Content.ENGINEER_POSITION)
            engineer_company = intent.getStringExtra(Content.ENGINEER_COMPANY)
            engineer_email = intent.getStringExtra(Content.ENGINEER_EMAIL)
            engineer_pic = intent.getStringExtra(Content.ENGINEER_PROFILE_PIC)

            txtEngineerName.setText(engineer_name)
            Picasso.get().load(engineer_pic).placeholder(R.drawable.ic_placeholder_profile)
                .error(R.drawable.ic_placeholder_profile).into(ivEngineerprofile)
        }
        frameLayout = findViewById(R.id.frame_container)
        tabLayout = findViewById(R.id.tablayout)
        viewpager = findViewById(R.id.viewpager)
        tvpriority = findViewById(R.id.tvpriority)
        lnPriority = findViewById(R.id.lnPriority)
        viewpager.setPagingEnabled(false)

        ivBack.setOnClickListener(this)


        fragment = EngineerJobRequestFragment()
        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_container, fragment)
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        fragmentTransaction.commit()



        setData()
        Setpriority()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab!!.position) {
                    0 -> {
                        fragment = EngineerJobRequestFragment()
                    }
                    1 -> {
                        fragment = EngineerWorkOrderFragment()
                    }
                    2 -> {
                        fragment = EngineerCompletedFragment()
                    }

                }
                var fm: FragmentManager = supportFragmentManager
                var ft = fm.beginTransaction()
                ft.replace(R.id.frame_container, fragment)
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                ft.commit()
            }

        })

    }


    private fun setData() {
        edtEmail.keyListener = null
        edtPosition.keyListener = null
        edtCompny.keyListener = null
        edtPhoneNumber.keyListener = null

        val face = Typeface.createFromAsset(
            getAssets(),
            "font/lato_bold.ttf"
        )


        edtEmail.setTextColor(ContextCompat.getColor(this, R.color.fontColorBlack))
        edtEmail.setTypeface(face)
        edtPosition.setTextColor(ContextCompat.getColor(this, R.color.fontColorBlack))
        edtPosition.setTypeface(face)
        edtPhoneNumber.setTextColor(ContextCompat.getColor(this, R.color.fontColorBlack))
        edtPhoneNumber.setTypeface(face)
        edtCompny.setTextColor(ContextCompat.getColor(this, R.color.fontColorBlack))
        edtCompny.setTypeface(face)

        edtEmail.setText(engineer_email)
        edtPosition.setText(engineer_position)
        edtPhoneNumber.setText(engineer_number)
        edtCompny.setText(engineer_company)


        val typeface = Typeface.createFromAsset(assets, "font/lato_semibold.ttf")
        tabLayout.applyFont(typeface)
    }


    class ViewpagerAdapter(var context: EngineerDetailsActivity, fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val mFragmentList: ArrayList<Fragment> = arrayListOf()
        private val mFragmentTitleList: ArrayList<String> = ArrayList()

        override fun getCount(): Int {
            return mFragmentList.size
        }

        override fun getItem(position: Int): Fragment {


            return mFragmentList.get(position)


        }

        private fun getcurrentfragment(position: Int): Fragment {
            val position = context.viewpager.currentItem
            val fragmet = context.adapter.mFragmentList.get(position)
            return fragmet


        }

        fun addfragment(fragment: Fragment, title: String) {

            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)

        }

        override fun getPageTitle(position: Int): CharSequence? {


            var title: String? = null
            if (position == 0) {
                title = context.resources.getString(R.string.job_request)
            } /*else if (position == 1) {
                title = context.resources.getString(R.string.job_request)

            } */
            else if (position == 1) {
                title = context.resources.getString(R.string.workorder)

            } else if (position == 2) {
                title = context.resources.getString(R.string.completed)

            }/* else if (position == 3) {
                title = context.resources.getString(R.string.completed)

            }*/

//            return mFragmentTitleList.get(position)
            /*   val mTypeface = Typeface.createFromAsset(context.getAssets(), "font/lato_semibold.ttf")

               val typefaceSpan = CalligraphyTypefaceSpan(mTypeface)
               val s = SpannableStringBuilder()
               s.append(title)
               s.setSpan(typefaceSpan, 0, title!!.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
               return SpannableString.valueOf(s)*/

            return title
        }


        /* override fun onClick(v: View?) {

             when (v?.id) {
                 R.id.ivBack -> {
                     onBackPressed()
                 }
             }
         }*/
    }

    private fun Setpriority() {         //SetPriority in list
        prioritytypes =
            getResources().getStringArray(R.array.priority_item).toCollection(ArrayList())
        priorityDropdownView = ListPopupWindow(this)
        priorityDropdownView.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                prioritytypes!!
            )
        )
        priorityDropdownView.anchorView = lnPriority
        priorityDropdownView.setModal(true)
        lnPriority.post {
            priorityDropdownView.setDropDownGravity(Gravity.END)
            priorityDropdownView.width = 350

            priorityDropdownView.horizontalOffset = -15
        }
        priorityDropdownView.setOnItemClickListener { adapterView, view, position, viewId ->
            tvpriority.setText(prioritytypes!!.get(position))
            priorityDropdownView.dismiss()

            val mintent = Intent("ReconfirmBroadCast")
            if (prioritytypes!!.get(position).equals(getString(R.string.pending))) {
                Content.STATUS_PRIORITY = getString(R.string.pending)
            } else if (prioritytypes!!.get(position).equals(getString(R.string.low))) {
                Content.STATUS_PRIORITY = getString(R.string.low)
            } else if (prioritytypes!!.get(position).equals(getString(R.string.medium))) {
                Content.STATUS_PRIORITY = getString(R.string.medium)
            } else if (prioritytypes!!.get(position).equals(getString(R.string.high))) {
                Content.STATUS_PRIORITY = getString(R.string.high)
            } else {
                Content.STATUS_PRIORITY = getString(R.string.all)
            }
            mintent.putExtra(Content.PRIORITY, Content.STATUS_PRIORITY)

            LocalBroadcastManager.getInstance(this).sendBroadcast(mintent)

        }
        tvpriority.setOnClickListener {
            priorityDropdownView.show()
        }

    }

    fun TabLayout.applyFont(typeface: Typeface) {
        val viewGroup = getChildAt(0) as ViewGroup
        val tabsCount = viewGroup.childCount
        for (j in 0 until tabsCount) {
            val viewGroupChildAt = viewGroup.getChildAt(j) as ViewGroup
            val tabChildCount = viewGroupChildAt.childCount
            for (i in 0 until tabChildCount) {
                val tabViewChild = viewGroupChildAt.getChildAt(i)
                if (tabViewChild is TextView) {
                    tabViewChild.typeface = typeface
                }
            }
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                Content.FILTER_PRIORITY = Content.ALL
                onBackPressed()
            }
        }
    }

}


