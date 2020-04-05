/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.example.videohost

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.PowerManager
import android.text.format.Formatter
import android.view.View
import com.devbrackets.android.exomedia.listener.OnPreparedListener
import fi.iki.elonen.NanoHTTPD
import kotlinx.android.synthetic.main.main_activity.*


/**
 * Loads [MainFragment].
 */
class MainActivity : Activity(), OnPreparedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

            video_view.setOnPreparedListener(this);
        val wm = getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ip: String = Formatter.formatIpAddress(wm.connectionInfo.ipAddress)
        wm.isWifiEnabled
        if(isConnected()){
            ip_address.text = "${ip}:65007"
            wifi_name.text = wm.connectionInfo.ssid
        }
        if(wm.isWifiEnabled) {
            ip_address.text = "http://${ip}:65007"
            wifi_name.text = wm.connectionInfo.ssid
        } else {
            ip_address.text = "0.0.0.0:65007"
            wifi_name.text = "waiting ..."
        }


            val server = object : NanoHTTPD(65007) {
                override fun serve(session: IHTTPSession): Response = when (session.uri) {
                    "/" -> newFixedLengthResponse("HELLO_WORLD")
                    "/playlist" -> {
                        val videoLink = session.parameters["link"]?.get(0)

                            // get the POST body
                        Thread {

                            runOnUiThread {
                                openPlayer(videoLink)
                            }

                        }.start()
                        newFixedLengthResponse(Response.Status.OK, "application/json", "good")
                    }
                    "/sf" -> {
                        Thread {
                            runOnUiThread {
                                forward()
                            }
                        }.start()
                        newFixedLengthResponse(Response.Status.OK, "application/json", "good")
                    }
                    "/sb" -> {
                        Thread {
                            runOnUiThread {
                                back()
                            }
                        }.start()
                        newFixedLengthResponse(Response.Status.OK, "application/json", "good")
                    }
                    "/goTo" -> {
                        val minutes = session.parameters["minutes"]?.get(0)
                        val min = minutes.toString()
                        // get the POST body
                        Thread {

                            runOnUiThread {
                                goToMinutes(min.toLong())
                            }

                        }.start()
                        newFixedLengthResponse(Response.Status.OK, "application/json", "setTime")
                    }
                    "/pause" -> {
                        Thread {
                            runOnUiThread {
                                pausePlayer()
                            }

                        }.start()
                        newFixedLengthResponse(Response.Status.OK, "application/json", "pause")
                    }
                    "/play" -> {
                        Thread {
                            runOnUiThread {
                                playPlayer()
                            }

                        }.start()
                        newFixedLengthResponse(Response.Status.OK, "application/json", "Play")
                    }

                    "/image" -> {
                        newFixedLengthResponse(Response.Status.OK, "image/png", "image")
                    }
                    else -> newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Not found")
                } as Response
            }
            server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false)
    }

    private fun playPlayer() {
        video_view.start()
    }

    fun openPlayer(playlist: String?) {
        ip_address.visibility = View.GONE
        wifi_name.visibility = View.GONE
        video_view.visibility = View.VISIBLE
        val videoUri = Uri.parse(playlist)
        video_view.setVideoURI(videoUri)
    }
    fun forward() {
        video_view.seekTo(video_view.getCurrentPosition() + 10000)
    }
    fun back() {
        video_view.seekTo(video_view.getCurrentPosition() - 10000)
    }
    fun goToMinutes(minutes: Long) {
        video_view.seekTo(minutes * 60 * 1000)
    }
    fun pausePlayer() {
        video_view.pause()
    }


    override fun onPrepared() {
        video_view.start()
    }
}