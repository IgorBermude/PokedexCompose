package com.example.pokedex.ui.detail

import com.example.pokedex.data.repository.PokemonRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.domain.models.Pokemon
import com.example.pokedex.util.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.util.Locale

class PokemonDetailViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _evolutionsState = MutableStateFlow<UiState<List<String>>>(UiState.Success(emptyList()))
    // Estado privado para evoluções (apenas ViewModel pode alterar este fluxo)
    val evolutionsState: StateFlow<UiState<List<String>>> = _evolutionsState
    // Estado público para evoluções (UI observa este fluxo)

    // Função para carregar a cadeia de evolução — executa rede e parsing no ViewModel
    fun loadEvolutions(pokemonId: Int, pokemonName: String) {
        // Inicia o fluxo de carregamento
        viewModelScope.launch {
            // Seta o estado para carregamento
            _evolutionsState.value = UiState.Loading()
            try {
                // Faz a requisição à API para obter a cadeia de evolução
                val jsonText = withContext(Dispatchers.IO) {
                    URL("https://pokeapi.co/api/v2/evolution-chain/$pokemonId/").readText()
                }
                // Converte a resposta JSON em um objeto JSONObject
                val root = JSONObject(jsonText)
                // Extrai a cadeia de evolução
                val chain = root.getJSONObject("chain")
                // Cria uma lista de nomes de Pokémon a partir da cadeia de evolução
                val names = mutableListOf<String>()
                // Chama a função recursiva para parsear a cadeia de evolução
                parseNamesRecursively(chain, names)

                // cria uma lista de nomes distintos e filtra o nome do Pokémon atual
                val filtered = names.distinct()
                    .filter { it != pokemonName.lowercase(Locale.ROOT) }

                // seta o estado para sucesso com a lista de nomes de Pokémon
                _evolutionsState.value = UiState.Success(filtered)
            } catch (e: Exception) {
                _evolutionsState.value = UiState.Error(e.message ?: "Erro ao carregar evoluções")
            }
        }
    }

    // Função auxiliar de parsing (recursiva)
    private fun parseNamesRecursively(chainObj: JSONObject, list: MutableList<String>) {
        // Extrai o nome do Pokémon da entrada atual
        val speciesName = chainObj.getJSONObject("species").getString("name")
        // Adiciona o nome à lista
        list.add(speciesName)
        // Verifica se há Pokémon evoluídos
        val evolvesTo = chainObj.getJSONArray("evolves_to")
        for (i in 0 until evolvesTo.length()) {
            // Chama a função recursivamente para cada Pokémon evoluído
            parseNamesRecursively(evolvesTo.getJSONObject(i), list)
        }
    }

    // Obtem detalhes do Pokémon a partir do nome
    suspend fun getPokemonInfo(pokemonName: String): UiState<Pokemon>{
        return repository.getPokemonByQuery(pokemonName)
    }
}