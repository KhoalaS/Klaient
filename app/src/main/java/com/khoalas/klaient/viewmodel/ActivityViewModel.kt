package com.khoalas.klaient.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class ActivityViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    companion object {
        private const val STATUS_BAR_HIDDEN_KEY = "status_bar_hidden"
    }

    val isStatusBarHidden = savedStateHandle.getStateFlow(STATUS_BAR_HIDDEN_KEY, false)

    fun setStatusBarHidden(hidden: Boolean) {
        savedStateHandle[STATUS_BAR_HIDDEN_KEY] = hidden
    }
}