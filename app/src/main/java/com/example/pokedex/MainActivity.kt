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
            PokedexTheme {
                val navController = rememberNavController()

                val retrofit = remember {
                    Retrofit.Builder()
                        .baseUrl("https://pokeapi.co/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                }
                val api = remember { retrofit.create(PokemonApi::class.java) }
                val repo = remember { PokemonRepositoryImpl(api) }
                val factory = remember { PokemonListViewModelFactory(repo) }
                val pokemonListViewModel: PokemonListViewModel = viewModel(factory = factory)

                NavHost(
                    navController = navController,
                    startDestination = "pokemon_list_screen"
                ) {
                    composable("pokemon_list_screen") {
                        PokemonListScreen(navController = navController, viewModel = pokemonListViewModel)
                    }

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
                        val dominantColor = remember {
                            val color = backStackEntry.arguments?.getInt("dominantColor")
                            color?.let { Color(it) } ?: Color.White
                        }
                        val pokemonName = remember { backStackEntry.arguments?.getString("pokemonName") }

                        val pokemonDetailFactory = remember { PokemonDetailViewModelFactory(repo) }
                        val pokemonDetailViewModel: PokemonDetailViewModel = viewModel(factory = pokemonDetailFactory)

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