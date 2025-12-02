package com.example.pokedex.ui.list

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.example.pokedex.data.repository.PokemonRepository
import com.example.pokedex.domain.models.PokedexListEntry
import com.example.pokedex.util.Constants.PAGE_SIZE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.internal.filterList
import java.util.Locale

class PokemonListViewModel(
    private val repository: PokemonRepository
) : ViewModel() {
    private var curPage = 0

    var pokemonList = mutableStateOf<List<PokedexListEntry>>(listOf())
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)

    private var cachedPokemonList = listOf<PokedexListEntry>()
    private var isSearchStarting = true
    var isSearching = mutableStateOf(false)

    init {
        loadPokemonPaginates()
    }

    fun searchPokemonList(query: String) {
        val listToSearch = if (isSearchStarting) {
            pokemonList.value
        } else {
            cachedPokemonList
        }
        viewModelScope.launch(Dispatchers.Default) {
            if(query.isEmpty()) {
                pokemonList.value = cachedPokemonList
                isSearching.value = false
                isSearchStarting = true
                return@launch
            }
            val results = listToSearch.filter {
                it.pokemonName.contains(query.trim(), ignoreCase = true) ||
                        it.number.toString() == query.trim()
            }
            if(isSearchStarting) {
                cachedPokemonList = pokemonList.value
                isSearchStarting = false
            }
            pokemonList.value = results
            isSearching.value = true
        }
    }


    fun loadPokemonPaginates() {
        // Evita chamadas duplicadas ou além do fim
        if (isLoading.value || endReached.value) return
        viewModelScope.launch {
            isLoading.value = true
            try {
                val list = repository.getPokemonList(offset = curPage * PAGE_SIZE, limit = PAGE_SIZE)

                // Verifica fim da paginação
                endReached.value = list.size < PAGE_SIZE

                val entries = list.map { pokemon ->
                    PokedexListEntry(
                        pokemonName = pokemon.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                        imageUrl = pokemon.imageUrl,
                        number = pokemon.id
                    )
                }

                pokemonList.value += entries
                curPage++
                loadError.value = ""
            } catch (e: Exception) {
                loadError.value = e.message ?: "Erro desconhecido"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun calcDominantColor(drawable: Drawable, onFinish: (Color) -> Unit) {
        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)

        Palette.from(bmp).generate { palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }
    }
}