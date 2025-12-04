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
    // Página atual
    private var curPage = 0

    // lista de Pokémon
    var pokemonList = mutableStateOf<List<PokedexListEntry>>(listOf())

    // Estados para controle
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)

    // Lista de Pokémon para pesquisa
    private var cachedPokemonList = listOf<PokedexListEntry>()

    // Flag para verificar se a pesquisa começou
    private var isSearchStarting = true

    // Flag para verificar se a pesquisa está em andamento
    var isSearching = mutableStateOf(false)

    init {
        // Carrega a lista inicial de Pokémon
        loadPokemonPaginates()
    }

    // Função para pesquisar Pokémon
    fun searchPokemonList(query: String) {
        // Verifica se a pesquisa está vazia
        val listToSearch = if (isSearchStarting) {
            // Usa a lista completa se a pesquisa começou
            pokemonList.value
        } else {
            // Usa a lista cacheada se a pesquisa não estiver começando
            cachedPokemonList
        }

        // Verifica se a pesquisa está vazia e refaz a lista
        viewModelScope.launch(Dispatchers.Default) {
            if(query.isEmpty()) {
                pokemonList.value = cachedPokemonList
                isSearching.value = false
                isSearchStarting = true
                return@launch
            }

            // Filtra a lista com base na consulta
            val results = listToSearch.filter {
                it.pokemonName.contains(query.trim(), ignoreCase = true) ||
                        it.number.toString() == query.trim()
            }

            // Verifica se a pesquisa começou e atualiza a lista cacheada
            if(isSearchStarting) {
                cachedPokemonList = pokemonList.value
                isSearchStarting = false
            }
            pokemonList.value = results
            isSearching.value = true
        }
    }

    // Função para carregar a lista de Pokémon
    fun loadPokemonPaginates() {
        // Evita chamadas duplicadas ou além do fim
        if (isLoading.value || endReached.value) return

        // Inicia o carregamento
        viewModelScope.launch {
            isLoading.value = true
            try {
                // Obtém a lista de Pokémon da API
                val list = repository.getPokemonList(offset = curPage * PAGE_SIZE, limit = PAGE_SIZE)

                // Verifica fim da paginação
                endReached.value = list.size < PAGE_SIZE

                // Converte a lista de Pokémon para objetos PokedexListEntry
                val entries = list.map { pokemon ->
                    PokedexListEntry(
                        pokemonName = pokemon.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                        imageUrl = pokemon.imageUrl,
                        number = pokemon.id
                    )
                }

                // Atualiza a lista de Pokémon
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

    // Calcula a cor dominante
    fun calcDominantColor(drawable: Drawable, onFinish: (Color) -> Unit) {
        // Cria um Bitmap a partir do drawable
        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)

        // Cria um Palette a partir do Bitmap
        Palette.from(bmp).generate { palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }
    }
}