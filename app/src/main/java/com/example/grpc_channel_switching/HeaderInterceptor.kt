package com.example.grpc_channel_switching

import io.grpc.*
import javax.inject.Inject

class HeaderInterceptor @Inject constructor(
    private val meta: String
) : ClientInterceptor {

    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        method: MethodDescriptor<ReqT, RespT>?,
        callOptions: CallOptions?,
        next: Channel
    ): ClientCall<ReqT, RespT> {

        val call = next.newCall(method, callOptions)

        return object : ClientInterceptors.CheckedForwardingClientCall<ReqT, RespT>(call) {

            override fun checkedStart(responseListener: Listener<RespT>?, headers: Metadata) {
                headers.put(Metadata.Key.of("META", Metadata.ASCII_STRING_MARSHALLER), meta)
                call.start(responseListener, headers)
            }
        }
    }
}