package com.khoalas.klaient.modules

import androidx.media3.exoplayer.ExoPlayer
import com.khoalas.klaient.services.Route
import com.khoalas.klaient.viewmodel.FeedViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { (route: Route) ->
        FeedViewModel(get(), get(), route)
    }
    single {
        ExoPlayer.Builder(androidContext()).build()
    }
}