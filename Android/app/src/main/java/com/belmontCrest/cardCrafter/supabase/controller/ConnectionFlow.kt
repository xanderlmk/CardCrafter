package com.belmontCrest.cardCrafter.supabase.controller

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

fun Context.networkConnectivityFlow() =
    callbackFlow @androidx.annotation.RequiresPermission(
        android.Manifest.permission.ACCESS_NETWORK_STATE
    ) {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                trySend(true)
            }
            override fun onLost(network: Network) {
                super.onLost(network)
                trySend(false)
            }
            override fun onUnavailable() {
                super.onUnavailable()
                trySend(false)
            }
        }
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        // Close callback when no longer collected
        awaitClose { connectivityManager.unregisterNetworkCallback(networkCallback) }
    }