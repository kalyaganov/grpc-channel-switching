package com.example.grpc_channel_switching.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.grpc_channel_switching.DI
import com.example.grpc_channel_switching.R
import com.example.grpc_channel_switching.data.GreeterApi
import com.example.grpc_channel_switching.network.RxNetwork
import io.grpc.StatusRuntimeException
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val greeterApi: GreeterApi = DI.greeterApi
    private val rxNetwork: RxNetwork = DI.rxNetwork

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.main_fragment, container, false)

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<Button>(R.id.makeRequestButton).setOnClickListener { makeRequest() }

        rxNetwork.networkTypeChangeObservable.subscribe { Timber.d("Network changed $it") }
    }

    @SuppressLint("CheckResult")
    private fun makeRequest() {
        greeterApi.hello()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Timber.d("Hello response: $it")
                    view?.findViewById<TextView>(R.id.message)?.text = it.message
                },
                {
                    Timber.d("Hello error: $it")

                    when (it) {
                        is StatusRuntimeException -> view?.findViewById<TextView>(R.id.message)?.text =
                            "Error ${it.status}"
                        else -> view?.findViewById<TextView>(R.id.message)?.text =
                            "Error ${it.message}"
                    }
                }
            )
    }
}