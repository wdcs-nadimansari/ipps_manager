package com.webclues.IPPSManager.utility

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.net.Uri
import android.provider.Settings
import android.text.InputFilter
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.google.gson.Gson
import com.webclues.IPPSManager.Modelclass.UserRespone
import com.webclues.IPPSManager.R
import com.webclues.IPPSManager.activity.LoginActivity
import com.webclues.IPPSManager.application.mApplicationClass
import com.webclues.IPPSManager.view.MaterialAutoCompleteTextView
import com.webclues.IPPSManager.view.MaterialEditText
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class Utility {
    companion object {


        fun ValidateEmailTask(email: String): Boolean {
            val pattern: Pattern
            val matcher: Matcher
            val EMAIL_PATTERN =
                "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
            pattern = Pattern.compile(EMAIL_PATTERN)
            matcher = pattern.matcher(email)
            return matcher.matches()
        }

        fun convertDpToPixel(context: Context, sizeInDp: Int): Int {
            val scale = context.getResources().getDisplayMetrics().density
            return (sizeInDp * scale + 0.5f).toInt()
        }

        fun closeKeyboard(activity: Activity) {
            val inputManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                activity.getCurrentFocus()?.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS
            )

        }

        fun isValidPassword(s: String): Boolean {
            val PASSWORD_PATTERN = Pattern.compile(
                "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d\$@\$!%*#?&]{8,30}$"
            )

            return !TextUtils.isEmpty(s) && PASSWORD_PATTERN.matcher(s).matches()
        }


        fun bitmapToFile(
            bitmap: Bitmap?,
            addProfileImageScreen: Activity
        ): Any {
            val wrapper = ContextWrapper(addProfileImageScreen)

            // Initialize a new file instance to save bitmap object
            var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
            file = File(file, "${UUID.randomUUID()}.jpg")

            try {
                // Compress the bitmap and save in jpg format
                val stream: OutputStream = FileOutputStream(file)
                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                stream.flush()
                stream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            // Return the saved bitmap uri
            return Uri.parse(file.absolutePath)
        }

        fun hideSoftKeyBoard(context: Context) {
            try {
                val v = (context as Activity).currentFocus
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v!!.windowToken, 0)
            } catch (e: Exception) {
                // TODO: handle exception
                e.printStackTrace()
            }
        }

        fun showSiftKeyBoard(context: Context, view: View) {
            val v = (context as Activity).currentFocus
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInputFromWindow(
                view!!.windowToken,
                InputMethodManager.SHOW_FORCED, 0
            )
        }


        fun deviceID(context: Context): String {
            return Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
            )
        }


        /*   fun showAlertDialog(context: Context,yesClick: DialogInterface.OnClickListener) {
               val builder = AlertDialog.Builder(context)

               builder.setTitle(com.webclues.subbieme.R.string.app_name)
               //set message for alert dialog
               builder.setMessage(com.webclues.subbieme.R.string.close_alert)
               builder.setIcon(com.webclues.subbieme.R.drawable.appicon)

               //performing positive action
               builder.setPositiveButton(context.resources.getString(com.webclues.subbieme.R.string.yes),yesClick)
               //performing negative action
               builder.setNegativeButton(context.resources.getString(com.webclues.subbieme.R.string.no)) { dialogInterface, which ->
                   dialogInterface.dismiss()
               }
               // Create the AlertDialog
               val alertDialog: AlertDialog = builder.create()

               alertDialog.setCancelable(false)
               alertDialog.show()
           }*/

        /*  fun isValidEmail(target: CharSequence?): Boolean {
              return if (TextUtils.isEmpty(target)) {
                  false
              } else {
                  Patterns.EMAIL_ADDRESS.matcher(target).matches()
              }
          }*/


        fun checkIsMobileValid(string: String): Boolean {
            val contactPattern = "[0-9]+"
            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
            var reply = false
            //        if (string.matches(contactPattern)) {
            reply = if (string.length < 8 || string.length > 14) {
                false
                //                Utility.ShowOk(mContext, getString(R.string.app_name), getString(R.string.error_msg_mobile_not_valid));
            } else {
                true
            }
            //        }
            return reply
        }

        fun getDate(date_string: Long): String? {
            var datetime = ""
            if (date_string != 0L) {
                val date_ =
                    Date(date_string) // *1000 is to convert seconds to milliseconds
                val dateformat =
                    SimpleDateFormat("dd MMM yyyy")
                datetime = dateformat.format(date_)
            }
            return datetime
        }

        fun getTime(date_string: Long): String? {
            var datetime = ""
            if (date_string != 0L) {
                val date_ =
                    Date(date_string) // *1000 is to convert seconds to milliseconds
                val dateformat =
                    SimpleDateFormat("hh:mm a")
                datetime = dateformat.format(date_)
            }
            return datetime
        }

        fun getDatewithTimestamp(time: Long): String {
            val date = Date(time)
            val sdf = SimpleDateFormat(Content.DATE_FORMAT, Locale.ENGLISH)

            return sdf.format(date)
        }

        fun getDateTime(timestamp: Long): String {
            try {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = timestamp
                val sdf = SimpleDateFormat(Content.TAG_DateTimeFormate, Locale.getDefault())
                val currenTimeZone = calendar.time as Date
                return sdf.format(currenTimeZone)
            } catch (e: Exception) {
            }

            return ""
        }

        fun getCurrentTimeZone(): String {
            val timeZone: TimeZone = TimeZone.getDefault()
            return timeZone.id
        }



        fun hideKeyboard(ctx: Context) {
            val inputManager =
                ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            // check if no ic_view has focus:
            val v = (ctx as Activity).currentFocus ?: return
            inputManager.hideSoftInputFromWindow(v.windowToken, 0)
        }

        fun showKeyboard(ctx: Context) {
            val imm =
                ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }

        var CharachterFilter =
            InputFilter { cs, start, end, spanned, dStart, dEnd ->
                // TODO Auto-generated method stub
                if (cs == "") { // for backspace
                    return@InputFilter cs
                }
                if (cs.toString().matches(Regex("[a-zA-Z ]+"))) {
                    cs
                } else ""
            }
    }

    lateinit var context: Context
    fun Utility() {
        this.context = context
    }

    fun deviceWidth(): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.x
    }

    fun deviceHeight(): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.y
    }




    fun showOkDialog(context: Context, stitle: String, smessage: String) {

        val dialog = Dialog(context)
        val window = dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        window.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.getWindow()!!.setBackgroundDrawableResource(R.drawable.rectangle_background);
        dialog.setContentView(R.layout.popup_ok)
        dialog.show()

        val title = dialog.findViewById(R.id.txtTitle) as TextView
        val text_message = dialog.findViewById(R.id.txtMessage) as TextView
        val text_yes = dialog.findViewById(R.id.txtOk) as TextView
        title.setText(stitle)
        text_message.setText(smessage)
        text_yes.setText(context.resources.getString(R.string.ok))
        text_yes.setOnClickListener({ dialog.dismiss() })
    }

    fun showInactiveDialog(context: Context, stitle: String, smessage: String) {

        val dialog = Dialog(context)
        val window = dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        window.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.getWindow()!!.setBackgroundDrawableResource(R.drawable.rectangle_background);
        dialog.setContentView(R.layout.popup_ok)
        dialog.show()

        val title = dialog.findViewById(R.id.txtTitle) as TextView
        val text_message = dialog.findViewById(R.id.txtMessage) as TextView
        val text_yes = dialog.findViewById(R.id.txtOk) as TextView
        title.setText(stitle)
        text_message.setText(smessage)
        text_yes.setText(context.resources.getString(R.string.ok))
        text_yes.setOnClickListener({
//            mApplicationClass.getInstance()?.removeFCMToken()
            TinyDb(context).clear(context)
            context.startActivity(Intent(context, LoginActivity::class.java))
            (context as Activity).finish()
        })
    }



    fun isValidFirstName(context: Context, editText: MaterialEditText): Boolean {   //FirstName validation

        var isValid = true
        if (editText.text!!.isEmpty()) {
//            editText.requestFocus()
            editText.error = context.resources.getString(R.string.str_empty_firstName)
            isValid = false
        } else if (editText.text!!.trim().length < 2) {
//            editText.requestFocus()
            editText.error = context.resources.getString(R.string.str_invalid_firstName)
            isValid = false
        }
        return isValid
    }

    fun isValidLastName(context: Context, editText: MaterialEditText): Boolean {       //LastName Validation

        var isValid = true
        if (editText.text!!.isEmpty()) {
//            editText.requestFocus()
            editText.error = context.resources.getString(R.string.str_empty_lastName)
            isValid = false
        } else if (editText.text!!.trim().length < 2) {
//            editText.requestFocus()
            editText.error = context.resources.getString(R.string.str_invalid_lastName)
            isValid = false
        }
        return isValid
    }

    fun isGeneralValidation(context: Context, editText: MaterialEditText): Boolean {

        var isValid = true
        if (editText.text!!.isEmpty()) {
//            editText.requestFocus()
            editText.error = context.resources.getString(R.string.str_empty_lastName)
            isValid = false
        }
        return isValid
    }

    fun isEmailValid(context: Context, editText: MaterialEditText): Boolean {      //Email Validation
        var isValid = true

        if (editText.text!!.isEmpty()) {
            editText.error = context.resources.getString(R.string.str_val_email_empty)
//            editText.requestFocus()
            isValid = false
        } else {
            val pattern: Pattern
            val matcher: Matcher
            val EMAIL_PATTERN =
                "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
            pattern = Pattern.compile(EMAIL_PATTERN)
            matcher = pattern.matcher(editText.text)
            if (!matcher.matches()) {
                isValid = false
                editText.error = context.resources.getString(R.string.str_val_emal)
//                editText.requestFocus()
            } else {
                isValid = true
            }

        }
        return isValid
    }

    fun isPhoneValid(context: Context, editText: MaterialEditText): Boolean {     //PhoneNumber Validation

        var isValid = true
        if (editText.text!!.isEmpty()) {
            editText.error = context.resources.getString(R.string.str_val_phone_empty)
//            editText.requestFocus()
            isValid = false
        } else if (editText.text!!.length < 8) {
            editText.error = context.resources.getString(R.string.str_val_phone_valid)
//            editText.requestFocus()
            isValid = false
        }
        return isValid
    }

    fun isPositionValid(context: Context, editText: MaterialAutoCompleteTextView): Boolean {      //Position Validation

        var isValid = true
        if (editText.text!!.isEmpty()) {
            editText.error = context.resources.getString(R.string.str_val_position)
//            editText.requestFocus()
            isValid = false
        }
        return isValid
    }

    fun isCompanyValid(context: Context, editText: MaterialAutoCompleteTextView): Boolean {      //Company Validation

        var isValid = true
        if (editText.text!!.isEmpty()) {
            editText.error = context.resources.getString(R.string.str_val_company)
//            editText.requestFocus()
            isValid = false
        }
        return isValid
    }

    fun isCurrentPasswordValid(context: Context, editText: MaterialEditText): Boolean {        //Currentpassword validation
        if (editText.text!!.isEmpty()) {
            editText.error = context.getResources().getString(R.string.str_current_pass_empty)
//            editText.requestFocus()
            return false
        } else if (!editText.text.isNullOrEmpty() && editText.text!!.length < 6 || editText.text!!.length > 15) {
            editText.error = context.resources.getString(R.string.str_val_pass_length)
//            editText.requestFocus()
            return false
        } else {
            return true
        }

    }


    fun isPasswordValid(context: Context, editText: MaterialEditText): Boolean {          //password validation

        var isValid = true
        if (editText.text!!.isEmpty()) {
            editText.error = context.resources.getString(R.string.str_val_pass_empty)
//            editText.requestFocus()
            isValid = false
        } else if (!editText.text.isNullOrEmpty() && editText.text!!.length < 6 || editText.text!!.length > 15) {
            editText.error = context.resources.getString(R.string.str_val_pass_length)
//            editText.requestFocus()
            isValid = false
        }
        return isValid
    }


    fun isPassvalid(
        context: Context,
        edtPass: MaterialEditText
    ): Boolean {
        var isValid = true


        if (edtPass.text!!.isEmpty()) {
            edtPass.error = context.resources.getString(R.string.str_val_new_pass_empty)
//            edtPass.requestFocus()
            isValid = false
        } else if (!edtPass.text.isNullOrEmpty() && edtPass.text!!.length < 6 || edtPass.text!!.length > 15) {
            edtPass.error = context.resources.getString(R.string.str_val_newpass_length)
//            edtPass.requestFocus()
            isValid = false
        }

        return isValid
    }

    fun isConfirmPassvalid(                            //Confirmpasssword validation
        context: Context, edtnewpass: MaterialEditText,
        edtConfPass: MaterialEditText
    ): Boolean {
        var isValid = true
        if (edtConfPass.text!!.isEmpty()) {
            edtConfPass.error = context.resources.getString(R.string.str_val_confirm_pass_empty)
//            edtPass.requestFocus()
            isValid = false
        } else if (!edtConfPass.text.isNullOrEmpty() && edtConfPass.text!!.length < 6 || edtConfPass.text!!.length > 15) {
            edtConfPass.error = context.resources.getString(R.string.str_val_conf_pass_length)
//            edtConfPass.requestFocus()
            isValid = false
        } else if (!edtnewpass.text.toString().trim()!!.equals(edtConfPass.text.toString().trim())) {
            edtConfPass.error = context.resources.getString(R.string.str_val_pass_conf_match)
            isValid = false
        }
        return isValid
    }

    fun isSubjectValid(context: Context, editText: MaterialEditText): Boolean {       //Subjet validation
        var isvalid = true
        if (editText.text!!.isEmpty()) {
            editText.error = context.resources.getString(R.string.error_subject_blank)
//            editText.requestFocus()
            isvalid = false
        }
        return isvalid
    }

    fun isMessagevalid(context: Context, editText: MaterialEditText): Boolean {         //Message validation
        var isvalid = true
        if (editText.text!!.isEmpty()) {
            editText.error = context.resources.getString(R.string.error_message_blank)
//            editText.requestFocus()
            isvalid = false
        }
        return isvalid

    }
    fun getCompany_ID(context: Context):String{
        val userData = Gson().fromJson(TinyDb(context).getString(Content.USER_DATA), UserRespone::class.java)
        var id = ""
        if(userData.company.company_id==0 && TinyDb(context).getString(Content.USER_DATA).equals(TinyDb.DEFULT_STRING)){
            id="0"
        }else
        {
            id=userData.company.company_id.toString()
        }
        return id
    }
}