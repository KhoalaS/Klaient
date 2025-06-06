package com.khoalas.klaient

import android.app.Application
import com.khoalas.klaient.modules.appModule
import com.khoalas.klaient.modules.networkModule
import com.khoalas.klaient.modules.repoModule
import com.khoalas.klaient.modules.contentService
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@App)
            // Load modules
            modules(appModule, networkModule, contentService, repoModule)
        }
    }
}