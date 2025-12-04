package com.example.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokedex.data.repository.PokemonRepositoryImpl
import com.example.pokedex.ui.list.PokemonListViewModelFactory
import com.example.pokedex.ui.list.PokemonListViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pokedex.ui.list.PokemonListScreen
import com.example.pokedex.ui.theme.PokedexTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.pokedex.data.remote.PokemonApi
import com.example.pokedex.ui.detail.PokemonDetailScreen
import com.example.pokedex.ui.detail.PokemonDetailViewModel
import com.example.pokedex.ui.detail.PokemonDetailViewModelFactory
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Seta o tema da aplicação
            PokedexTheme {
                // Cria o controlador de navegação
                val navController = rememberNavController()

                // Cria uma instância do ViewModel usando o factory
                val retrofit = remember {
                    Retrofit.Builder()
                        .baseUrl("https://pokeapi.co/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                }
                // Cria uma instância da API
                val api = remember { retrofit.create(PokemonApi::class.java) }
                // Cria uma instância do repositório
                val repo = remember { PokemonRepositoryImpl(api) }
                // Cria uma instância do factory
                val factory = remember { PokemonListViewModelFactory(repo) }
                // Cria uma instância do ViewModel usando o factory
                val pokemonListViewModel: PokemonListViewModel = viewModel(factory = factory)

                // Configura a navegação
                NavHost(
                    navController = navController,
                    startDestination = "pokemon_list_screen"
                ) {
                    // Define as telas da navegação

                    // Tela de lista de Pokémon
                    composable("pokemon_list_screen") {
                        PokemonListScreen(navController = navController, viewModel = pokemonListViewModel)
                    }

                    // Tela de detalhes de Pokémon
                    composable(
                        "pokemon_detail_screen/{dominantColor}/{pokemonName}",
                        arguments = listOf(
                            navArgument("dominantColor") {
                                type = NavType.IntType
                            },
                            navArgument("pokemonName") {
                                type = NavType.StringType
                            }
                        )
                    ) { backStackEntry ->
                        // Passa a cor dominante e o nome do Pokémon para a tela de detalhes
                        val dominantColor = remember {
                            val color = backStackEntry.arguments?.getInt("dominantColor")
                            color?.let { Color(it) } ?: Color.White
                        }
                        val pokemonName = remember { backStackEntry.arguments?.getString("pokemonName") }

                        // Cria uma instância do factory
                        val pokemonDetailFactory = remember { PokemonDetailViewModelFactory(repo) }
                        // Cria uma instância do ViewModel usando o factory
                        val pokemonDetailViewModel: PokemonDetailViewModel = viewModel(factory = pokemonDetailFactory)

                        // Renderiza a tela de detalhes passando os parâmetros
                        PokemonDetailScreen(
                            dominantColor = dominantColor,
                            pokemonName = pokemonName?.lowercase(Locale.ROOT) ?: "",
                            navController = navController,
                            topPadding = 16.dp,
                            pokemonImageSize = 200.dp,
                            viewModel = pokemonDetailViewModel
                        )
                    }
                }
            }
        }
    }
}