package com.example.androidsync.ui.screen

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {

    private val _greeting = MutableStateFlow("Hello, AndroidSync!")
    val greeting: StateFlow<String> = _greeting.asStateFlow()
}
