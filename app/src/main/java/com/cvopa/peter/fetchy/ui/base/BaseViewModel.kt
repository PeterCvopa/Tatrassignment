package com.cvopa.peter.fetchy.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseViewModel<State : Any> : ViewModel() {

    protected abstract val initialState: State
    private val _state: MutableStateFlow<State> by lazy { MutableStateFlow(initialState) }

    val state: StateFlow<State>
        get() = _state

    protected fun emitState(newState: State) {
        Timber.tag(this::class.simpleName ?: "").d("Emit state: $newState")
        viewModelScope.launch {
            _state.update {
                newState
            }
        }
    }
}