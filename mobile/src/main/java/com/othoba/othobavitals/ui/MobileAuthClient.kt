package com.othoba.othobavitals.ui

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.PrintWriter
import java.net.Socket
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MobileAuthClient(private val context: Context) {
    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager

    suspend fun sendCredentials(user: String, pass: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val serviceInfo = discoverService() ?: return@withContext false
            Log.d("MobileAuthClient", "Resolved Service: \${serviceInfo.host}:\${serviceInfo.port}")
            val socket = Socket(serviceInfo.host, serviceInfo.port)
            val writer = PrintWriter(socket.outputStream, true)
            val json = JSONObject().apply {
                put("u", user)
                put("p", pass)
            }
            writer.println(json.toString())
            socket.close()
            true
        } catch (e: Exception) {
            Log.e("MobileAuthClient", "Failed to send credentials", e)
            false
        }
    }

    private suspend fun discoverService(): NsdServiceInfo? =
        suspendCancellableCoroutine { continuation ->
            var resolved = false
            var discoveryListener: NsdManager.DiscoveryListener? = null

            val resolveListener = object : NsdManager.ResolveListener {
                override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                    if (!resolved) {
                        resolved = true
                        continuation.resume(null)
                    }
                }

                override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                    if (!resolved) {
                        resolved = true
                        continuation.resume(serviceInfo)
                    }
                }
            }

            discoveryListener = object : NsdManager.DiscoveryListener {
                override fun onDiscoveryStarted(regType: String) {}
                override fun onServiceFound(service: NsdServiceInfo) {
                    if (service.serviceType.contains("_othoba._tcp") && service.serviceName.contains(
                            "OthobaVitalsTV"
                        )
                    ) {
                        nsdManager.resolveService(service, resolveListener)
                        try {
                            nsdManager.stopServiceDiscovery(this)
                        } catch (e: Exception) {
                            // ignore
                        }
                    }
                }

                override fun onServiceLost(service: NsdServiceInfo) {}
                override fun onDiscoveryStopped(serviceType: String) {}
                override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                    try {
                        nsdManager.stopServiceDiscovery(this)
                    } catch (e: Exception) {
                        // ignore
                    }
                    if (!resolved) {
                        resolved = true
                        continuation.resume(null)
                    }
                }

                override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {}
            }

            try {
                nsdManager.discoverServices(
                    "_othoba._tcp.",
                    NsdManager.PROTOCOL_DNS_SD,
                    discoveryListener
                )
            } catch (e: Exception) {
                continuation.resume(null)
            }
        }
}
