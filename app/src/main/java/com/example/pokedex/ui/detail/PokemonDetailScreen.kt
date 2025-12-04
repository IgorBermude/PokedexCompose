package com.example.pokedex.ui.detail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import com.example.pokedex.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pokedex.domain.models.Pokemon
import com.example.pokedex.util.UiState
import com.example.pokedex.util.parseStatToAbbr
import com.example.pokedex.util.parseStatToColor
import com.example.pokedex.util.parseTypeToColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.util.Locale
import kotlin.math.round

// tela de detalhes do Pokémon
@Composable
fun PokemonDetailScreen(
    dominantColor: Color,
    pokemonName: String,
    navController: NavController,
    topPadding: Dp = 20.dp,
    pokemonImageSize: Dp = 200.dp,
    viewModel: PokemonDetailViewModel
){
    // obter detalhes do Pokémon
    val pokemonInfo = produceState<UiState<Pokemon>>(initialValue = UiState.Loading<Pokemon>()) {
        value = viewModel.getPokemonInfo(pokemonName)
    }

    // tela com gradiente e sombra
    Box(
        modifier = Modifier
            .fillMaxSize()
            // gradiente sutil usando a cor dominante + tom do tema
            .background(
                Brush.verticalGradient(
                    listOf(
                        dominantColor,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(bottom = 16.dp)
    ){
        // header com back e ação de evolução
        PokemonDetailTopSection(
            navController = navController,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 8.dp, vertical = 25.dp),
            pokemonInfo = pokemonInfo.value,
            viewModel = viewModel
        )

        // verifica se o estado é sucesso, erro ou carregando para exibir mensagem de erro, carregamento ou os atributos
        PokemonDetailStateWrapper(
            pokemonInfo = pokemonInfo.value,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topPadding + pokemonImageSize / 2f, start = 16.dp, end = 16.dp, bottom = 16.dp)
                .shadow(10.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .align(Alignment.BottomCenter),
            loadingModifier = Modifier
                .size(100.dp)
                .padding(top = topPadding + pokemonImageSize / 2f, start = 16.dp, end = 16.dp, bottom = 16.dp),
        )

        // imagem flutuante central com borda e sombra
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize()
        ) {
            if (pokemonInfo.value is UiState.Success<*>) {
                val data = (pokemonInfo.value as UiState.Success<Pokemon>).data
                val context = LocalContext.current
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(data?.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = data?.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(pokemonImageSize)
                        .offset(y = topPadding)
                        .shadow(12.dp, RoundedCornerShape(100.dp))
                        .clip(RoundedCornerShape(100.dp))
                        .border(3.dp, MaterialTheme.colorScheme.surface, RoundedCornerShape(100.dp))
                )
            }
        }
    }
}

// header com back e ação de evolução
@Composable
fun PokemonDetailTopSection(
    navController: NavController,
    modifier: Modifier = Modifier,
    pokemonInfo: UiState<Pokemon>,
    viewModel: PokemonDetailViewModel // <--- novo parâmetro
){
    // header redesenhado: back + título + ação de evolução alinhada à direita
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(Color.Transparent)
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(36.dp)
                .clickable{
                    navController.popBackStack()
                }
        )
        Spacer(modifier = Modifier.width(12.dp))

        Spacer(modifier = Modifier.weight(1f)) // empurra o item seguinte para a direita

        // Renderizar evolução apenas quando houver dados válidos
        if (pokemonInfo is UiState.Success && pokemonInfo.data != null) {
            PokemonEvolution(
                pokemonInfo = pokemonInfo.data,
                navController = navController,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .align(Alignment.Top),
                viewModel = viewModel // <--- passa o ViewModel
            )
        }
    }
}

// verifica se o estado é sucesso, erro ou carregando para exibir mensagem de erro, carregamento ou os atributos
@Composable
fun PokemonDetailStateWrapper(
    pokemonInfo: UiState<Pokemon>,
    modifier: Modifier = Modifier,
    loadingModifier: Modifier = Modifier,
) {
    when(pokemonInfo){
        is UiState.Success -> {
            // mostrar detalhes do Pokémon
            PokemonDetailSection(
                pokemonInfo = pokemonInfo.data!!,
                modifier = modifier
                    .offset(y = (-20).dp)
                    .clip(CircleShape),
            )
        }
        is UiState.Error -> {
            // texto de erro
            Text(
                text = pokemonInfo.message!!,
                color = Color.Red,
                modifier = modifier
            )
        }
        is UiState.Loading -> {
            // Indicador de carregamento
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = loadingModifier
            )
        }
    }
}

