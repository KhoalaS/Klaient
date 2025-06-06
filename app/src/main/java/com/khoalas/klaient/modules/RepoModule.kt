package com.khoalas.klaient.modules

import com.khoalas.klaient.repo.ContentRepository
import org.koin.dsl.module

val repoModule = module {
    single { ContentRepository(get()) }
}