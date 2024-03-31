package com.goodrequest.hiring.api

import com.squareup.moshi.Json
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonService {

    @GET("pokemon")
    suspend fun getPokemons(@Query("limit") limit: Int = 20, @Query("offset") offset: Int = 0): PokemonListResponse

    @GET("pokemon/{id}")
    suspend fun getPokemonDetail(@Path("id") id: String): PokemonDetailResponse
}

data class PokemonListResponse(
    val results: List<PokemonData>
)

data class PokemonData(
    val url: String,
    val name: String,
)

data class Move(
    val move: MoveData
)

data class MoveData(
    val name: String
)

data class Sprites(
    @field:Json(name = "front_default")
    val imageUrl: String
)

data class PokemonDetailResponse(
    val moves: List<Move>,
    val sprites: Sprites,
    val weight: Int,
)
