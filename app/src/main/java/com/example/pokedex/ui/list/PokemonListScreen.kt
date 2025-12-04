package com.example.pokedex.ui.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pokedex.R
import com.example.pokedex.domain.models.PokedexListEntry
import java.util.Map.entry

// TELA COM A LISTA DE POKEMONS E O TIME
@Composable
fun PokemonListScreen(
    navController: NavController,
    viewModel: PokemonListViewModel
) {
    // atributo que mantém a lista de Pokémon do time
    val team = remember { mutableStateListOf<PokedexListEntry>() }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        // Coluna principal
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Cabeçalho com logo e título (visual atualizado)
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.ic_international_pok_mon_logo),
                    contentDescription = "Pokemon",
                    modifier = Modifier
                        .size(72.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))

                // Coluna com texto
                Column {
                    Text(
                        text = "Pokedex",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Explore seu mundo Pokémon",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Barra de pesquisa (comportamento de hint corrigido)
            SearchBar(
                hint = "Buscar Pokémon...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, vertical = 7.dp)
            ) {
                viewModel.searchPokemonList(it)
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Lista de Pokémon do time (visualmente menor, mesma proporção)
            if (team.isNotEmpty()) {
                Text(
                    text = "Seu time",
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .align(Alignment.CenterHorizontally)
                )

                // LazyRow para exibir os Pokémon do time horizontalmente
                LazyRow(
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Itera sobre os Pokémon do time e cria um PokedexEntry para cada um
                    items(
                        items = team,
                        key = { entry -> entry.pokemonName }
                    ) { entry ->
                        PokedexEntry(
                            entry = entry,
                            navController = navController,
                            modifier = Modifier.size(110.dp), // menor que antes
                            imageSize = 64.dp, // imagem interna menor para o time
                            viewModel = viewModel,
                            team = team
                        )
                    }
                }

                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Título da seção principal antes da lista
            Text(
                text = "Explorar",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 20.dp, bottom = 8.dp)
            )

            // Lista principal de Pokémon
            PokedexList(
                navController = navController,
                viewModel = viewModel,
                team = team
            )
        }
    }
}

// Barra de pesquisa
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String = "",
    onSearch: (String) -> Unit = {}
) {
    // Estado para armazenar o texto na barra de pesquisa
    var text by remember {
        mutableStateOf("")
    }

    Box(modifier = modifier){
        // TextField personalizado para a barra de pesquisa
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                onSearch(it)
            },
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        )
        if(text.isEmpty()){
            // Texto de dica na barra de pesquisa se estiver vazia
            Text(
                text = hint,
                color = Color.LightGray,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }
}

