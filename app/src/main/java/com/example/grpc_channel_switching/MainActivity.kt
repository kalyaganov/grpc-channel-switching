package com.example.grpc_channel_switching

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.grpc_channel_switching.ui.main.MainFragment
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }

    companion object {
        init {
            Timber.plant(Timber.DebugTree() )
        }
    }
}