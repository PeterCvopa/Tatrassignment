package com.goodrequest.hiring.model


data class PokemonListState(
    val pokemonList: List<PokemonWithDetail> = emptyList(),
    val isPullRefreshing: Boolean = false,
    val pullDownError: PullDownErrorState? = null,
    val pagingState: PagingState? = null,
    val nextPage: Int = 1,
)

sealed class PullDownErrorState(open val messageRes: Int) {
    data class NetworkError(override val messageRes: Int) : PullDownErrorState(messageRes)
    data class UnknownError(override val messageRes: Int) : PullDownErrorState(messageRes)
}

sealed class PagingState {
    data object Refreshing : PagingState()
    data object Error : PagingState()
}

data class PokemonInfo(
    val id: String,
    val name: String,
)

data class PokemonWithDetail(
    val name: String,
    val detail: PokemonDetail?,
)

data class PokemonDetail(
    val move: String,
    val imageUrl: String,
    val weight: Int,
)