// Componente de entrada de lista de Pokémon
@Composable
fun PokedexEntry(
    entry: PokedexListEntry,
    navController: NavController,
    modifier: Modifier = Modifier,
    imageSize: Dp = 120.dp,
    viewModel: PokemonListViewModel,
    team: MutableList<PokedexListEntry>
) {
    // Estados para armazenar a cor dominante do Pokémon
    val defaultDominantColor = MaterialTheme.colorScheme.surface
    val dominantColor = remember { mutableStateOf(defaultDominantColor) }

    // Box para a entrada do Pokémon
    Box(
        // Alinhamento central
        contentAlignment = Alignment.Center,
        modifier = modifier
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .aspectRatio(1f)
            .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.06f), RoundedCornerShape(10.dp))
            // Cor de fundo do Pokémon com base na cor dominante
            .background(
                Brush.verticalGradient(
                    listOf(
                        dominantColor.value,
                        defaultDominantColor
                    )
                )
            )
            .combinedClickable(
                // Click para navegação para detalhes do Pokémon
                onClick = {
                    navController.navigate(
                        "pokemon_detail_screen/${dominantColor.value.toArgb()}/${entry.pokemonName}"
                    )
                },
                // Long click para adicionar Pokémon ao time
                onLongClick = {
                    if (team.size < 6 && !team.contains(entry)) {
                        team.add(entry)
                    }
                }
            )
            .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            // Imagem do Pokémon com carregamento e tratamento de erro
            AsyncImage(
                // Carregamento da imagem do Pokémon usando Coil
                model = ImageRequest.Builder(LocalContext.current)
                    .data(entry.imageUrl)
                    .crossfade(true)
                    .build(),
                // Descrição do Pokémon para acessibilidade
                contentDescription = entry.pokemonName,
                // Modo de ajuste da imagem
                contentScale = ContentScale.Crop,
                // Modificações da imagem
                modifier = Modifier
                    .size(imageSize)
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(8.dp)),
                onSuccess = { state ->
                    // Calcula a cor dominante do Pokémon após o carregamento da imagem
                    state.result.drawable.let { drawable ->
                        viewModel.calcDominantColor(drawable) { color ->
                            dominantColor.value = color
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Nome
            Text(
                text = entry.pokemonName,
                fontSize = if (imageSize < 90.dp) 12.sp else 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            // número do Pokémon
            Text(
                text = entry.number.toString(),
                fontSize = if (imageSize < 90.dp) 10.sp else 18.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// Componente de lista de Pokémon
@Composable
fun PokedexList(
    navController: NavController,
    viewModel: PokemonListViewModel,
    team: MutableList<PokedexListEntry>
) {
    // Estados para armazenar a lista de Pokémon e outras informações
    val pokemonList by remember { viewModel.pokemonList }
    val endReached by remember { viewModel.endReached }
    val loadError by remember { viewModel.loadError }
    val isLoading by remember { viewModel.isLoading }
    val isSearching by remember { viewModel.isSearching }

    LazyColumn(
        contentPadding = PaddingValues(16.dp)
    ) {
        // Cria itens para cada Pokémon na lista
        val itemCount = if (pokemonList.size % 2 == 0) {
            pokemonList.size / 2
        } else {
            pokemonList.size / 2 + 1
        }
        items(itemCount) { index ->
            // Verifica se é a última página e carrega mais Pokémon se necessário
            if (index >= itemCount - 1 && !endReached && !isLoading && !isSearching) {
                viewModel.loadPokemonPaginates()
            }
            // Renderiza uma linha de Pokémon
            PokedexRow(
                rowIndex = index,
                entries = pokemonList,
                navController = navController,
                viewModel = viewModel,
                team = team
            )
        }
    }

    // Componente de carregamento ou erro
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if(isLoading){
            // Carregamento
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        if(loadError.isNotEmpty()){
            // Recarregar
            RetrySection(error = loadError) {
                viewModel.loadPokemonPaginates()
            }
        }
    }
}

// Componente de linha de Pokémon
@Composable
fun PokedexRow(
    rowIndex: Int,
    entries: List<PokedexListEntry>,
    navController: NavController,
    viewModel: PokemonListViewModel,
    team: MutableList<PokedexListEntry>,
){
    Column() {
        // Cria duas entradas de Pokémon na linha
        Row{
            // Renderiza uma entrada de Pokémon
            PokedexEntry(
                entry = entries[rowIndex * 2],
                navController = navController,
                modifier = Modifier.weight(1f),
                viewModel = viewModel,
                team = team
            )
            Spacer(modifier = Modifier.width(16.dp))
            if(entries.size >= rowIndex * 2 + 2){
                // Renderiza a segunda entrada de Pokémon
                PokedexEntry(
                    entry = entries[rowIndex * 2 + 1],
                    navController = navController,
                    modifier = Modifier.weight(1f),
                    viewModel = viewModel,
                    team = team
                )
            } else{
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// Componente de recarregamento
@Composable
fun RetrySection(
    error: String,
    onRetry: () -> Unit
) {
    Column {
        // Texto de erro
        Text(error, color = Color.Red, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        // Botão de recarregar
        Button(onClick = { onRetry() }) {
            Text(text = "Recarregar")
        }
    }
}
