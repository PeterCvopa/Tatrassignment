package com.goodrequest.hiring.ui.pokedex

import androidx.lifecycle.viewModelScope
import com.goodrequest.hiring.R
import com.goodrequest.hiring.api.PokemonManager
import com.goodrequest.hiring.api.PokemonWithDetailResult
import com.goodrequest.hiring.model.PagingState
import com.goodrequest.hiring.model.PokemonInfo
import com.goodrequest.hiring.model.PokemonListState
import com.goodrequest.hiring.model.PokemonWithDetail
import com.goodrequest.hiring.model.PullDownErrorState
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
        get() = PokemonListState(isPullRefreshing = true)

    private fun fullLoad() {
        emitState(state.value.copy(isPullRefreshing = true))
        viewModelScope.launch {
            load(onSuccess = { pokemons ->
                emitState(
                    PokemonListState(
                        pokemonList = pokemons,
                        pullDownError = null,
                        isPullRefreshing = false
                    )
                )
            }, onError = { errorState ->
                sideEffectChannel.send(Actions.ShowSnackbar(errorState.messageRes))
                emitState(state.value.copy(pullDownError = errorState, isPullRefreshing = false))
            })
        }
    }

    private fun pagingLoad() {
        emitState(state.value.copy(pagingState = PagingState.Refreshing))
        val nextPage = state.value.nextPage
        viewModelScope.launch {
            load(nextPage,
                onSuccess = { pokemons ->
                    emitState(
                        PokemonListState(
                            pokemonList = state.value.pokemonList + pokemons,
                            pullDownError = null,
                            isPullRefreshing = false,
                            pagingState = null,
                            nextPage = nextPage + 1
                        )
                    )
                },
                onError = { _ ->
                    emitState(
                        state.value.copy(
                            pagingState = PagingState.Error,
                            isPullRefreshing = false
                        )
                    )
                })
        }
    }

    private fun load(page: Int = INIT_PAGE_INDEX, onSuccess: (List<PokemonWithDetail>) -> Unit, onError: suspend (PullDownErrorState) -> Unit) {
        viewModelScope.launch {
            repository
                .loadPokemonsWithDetail(page)
                .collect { pokemonResult ->
                    when (pokemonResult) {
                        is PokemonWithDetailResult.Data -> {
                            onSuccess(pokemonResult.pokemons)
                        }

                        is PokemonWithDetailResult.Error -> {
                            onError(pokemonResult.toPokemonError())
                        }
                    }
                }
        }
    }

    fun onEvent(it: Event) {
        when (it) {
            is Event.OnRefresh -> fullLoad()
            is Event.OnPokemonClicked -> {
                Timber.d("Pokemon ${it.pokemon.name} loves you!")
            }
            is Event.OnLoadMore -> {
                pagingLoad()
            }
        }
    }
}

sealed class Event {
    data object OnRefresh : Event()
    data object OnLoadMore : Event()
    data class OnPokemonClicked(val pokemon: PokemonInfo) : Event()
}

sealed class Actions {
    data class ShowSnackbar(val messageRes: Int) : Actions()
}

fun PokemonWithDetailResult.Error.toPokemonError(): PullDownErrorState {
    return when (this) {
        is PokemonWithDetailResult.Error.NetworkError -> PullDownErrorState.NetworkError(R.string.error_network)
        is PokemonWithDetailResult.Error.UnknownError -> PullDownErrorState.UnknownError(R.string.error_unknown)
    }
}

const val INIT_PAGE_INDEX = 0