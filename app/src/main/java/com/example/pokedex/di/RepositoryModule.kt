package com.example.pokedex.di

import com.example.pokedex.data.remote.PokemonApi
import com.example.pokedex.data.repository.PokemonRepository
import com.example.pokedex.data.repository.PokemonRepositoryImpl

object RepositoryModule {

    val pokemonRepository: PokemonRepository by lazy {
        PokemonRepositoryImpl(NetworkModule.pokemonApi)
    }
}