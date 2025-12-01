package com.example.pokedex.data.remote

import com.example.pokedex.data.remote.dto.EvolutionChainDto
import com.example.pokedex.data.remote.dto.PokemonDetailDto
import com.example.pokedex.data.remote.dto.PokemonListDto
import com.example.pokedex.di.NetworkModule

object ApiService {
    private val api = NetworkModule.pokemonApi

    suspend fun getPokemonList(offset: Int, limit: Int): PokemonListDto =
        api.getPokemonList(offset, limit)

    suspend fun getPokemon(idOrName: String): PokemonDetailDto =
        api.getPokemon(idOrName)

    suspend fun getEvolutionChain(id: Int): EvolutionChainDto =
        api.getEvolutionChain(id)
}