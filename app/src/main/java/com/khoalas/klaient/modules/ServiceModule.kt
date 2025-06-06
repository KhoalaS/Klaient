package com.khoalas.klaient.modules

import com.khoalas.klaient.services.ContentService
import org.koin.dsl.module

val contentService = module {
    single { ContentService(get()) }
}