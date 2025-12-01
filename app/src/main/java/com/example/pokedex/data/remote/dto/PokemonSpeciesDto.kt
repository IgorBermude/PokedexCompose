package com.example.pokedex.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PokemonSpeciesDto (
    val id: Int,
    val name: String,
    @SerializedName("flavor_text_entries")
    val flavorTextEntries: List<FlavorTextEntryDto>,
    @SerializedName("evolution_chain")
    val evolutionChain: EvolutionChainLinkDto?
) {
    data class FlavorTextEntryDto(
        @SerializedName("flavor_text")
        val flavorText: String,
        val language: LanguageDto
    )
    data class LanguageDto(
        val name: String
    )
    data class EvolutionChainLinkDto(
        val url: String
    )
}