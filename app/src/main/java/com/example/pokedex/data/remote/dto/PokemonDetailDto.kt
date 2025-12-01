package com.example.pokedex.data.remote.dto

data class PokemonDetailDto(
    val id: Int,
    val name: String,
    val sprites: SpritesDto
) {
    data class SpritesDto(
        val other: OtherDto?
    )
    data class OtherDto(
        val official_artwork: OfficialArtworkDto?
    )
    data class OfficialArtworkDto(
        val front_default: String?
    )
}
