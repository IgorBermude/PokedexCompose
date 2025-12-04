package com.example.pokedex.domain.models

// Classe que representa um Pokémon na lista de Pokémons
data class PokedexListEntry (
    val pokemonName: String,
    val imageUrl: String,
    val number: Int
)