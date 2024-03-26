package com.goodrequest.hiring.api

import com.goodrequest.hiring.model.PokemonData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


interface PokemonRepository {
    fun loadPokemons(): Flow<PokemonResult>
}

@Singleton
class PokemonRepositoryImpl
@Inject constructor(
    private val pokemonService: PokemonService,
) : PokemonRepository {

    override fun loadPokemons(): Flow<PokemonResult> {
        return flow {
            try {
                val pokemons = pokemonService.getPokemons()
                emit(PokemonResult.Data(pokemons.results))
            } catch (e: Exception) {
                Timber.e(e)
                when (e) {
                    is HttpException -> emit(PokemonResult.Error.NetworkError)
                    else -> emit(PokemonResult.Error.UnknownError)
                }
            }
        }
    }
}


sealed class PokemonResult {
    data class Data(val pokemons: List<PokemonData>) : PokemonResult()
    sealed class Error() : PokemonResult() {
        data object NetworkError : Error()
        data object UnknownError : Error()
    }
}

