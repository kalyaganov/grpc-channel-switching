package com.example.grpc_channel_switching.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.example.grpc_channel_switching.DI
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class NetworkChangeReceiver : BroadcastReceiver() {
    private val rxNetwork = DI.rxNetwork

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) return
        rxNetwork.networkChanged(getNetworkType(context))
    }

    private fun getNetworkType(context: Context): RxNetwork.NetworkType {
        val cm =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT < 23) {
            val networkInfo = cm.activeNetworkInfo ?: return RxNetwork.NetworkType.Unknown
            when {
                networkInfo.isConnected && networkInfo.type == ConnectivityManager.TYPE_WIFI -> RxNetwork.NetworkType.Wifi
                networkInfo.isConnected && networkInfo.type == ConnectivityManager.TYPE_MOBILE -> RxNetwork.NetworkType.Cellular
                else -> RxNetwork.NetworkType.Unknown
            }
        } else {
            val network = cm.activeNetwork ?: return RxNetwork.NetworkType.Unknown
            val networkCapabilities =
                cm.getNetworkCapabilities(network) ?: return RxNetwork.NetworkType.Unknown
            when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> RxNetwork.NetworkType.Wifi
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> RxNetwork.NetworkType.Cellular
                else -> RxNetwork.NetworkType.Unknown
            }
        }
        return RxNetwork.NetworkType.Unknown
    }
}

class RxNetwork {

    private val networkTypeChangeSubject: BehaviorSubject<NetworkType> = BehaviorSubject.create()
    val networkTypeChangeObservable: Observable<NetworkType> =
        networkTypeChangeSubject.hide().distinct().subscribeOn(Schedulers.io())

    internal fun networkChanged(networkType: NetworkType) =
        networkTypeChangeSubject.onNext(networkType)

    enum class NetworkType {
        Cellular,
        Wifi,
        Unknown
    }
}