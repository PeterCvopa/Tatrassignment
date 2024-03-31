package com.goodrequest.hiring.api

import com.goodrequest.hiring.model.PokemonDetail
import com.goodrequest.hiring.model.PokemonInfo
import com.goodrequest.hiring.model.PokemonWithDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton


interface PokemonManager {
    fun loadPokemonsWithDetail(page: Int): Flow<PokemonWithDetailResult>
}

@Singleton
class PokemonRepositoryImpl
@Inject constructor(
    private val pokemonService: PokemonService,
) : PokemonManager {

    private suspend fun loadPokemons(page: Int): PokemonsResult {
        return runCatching<PokemonsResult> {
            val pokemons = pokemonService.getPokemons(limit = 20, offset = page.pageToOffset())
            PokemonsResult.Data(pokemons.results.map { PokemonInfo(it.url, it.name) })
        }
            .onFailure {
                when (it) {
                    is HttpException, is UnknownHostException -> PokemonsResult.Error.NetworkError
                    else -> PokemonsResult.Error.UnknownError
                }
            }
            .getOrNull() ?: PokemonsResult.Error.UnknownError

    }

    private suspend fun loadPokemonDetail(name: String): PokemonDetailResponse? {
        return kotlin.runCatching {
            pokemonService.getPokemonDetail(name)
        }.getOrNull()
    }

    override fun loadPokemonsWithDetail(page: Int): Flow<PokemonWithDetailResult> {
        return flow {
            when (val pokemonsResult = loadPokemons(page)) {
                is PokemonsResult.Data -> {
                    val pokemonWithDetail = pokemonsResult
                        .pokemons
                        .map { pokemonData ->
                            val detail = loadPokemonDetail(pokemonData.name)
                            PokemonWithDetail(pokemonData.name, detail?.toDetail())
                        }
                    emit(PokemonWithDetailResult.Data(pokemonWithDetail))
                }

                PokemonsResult.Error.NetworkError -> emit(PokemonWithDetailResult.Error.NetworkError)
                PokemonsResult.Error.UnknownError -> emit(PokemonWithDetailResult.Error.UnknownError)
            }
        }
    }
}

sealed class PokemonWithDetailResult {
    data class Data(val pokemons: List<PokemonWithDetail>) : PokemonWithDetailResult()
    sealed class Error : PokemonWithDetailResult() {
        data object NetworkError : Error()
        data object UnknownError : Error()
    }
}

sealed class PokemonsResult {
    data class Data(val pokemons: List<PokemonInfo>) : PokemonsResult()
    sealed class Error : PokemonsResult() {
        data object NetworkError : Error()
        data object UnknownError : Error()
    }
}


private fun Int.pageToOffset(): Int {
    return (this) * 20
}

private fun PokemonDetailResponse.toDetail(): PokemonDetail {
    return PokemonDetail(
        move = moves.first().move.name,
        imageUrl = sprites.imageUrl,
        weight = weight
    )
}