package com.example.pokedex.data.repository

import com.example.pokedex.data.mapper.toDomain
import com.example.pokedex.data.remote.PokemonApi
import com.example.pokedex.domain.models.Pokemon
import com.example.pokedex.data.repository.PokemonRepository
import com.example.pokedex.util.UiState
import retrofit2.HttpException
import java.util.Locale

class PokemonRepositoryImpl(
    private val api: PokemonApi
) : PokemonRepository {

    // Implementação do repositório para obter a lista de Pokémon
    override suspend fun getPokemonList(offset: Int, limit: Int): List<Pokemon> {
        val dto = api.getPokemonList(offset = offset, limit = limit)
        return dto.results.map { it.toDomain() }
    }

    // Implementação do repositório para obter detalhes de um Pokémon
    override suspend fun getPokemonByQuery(query: String): UiState<Pokemon> {
        val normalized = query.trim().lowercase(Locale.ROOT)
        return try {
            val dto = api.getPokemon(normalized)
            UiState.Success(dto.toDomain())
        } catch (e: HttpException) {
            if (e.code() == 404) {
                UiState.Error("Pokémon não encontrado")
            } else {
                UiState.Error(e.message() ?: "Erro desconhecido")
            }
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Erro desconhecido")
        }
    }
}