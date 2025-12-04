package com.example.pokedex.data.repository

import com.example.pokedex.domain.models.Pokemon
import com.example.pokedex.util.UiState

// Interface para a implementação do repositório
interface PokemonRepository {
    suspend fun getPokemonList(offset: Int, limit: Int): List<Pokemon>
    suspend fun getPokemonByQuery(query: String): UiState<Pokemon>
}