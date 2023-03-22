package com.webclues.IPPSManager.utility;


public class Log {

    public static void e(Object arg1, Object arg2) {
        try {
            android.util.Log.e(arg1.toString(), "-->" + arg2.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void mE(Object arg1, Object arg2) {
        try {
            android.util.Log.e(arg1.toString(), "-->" + arg2.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void e4(Object arg1, Object arg2) {
//        android.util.Log.e(arg1.toString(), "-VM->>" + arg2.toString());
    }

    public static void e5(Object arg1, Object arg2) {
        android.util.Log.e(arg1.toString(), "-->>" + arg2.toString());
    }

    public static void mError(Object arg1, Object arg2) {
        try {
            //android.util.Log.e(arg1.toString(), "-->>" + arg2.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void mEr(Object arg1, Object arg2) {
        try {
//            android.util.Log.e(arg1.toString(), "-->>" + arg2.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void mErr(Object arg1, Object arg2) {
        try {
//            android.util.Log.e(arg1.toString(), "-->>" + arg2.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void Err(Object arg1, Object arg2) {
        try {
//            android.util.Log.e(arg1.toString(), "-->>" + arg2.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void makeerror(Object arg1, Object arg2) {
        try {
//            android.util.Log.e(arg1.toString(), "-->>" + arg2.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void eer(Object arg1, Object arg2) {
        try {
//            android.util.Log.e(arg1.toString(), "-->>" + arg2.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
