package com.khoalas.klaient.modules

import android.content.Context
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.exoplayer.ExoPlayer
import com.khoalas.klaient.services.Route
import com.khoalas.klaient.viewmodel.FeedViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun getPlayer(context: Context) : ExoPlayer {
    val player = ExoPlayer.Builder(context).build()
    player.repeatMode = REPEAT_MODE_ALL
    return player
}

val appModule = module {
    viewModel { (route: Route) ->
        FeedViewModel(get(), get(), route)
    }
    single {
        getPlayer(androidContext())
    }
}