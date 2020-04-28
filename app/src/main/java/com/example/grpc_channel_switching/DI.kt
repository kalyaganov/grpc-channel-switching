package com.example.grpc_channel_switching

import android.content.Context
import com.example.grpc_channel_switching.data.GreeterApi
import com.example.grpc_channel_switching.data.MultiChannelResolver
import com.example.grpc_channel_switching.network.RxNetwork
import io.grpc.ManagedChannel
import io.grpc.android.AndroidChannelBuilder
import io.grpc.okhttp.OkHttpChannelBuilder

object DI {
    val context: Context by lazy { App.context }

    val rxNetwork: RxNetwork by lazy { RxNetwork() }

    val internalChannel: ManagedChannel by lazy {
        OkHttpChannelBuilder
            .forAddress("192.168.1.64", 9090)
            .usePlaintext()
            .intercept(HeaderInterceptor("internal call"))
            .keepAliveWithoutCalls(true)
            .let {
                AndroidChannelBuilder
                    .usingBuilder(it)
                    .context(context)
                    .build()
            }
    }

    val externalChannel: ManagedChannel by lazy {
        OkHttpChannelBuilder
            .forAddress("f505dfa9.ngrok.io", 9090)
            .usePlaintext()
            .intercept(HeaderInterceptor("external call"))
            .keepAliveWithoutCalls(true)
            .let {
                AndroidChannelBuilder
                    .usingBuilder(it)
                    .context(context)
                    .build()
            }
    }

    val multiChannelResolver: MultiChannelResolver by lazy {
        MultiChannelResolver(
            internalChannel,
            externalChannel,
            rxNetwork
        )
    }

    val greeterApi: GreeterApi by lazy { GreeterApi(multiChannelResolver) }
}