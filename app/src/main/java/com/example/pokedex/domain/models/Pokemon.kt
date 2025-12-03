package com.example.pokedex.domain.models

data class Pokemon (
    val id: Int,
    val name: String,
    val imageUrl: String,
    val description: String,
    val weight: Int,
    val height: Int,
    val stats: List<PokemonStat> = emptyList(),
    val types: List<String> = emptyList()
)

data class PokemonStat(
    val name: String,
    val baseStat: Int
)