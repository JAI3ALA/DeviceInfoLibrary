package com.jaibala.deviceinfo

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.text.format.Formatter
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DeviceInfoHelper(this);
    }
    class DeviceInfoHelper(val context: Context) {

        private val deviceModel
            @SuppressLint("DefaultLocale")
            get() = capitalize(
                if (Build.MODEL.toLowerCase().startsWith(Build.MANUFACTURER.toLowerCase())) {
                    Build.MODEL
                } else {
                    "${Build.MANUFACTURER} ${Build.MODEL}"
                }
            )

        private fun capitalize(str: String) = str.apply {
            if (isNotEmpty()) {
                first().run { if (isLowerCase()) toUpperCase() }
            }
        }

        public fun getSystemTimeZone(): String? {
            val tz = TimeZone.getDefault()
            val isDaylite = tz.inDaylightTime(Date())
            val timezone = tz.getDisplayName(isDaylite, TimeZone.SHORT)
            Log.d("TAG ", " " + tz.id)
            return tz.id.toString()
        }

        public fun getAndroidVersion(): String? {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            val release = Build.VERSION.RELEASE
            val sdkVersion = Build.VERSION.SDK_INT
            return "Android SDK: $manufacturer $model $sdkVersion ($release)"
        }


        public fun getScreenResolution(context: Context): String? {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display: Display = wm.defaultDisplay
            val metrics = DisplayMetrics()
            display.getMetrics(metrics)
            val width = metrics.widthPixels
            val height = metrics.heightPixels
            return "{$width,$height}"
        }

        @SuppressWarnings("deprecation")
        public fun getIPAddress(context: Context): String? {
            var iIPv4: String? = ""
            try {
                val wm = context.getSystemService(WIFI_SERVICE) as WifiManager?
                iIPv4 = Formatter.formatIpAddress(wm!!.connectionInfo.ipAddress)
            } catch (e: Exception) {
            }
            return iIPv4
        }

        public fun getAppBuildVersion(context: Context): String? {
            var packageInfo: PackageInfo? = null
            try {
                packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            val version_code = packageInfo!!.versionCode
            val version_name = packageInfo!!.versionName
            var str = " v $version_code ($version_name)".toString()
            return str
        }


        object InternetConnection {
            /**
             * CHECK WHETHER INTERNET CONNECTION IS AVAILABLE OR NOT
             */
            fun checkConnection(context: Context): String {
                val connMgr =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                if (connMgr != null) {
                    val activeNetworkInfo = connMgr.activeNetworkInfo
                    if (activeNetworkInfo != null) { // connected to the internet
                        // connected to the mobile provider's data plan
                        if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
                            // connected to wifi
                            return "WIFI"
                        } else if (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                            return "MOBILE"
                        }
                    }
                }
                return ""
            }
        }

    }
}