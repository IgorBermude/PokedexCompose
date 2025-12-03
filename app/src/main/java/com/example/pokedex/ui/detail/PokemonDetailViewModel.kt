package com.example.pokedex.ui.detail

import com.example.pokedex.data.repository.PokemonRepository
import androidx.lifecycle.ViewModel
import com.example.pokedex.domain.models.Pokemon
import com.example.pokedex.util.UiState

class PokemonDetailViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    suspend fun getPokemonInfo(pokemonName: String): UiState<Pokemon>{
        return repository.getPokemonByQuery(pokemonName)
    }
}