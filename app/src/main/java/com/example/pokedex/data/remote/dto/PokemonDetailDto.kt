package com.example.pokedex.data.remote.dto

data class PokemonDetailDto(
    val id: Int,
    val name: String,
    val sprites: SpritesDto,
    val weight: Float,
    val height: Float,
    val stats: List<StatDto>,
    val types: List<TypeSlotDto>
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
    data class StatDto(
        val base_stat: Int,
        val stat: StatNameDto
    )
    data class StatNameDto(
        val name: String
    )
    data class TypeSlotDto(
        val slot: Int,
        val type: TypeRefDto
    )
    data class TypeRefDto(
        val name: String,
        val url: String
    )
}
