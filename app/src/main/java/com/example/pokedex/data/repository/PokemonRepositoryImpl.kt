package com.example.pokedex.data.repository

import com.example.pokedex.data.mapper.toDomain
import com.example.pokedex.data.remote.PokemonApi
import com.example.pokedex.domain.models.Pokemon
import com.example.pokedex.data.repository.PokemonRepository

class PokemonRepositoryImpl(
    private val api: PokemonApi
) : PokemonRepository {

    override suspend fun getPokemonList(offset: Int, limit: Int): List<Pokemon> {
        val dto = api.getPokemonList(offset = offset, limit = limit)
        return dto.results.map { it.toDomain() }
    }

    override suspend fun getPokemonByQuery(query: String): Pokemon? {
        return runCatching { api.getPokemon(query).toDomain() }.getOrNull()
    }
}