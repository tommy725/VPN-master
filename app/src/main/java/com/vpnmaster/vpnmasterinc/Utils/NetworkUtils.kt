package com.vpnmaster.vpnmasterinc.Utils

import android.content.Context
import android.net.wifi.WifiManager
import android.text.TextUtils
import android.util.Log
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*


class NetworkUtils {
    companion object {
        private val TAG = NetworkUtils::class.java.canonicalName

        fun ipAddress(context: Context): String {
            try {
                val en = NetworkInterface
                        .getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val intf = en.nextElement()
                    val enumIpAddr = intf
                            .inetAddresses
                    while (enumIpAddr.hasMoreElements()) {
                        val inetAddress = enumIpAddr.nextElement()
                        println("ip1--:$inetAddress")
                        println("ip2--:" + inetAddress.hostAddress)
                        if (!inetAddress.isLoopbackAddress && inetAddress is Inet6Address) {
                            return inetAddress.hostAddress.toString()
                        }
                    }
                }
            } catch (ex: java.lang.Exception) {
                Log.e("IP Address", ex.toString())
            }
            return "null"
            }

        fun getWifiIp(context: Context): String? {
            val mWifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (mWifiManager != null && mWifiManager.isWifiEnabled) {
                val ip = mWifiManager.connectionInfo.ipAddress
                return ((ip and 0xFF).toString() + "." + (ip shr 8 and 0xFF) + "." + (ip shr 16 and 0xFF) + "."
                        + (ip shr 24 and 0xFF))
            }
            return null
        }

        fun getNetworkInterfaceIpAddress(): String? {
            try {
                val en: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val networkInterface: NetworkInterface = en.nextElement()
                    val enumIpAddr: Enumeration<InetAddress> = networkInterface.getInetAddresses()
                    while (enumIpAddr.hasMoreElements()) {
                        val inetAddress: InetAddress = enumIpAddr.nextElement()
                        if (!inetAddress.isLoopbackAddress() && inetAddress is Inet4Address) {
                            val host: String = inetAddress.getHostAddress()
                            if (!TextUtils.isEmpty(host)) {
                                return host
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                Log.e("IP Address", "getLocalIpAddress", ex)
            }
            return null
        }


    }



}