package com.example.grpc_channel_switching.data

import api.GreeterGrpc
import api.Helloworld
import io.grpc.ManagedChannel
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 * Возможно лучше переключать не каналы, а стабы сервисов в зависимости от сети
 */
class GreeterApi(private val multiChannelResolver: MultiChannelResolver) {

    private val stubs = mutableMapOf<ManagedChannel, GreeterGrpc.GreeterBlockingStub>()

    fun hello(): Single<Helloworld.HelloReply> = Single.fromCallable {
        getStub().sayHello(Helloworld.HelloRequest.newBuilder().setName("Somebody").build())
    }.subscribeOn(Schedulers.io())

    private fun getStub(): GreeterGrpc.GreeterBlockingStub {
        val channel = multiChannelResolver.resolve()
        if (!stubs.containsKey(channel)) {
            stubs[channel] = GreeterGrpc.newBlockingStub(channel)
        }
        return stubs[channel]!!
    }
}