package com.othoba.othobavitals.dashboard

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket

class TvAuthServer(private val context: Context) {
    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
    private var serverSocket: ServerSocket? = null

    private val _credentials = MutableStateFlow<Pair<String, String>?>(null)
    val credentials: StateFlow<Pair<String, String>?> = _credentials

    fun start() {
        Thread {
            try {
                serverSocket = ServerSocket(0)
                val port = serverSocket!!.localPort
                registerService(port)

                while (true) {
                    val client = serverSocket!!.accept()
                    val reader = BufferedReader(InputStreamReader(client.inputStream))
                    val message = reader.readLine()
                    if (message != null) {
                        try {
                            val json = JSONObject(message)
                            val user = json.getString("u")
                            val pass = json.getString("p")
                            _credentials.value = Pair(user, pass)
                        } catch (e: Exception) {
                            Log.e("TvAuthServer", "Error parsing JSON", e)
                        }
                    }
                    client.close()
                }
            } catch (e: Exception) {
                Log.e("TvAuthServer", "Server exception", e)
            }
        }.start()
    }

    private fun registerService(port: Int) {
        val serviceInfo = NsdServiceInfo().apply {
            serviceName = "OthobaVitalsTV"
            serviceType = "_othoba._tcp"
            setPort(port)
        }

        nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(nsdServiceInfo: NsdServiceInfo) {
                Log.d("TvAuthServer", "Service registered with name: \${nsdServiceInfo.serviceName}")
            }
            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                Log.e("TvAuthServer", "Service registration failed: \$errorCode")
            }
            override fun onServiceUnregistered(arg0: NsdServiceInfo) {}
            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {}
        })
    }

    fun stop() {
        try {
            serverSocket?.close()
        } catch (e: Exception) {
            // ignore
        }
    }
}
