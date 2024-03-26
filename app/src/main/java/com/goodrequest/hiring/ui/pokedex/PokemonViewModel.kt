package com.goodrequest.hiring.ui.pokedex

import androidx.lifecycle.viewModelScope
import com.cvopa.peter.fetchy.ui.base.BaseViewModel
import com.goodrequest.hiring.R
import com.goodrequest.hiring.api.PokemonRepository
import com.goodrequest.hiring.api.PokemonResult
import com.goodrequest.hiring.model.PokemonData
import com.goodrequest.hiring.model.PokemonError
import com.goodrequest.hiring.model.PokemonListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val repository: PokemonRepository,
) : BaseViewModel<PokemonListState>() {

    init {
        onEvent(Event.OnLoad)
    }

    private val sideEffectChannel = Channel<Actions>(capacity = Channel.BUFFERED)
    val sideEffectFlow: Flow<Actions>
        get() = sideEffectChannel.receiveAsFlow()

    override val initialState: PokemonListState
        get() = PokemonListState(isRefreshing = true)

    private fun load() {
        Timber.d("peter Loading pokemons")
        emitState(state.value.copy(isRefreshing = true))
        viewModelScope.launch {
            repository.loadPokemons()
                .collect { pokemonResult ->
                    when (pokemonResult) {
                        is PokemonResult.Error -> {
                            val errorState = pokemonResult.toPokemonError()
                            sideEffectChannel.send(Actions.ShowSnackbar(errorState.messageRes))
                            emitState(state.value.copy(error = errorState, isRefreshing = false))
                        }

                        is PokemonResult.Data -> {
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
            is Event.OnLoad -> load()
            is Event.OnPokemonClicked -> {
                Timber.d("Pokemon ${it.pokemon} loves you!")
            }
        }
    }
}

sealed class Event {
    data object OnLoad : Event()
    data class OnPokemonClicked(val pokemon: PokemonData) : Event()
}

sealed class Actions {
    data class ShowSnackbar(val messageRes: Int) : Actions()
}

fun PokemonResult.Error.toPokemonError(): PokemonError {
    return when (this) {
        is PokemonResult.Error.NetworkError -> PokemonError.NetworkError(R.string.error_network)
        is PokemonResult.Error.UnknownError -> PokemonError.UnknownError(R.string.error_unknown)
    }
}