package com.example.pokedex.domain.models

// Classe que representa um Pokémon
data class Pokemon (
    val id: Int,
    val name: String,
    val imageUrl: String,
    val description: String,
    val weight: Float,
    val height: Float,
    val stats: List<PokemonStat> = emptyList(),
    val types: List<String> = emptyList()
)