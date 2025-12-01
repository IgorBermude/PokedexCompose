package com.example.pokedex.di

import com.example.pokedex.data.remote.PokemonApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Simple manual singletons to replace Hilt modules
object NetworkModule {

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder().build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://pokeapi.co/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val pokemonApi: PokemonApi by lazy {
        retrofit.create(PokemonApi::class.java)
    }
}