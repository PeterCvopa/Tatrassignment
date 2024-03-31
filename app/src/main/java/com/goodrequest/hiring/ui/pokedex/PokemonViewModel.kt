package com.goodrequest.hiring.ui.pokedex

import androidx.lifecycle.viewModelScope
import com.goodrequest.hiring.R
import com.goodrequest.hiring.api.PokemonManager
import com.goodrequest.hiring.api.PokemonWithDetailResult
import com.goodrequest.hiring.model.PokemonError
import com.goodrequest.hiring.model.PokemonInfo
import com.goodrequest.hiring.model.PokemonListState
import com.goodrequest.hiring.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val repository: PokemonManager,
) : BaseViewModel<PokemonListState>() {

    init {
        onEvent(Event.OnRefresh)
    }

    private val sideEffectChannel = Channel<Actions>(capacity = Channel.BUFFERED)

    val sideEffectFlow: Flow<Actions>
        get() = sideEffectChannel.receiveAsFlow()

    override val initialState: PokemonListState
        get() = PokemonListState(isRefreshing = true)

    private fun load(page: Int = INIT_PAGE_INDEX) {
        setStateRefreshing()
        viewModelScope.launch {
            repository.loadPokemonsWithDetail(page)
                .collect { pokemonResult ->
                    when (pokemonResult) {
                        is PokemonWithDetailResult.Error -> {
                            val errorState = pokemonResult.toPokemonError()
                            sideEffectChannel.send(Actions.ShowSnackbar(errorState.messageRes))
                            emitState(state.value.copy(error = errorState, isRefreshing = false))
                        }

                        is PokemonWithDetailResult.Data -> {
                            emitState(
                                PokemonListState(
                                    pokemonList = pokemonResult.pokemons,
                                    error = null,
                                    isRefreshing = false
                                )
                            )
                        }
                    }
                }
        }
    }

    fun onEvent(it: Event) {
        when (it) {
            is Event.OnRefresh -> load()
            is Event.OnPokemonClicked -> {
                Timber.d("Pokemon ${it.pokemon} loves you!")
            }
            is Event.OnLoadMore -> {
                load(it.page)
            }
        }
    }

    private fun setStateRefreshing() {
        emitState(state.value.copy(isRefreshing = true))
    }
}

sealed class Event {
    data object OnRefresh : Event()
    data class OnLoadMore(val page: Int) : Event()
    data class OnPokemonClicked(val pokemon: PokemonInfo) : Event()
}

sealed class Actions {
    data class ShowSnackbar(val messageRes: Int) : Actions()
}

fun PokemonWithDetailResult.Error.toPokemonError(): PokemonError {
    return when (this) {
        is PokemonWithDetailResult.Error.NetworkError -> PokemonError.NetworkError(R.string.error_network)
        is PokemonWithDetailResult.Error.UnknownError -> PokemonError.UnknownError(R.string.error_unknown)
    }
}

const val INIT_PAGE_INDEX = 1