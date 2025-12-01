package com.example.pokedex.data.remote

import com.example.pokedex.data.remote.dto.EvolutionChainDto
import com.example.pokedex.data.remote.dto.PokemonDetailDto
import com.example.pokedex.data.remote.dto.PokemonListDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonApi {

    @GET("api/v2/pokemon")
    suspend fun getPokemonList(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): PokemonListDto

    @GET("api/v2/pokemon/{idOrName}")
    suspend fun getPokemon(
        @Path("idOrName") idOrName: String
    ): PokemonDetailDto

    @GET("api/v2/evolution-chain/{id}")
    suspend fun getEvolutionChain(
        @Path("id") id: Int
    ): EvolutionChainDto
}