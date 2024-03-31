package com.goodrequest.hiring.model

import com.goodrequest.hiring.api.PokemonWithDetail

data class PokemonListState(
    val pokemonList: List<PokemonWithDetail> = emptyList(),
    val isRefreshing: Boolean = false,
    val error: PokemonError? = null,
    val nextLoadPage : Int = 1,
)

sealed class PokemonError(open val messageRes: Int) {
    data class NetworkError(override val messageRes: Int) : PokemonError(messageRes)
    data class UnknownError(override val messageRes: Int) : PokemonError(messageRes)
}

data class PokemonInfo(
    val id: String,
    val name: String,
)
