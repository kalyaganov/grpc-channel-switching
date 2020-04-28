package com.example.grpc_channel_switching.data

import com.example.grpc_channel_switching.network.RxNetwork
import io.grpc.ManagedChannel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class MultiChannelResolver(
    private val internalChannel: ManagedChannel,
    private val externalChannel: ManagedChannel,
    rxNetwork: RxNetwork
) {

    private var currentChannel = internalChannel
    private val compositeDisposable = CompositeDisposable()

    init {
        rxNetwork.networkTypeChangeObservable.subscribe { type ->
            currentChannel = when(type){
                RxNetwork.NetworkType.Cellular -> externalChannel
                RxNetwork.NetworkType.Wifi -> internalChannel
                RxNetwork.NetworkType.Unknown -> externalChannel
                else -> externalChannel
            }
        }.addTo(compositeDisposable)
    }

    fun resolve(): ManagedChannel = currentChannel

    fun dispose() = compositeDisposable.dispose()
}