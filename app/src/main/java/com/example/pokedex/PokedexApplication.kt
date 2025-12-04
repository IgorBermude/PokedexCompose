package com.example.pokedex

import android.app.Application
import timber.log.Timber

class PokedexApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Inicializa recursos e estados globais do app.
        Timber.plant(Timber.DebugTree())
    }
}