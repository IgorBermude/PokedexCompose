package com.example.pokedex.data.repository

import com.example.pokedex.domain.models.Pokemon

interface PokemonRepository {
    suspend fun getPokemonList(offset: Int, limit: Int): List<Pokemon>
    suspend fun getPokemonByQuery(query: String): Pokemon?
}