// Componente com todos os detalhes do pokemon
@Composable
fun PokemonDetailSection(
    pokemonInfo: Pokemon,
    modifier: Modifier = Modifier
) {
    // scroll vertical
    val scrollState = rememberScrollState()

    // Coluna com o id, nome, tipos, altura e peso alinhados
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            // diminuir o offset do card agora mais arredondado
            .offset(y = 100.dp)
            .verticalScroll(scrollState)
            .padding(16.dp) // padding interno para respirar
    ) {
        // Texto com id e nome do Pokémon
        Text(
            text = "#${pokemonInfo.id} ${pokemonInfo.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }}",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(top = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Tipo do pokemon
        if (pokemonInfo.types.isNotEmpty()) {
            PokemonTypeSection(types = pokemonInfo.types)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Peso e altura
        PokemonDetailDataSection(
            pokemonWeight = pokemonInfo.weight,
            pokemonHeight = pokemonInfo.height
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Status
        if (pokemonInfo.stats.isNotEmpty()) {
            PokemonBaseStats(pokemonInfo = pokemonInfo)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// Tipo de pokemon
@Composable
fun PokemonTypeSection(types: List<String>) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        // procura todos os tipos do pokemon e cria um chip de cor para cada um
        types.forEach { type ->
            val chipColor = parseTypeToColor(type)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(horizontal = 8.dp)
                    .clip(CircleShape)
                    .background(chipColor)
                    .height(35.dp)
            ){
                // texto com o tipo do pokemon
                Text(
                    text = type.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    }
}

// Detalhes do pokemon, peso e altura
@Composable
fun PokemonDetailDataSection(
    pokemonWeight: Float,
    pokemonHeight: Float,
    sectionHeight: Dp = 80.dp
){
    // arredonda peso e altura para 1 casa decimal
    val pokemonWeightInKg = remember {
        round(pokemonWeight.div(10f))
    }
    val pokemonHeightInMeters = remember {
        round(pokemonHeight.div(10f))
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ){
        // Peso do pokemon
        PokemonDetailDataItem(
            dataValue = pokemonWeightInKg,
            dataUnit = "kg",
            dataIcon = painterResource(id = R.drawable.ic_peso),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier
            .size(1.dp, sectionHeight)
            .background(Color.LightGray)
        )
        // Altura do pokemon
        PokemonDetailDataItem(
            dataValue = pokemonHeightInMeters,
            dataUnit = "m",
            dataIcon = painterResource(id = R.drawable.ic_altura),
            modifier = Modifier.weight(1f)
        )
    }

}

// item de detalhes do pokemon
@Composable
fun PokemonDetailDataItem(
    dataValue: Float,
    dataUnit: String,
    dataIcon: Painter,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        // ícone do pokemon
        Icon(painter = dataIcon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(8.dp))
        // valor do pokemon
        Text(
            text = "$dataValue$dataUnit",
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// Barra de status do pokemon
@Composable
fun PokemonStat(
    statName: String,
    statValue: Int,
    statMaxValue: Int,
    statColor: Color,
    height: Dp = 28.dp,
    animDuration: Int = 1000,
    animDelay: Int = 0
) {
    // animação do progresso
    var animationPlayed by remember { mutableStateOf(false) }

    // valor do progresso
    val curPercent = animateFloatAsState(
        targetValue = if (animationPlayed) {
            statValue / statMaxValue.toFloat()
        } else 0f,
        animationSpec = tween(
            animDuration,
            animDelay
        ), label = "statPercent"
    )

    // animação apenas uma vez
    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }
    // Caixa com fundo circular sutil
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(
                if (isSystemInDarkTheme()) {
                    Color(0xFF505050)
                } else {
                    Color.LightGray
                }
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(curPercent.value)
                .clip(CircleShape)
                .background(statColor)
                .padding(horizontal = 8.dp)
        ) {
            // textos com nome e valor
            Text(
                text = statName,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = (curPercent.value * statMaxValue).toInt().toString(),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Atributos de status
@Composable
fun PokemonBaseStats(
    pokemonInfo: Pokemon,
    animDelayPerItem: Int = 100
) {
    // valor máximo de cada stat
    val maxBaseStat = remember(pokemonInfo.stats) {
        pokemonInfo.stats.maxOfOrNull { it.baseStat } ?: 100
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Texto de titulo
        Text(
            text = "Base stats:",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))

        // Exibe cada status do pokemon
        for(i in pokemonInfo.stats.indices){
            val stat = pokemonInfo.stats[i]
            PokemonStat(
                statName = parseStatToAbbr(stat.name),
                statValue = stat.baseStat,
                statMaxValue = maxBaseStat,
                statColor = parseStatToColor(stat.name),
                animDelay = i * animDelayPerItem
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// Botão de evolução
@Composable
fun PokemonEvolution(
    pokemonInfo: Pokemon,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: PokemonDetailViewModel // <--- recebe ViewModel
){
    // agora a UI dispara a ação e observa o estado exposto pelo ViewModel
    var showDialog by remember { mutableStateOf(false) }
    var showEmptyDialog by remember { mutableStateOf(false) }
    // novo flag para indicar que o usuário pediu carregamento de evoluções
    val requested = remember { mutableStateOf(false) }

    val evolutionsState by viewModel.evolutionsState.collectAsState()

    // reage às mudanças de estado apenas se o carregamento foi requisitado
    LaunchedEffect(evolutionsState, requested.value) {
        if (!requested.value) return@LaunchedEffect

        when (evolutionsState) {
            is UiState.Loading -> {
            }
            is UiState.Success -> {
                // lista de evoluções
                val list = (evolutionsState as UiState.Success<List<String>>).data
                if (list.isNullOrEmpty()) {
                    showEmptyDialog = true
                } else if (list.size == 1) {
                    // navega direto para a tela de detalhes do Pokémon
                    navController.navigate("pokemon_detail_screen/0/${list[0]}")
                } else {
                    showDialog = true
                }
                // resetar pedido para evitar reações repetidas ao estado inicial
                requested.value = false
            }
            is UiState.Error -> {
                // mostra diálogo de erro
                showEmptyDialog = true
                requested.value = false
            }
            else -> {}
        }
    }

    // Botão de evolução
    Icon(
        imageVector = Icons.Default.Star,
        contentDescription = null,
        tint = Color.Yellow,
        modifier = modifier
            .size(44.dp)
            .clickable{
                // marca que o usuário solicitou e dispara a carga no ViewModel
                requested.value = true
                // carrega as evoluções do Pokémon
                viewModel.loadEvolutions(pokemonInfo.id, pokemonInfo.name)
            }
            .background(Color.White.copy(alpha = 0.12f), CircleShape)
            .padding(8.dp)
    )

    // Mostra o diálogo de evolução se solicitado
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = { /* vazio */ },
            text = {
                // Coluna com os nomes das evoluções
                Column{
                    Text(text = "Escolha uma evolução:")
                    Spacer(modifier = Modifier.height(8.dp))
                    // Lista de evoluções
                    val list = (evolutionsState as? UiState.Success<List<String>>)?.data ?: emptyList()
                    list.forEach { evoName ->
                        TextButton(onClick = {
                            showDialog = false
                            // navega para a tela de detalhes do Pokémon
                            navController.navigate("pokemon_detail_screen/0/$evoName")
                        }) {
                            Text(text = evoName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() })
                        }
                    }
                }
            }
        )
    }

    // Mostra o diálogo de erro se solicitado
    if (showEmptyDialog) {
        // mostra diálogo de erro
        AlertDialog(
            onDismissRequest = { showEmptyDialog = false },
            confirmButton = {
                TextButton(onClick = { showEmptyDialog = false }) {
                    Text("OK")
                }
            },
            text = {
                Text(text = "Nenhuma evolução disponível para este Pokémon.")
            }
        )
    }
}
