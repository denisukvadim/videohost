package com.example.videohost

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.example.videohost.WifiReceiver.Companion.connected


class WifiReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val mgr = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = mgr
            .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        connected = networkInfo != null && networkInfo.isConnected
    }

    companion object {
        var connected = false
    }
}

fun isConnected(): Boolean {
    return connected
}