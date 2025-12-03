package com.example.pokedex.data.mapper

import com.example.pokedex.data.remote.dto.PokemonDetailDto
import com.example.pokedex.data.remote.dto.PokemonListItemDto
import com.example.pokedex.domain.models.Pokemon
import com.example.pokedex.domain.models.PokemonStat

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
        description = "",
        weight = 0,
        height = 0,
        stats = emptyList()
    )
}

fun PokemonDetailDto.toDomain(): Pokemon {
    val image = sprites.other?.official_artwork?.front_default ?: officialArtworkUrl(id)
    val mappedStats = stats.map { PokemonStat(name = it.stat.name, baseStat = it.base_stat) }
    val mappedTypes = types.map { it.type.name }
    return Pokemon(
        id = id,
        name = name,
        imageUrl = image,
        description = "Pokemon $name #$id",
        weight = weight,
        height = height,
        stats = mappedStats,
        types = mappedTypes
    )
}
