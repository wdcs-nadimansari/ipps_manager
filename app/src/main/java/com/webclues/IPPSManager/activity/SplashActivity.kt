package com.webclues.IPPSManager.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.webclues.IPPSManager.R
import com.webclues.IPPSManager.utility.Content
import com.webclues.IPPSManager.utility.Static.Companion.SPLASH_SCREEN_TIME_OUT
import com.webclues.IPPSManager.utility.TinyDb

class SplashActivity : AppCompatActivity() {

    val handler: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        var intent = intent
        handler.postDelayed(Runnable {

            NavigateToInfo()


        }, SPLASH_SCREEN_TIME_OUT.toLong())


    }

    private fun NavigateToInfo() {


        if (TinyDb(this@SplashActivity).getBoolean(Content.IS_LOGIN)) {

            startActivity(Intent(this, MainActivity::class.java))
            finish()

        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }

}
