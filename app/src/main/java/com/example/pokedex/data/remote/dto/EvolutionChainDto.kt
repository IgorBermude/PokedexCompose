package com.example.pokedex.data.remote.dto

data class EvolutionChainDto(
    val id: Int,
    val chain: ChainLinkDto
) {
    data class ChainLinkDto(
        val species: SpeciesDto,
        val evolves_to: List<ChainLinkDto>
    )
    data class SpeciesDto(
        val name: String,
        val url: String
    )
}
