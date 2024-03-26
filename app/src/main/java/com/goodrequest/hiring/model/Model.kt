package com.goodrequest.hiring.model

data class PokemonListState(
    val pokemonList: List<PokemonData> = emptyList(),
    val isRefreshing: Boolean = false,
    val error: PokemonError? = null,
)

sealed class PokemonError(open val messageRes: Int) {
    data class NetworkError(override val messageRes: Int) : PokemonError(messageRes)
    data class UnknownError(override val messageRes: Int) : PokemonError(messageRes)
}

data class PokemonData(
    val id: String,
    val name: String,
)
