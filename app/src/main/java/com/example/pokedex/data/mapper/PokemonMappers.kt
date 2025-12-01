package com.example.pokedex.data.mapper

import com.example.pokedex.data.remote.dto.PokemonDetailDto
import com.example.pokedex.data.remote.dto.PokemonListItemDto
import com.example.pokedex.domain.models.Pokemon

private fun extractId(url: String): Int =
    url.trimEnd('/').substringAfterLast('/').toIntOrNull() ?: -1

private fun officialArtworkUrl(id: Int): String =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"

fun PokemonListItemDto.toDomain(): Pokemon {
    val id = extractId(url)
    return Pokemon(
        id = id,
        name = name,
        imageUrl = officialArtworkUrl(id),
        description = ""
    )
}

fun PokemonDetailDto.toDomain(): Pokemon {
    val image = sprites.other?.official_artwork?.front_default ?: officialArtworkUrl(id)
    return Pokemon(
        id = id,
        name = name,
        imageUrl = image,
        description = "Pokemon $name #$id"
    )
}
