package com.example.pokedex.ui.detail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import com.example.pokedex.R
import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
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

@Composable
fun PokemonDetailScreen(
    dominantColor: Color,
    pokemonName: String,
    navController: NavController,
    topPadding: Dp = 20.dp,
    pokemonImageSize: Dp = 200.dp,
    viewModel: PokemonDetailViewModel
){
    val pokemonInfo = produceState<UiState<Pokemon>>(initialValue = UiState.Loading<Pokemon>()) {
        value = viewModel.getPokemonInfo(pokemonName)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(dominantColor)
            .padding(bottom = 16.dp)
    ){
        PokemonDetailTopSection(
            navController = navController,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter),
            pokemonInfo = pokemonInfo.value
        )
        PokemonDetailStateWrapper(
            pokemonInfo = pokemonInfo.value,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topPadding + pokemonImageSize / 2f, start = 16.dp, end = 16.dp, bottom = 16.dp)
                .shadow(10.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surface)
                .align(Alignment.BottomCenter),
            loadingModifier = Modifier
                .size(100.dp)
                .padding(top = topPadding + pokemonImageSize / 2f, start = 16.dp, end = 16.dp, bottom = 16.dp),
        )
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize()
        ) {
            if (pokemonInfo.value is UiState.Success<*>) {
                val data = (pokemonInfo.value as UiState.Success<Pokemon>).data
                AsyncImage(
                    model = ImageRequest.Builder(navController.context)
                        .data(data?.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = data?.name,
                    modifier = Modifier
                        .size(pokemonImageSize)
                        .offset(y = topPadding)
                )
            }
        }
    }
}

@Composable
fun PokemonDetailTopSection(
    navController: NavController,
    modifier: Modifier = Modifier,
    pokemonInfo: UiState<Pokemon>
){
    Box(
        contentAlignment = Alignment.TopStart,
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.Black,
                        Color.Transparent
                    )
                )
            )
    ){
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(36.dp)
                .offset(16.dp, 16.dp)
                .clickable{
                    navController.popBackStack()
                }
        )

        // Renderizar evolução apenas quando houver dados válidos
        if (pokemonInfo is UiState.Success && pokemonInfo.data != null) {
            PokemonEvolution(
                pokemonInfo = pokemonInfo.data,
                navController = navController,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PokemonDetailStateWrapper(
    pokemonInfo: UiState<Pokemon>,
    modifier: Modifier = Modifier,
    loadingModifier: Modifier = Modifier,
) {
    when(pokemonInfo){
        is UiState.Success -> {
            PokemonDetailSection(
                pokemonInfo = pokemonInfo.data!!,
                modifier = modifier
                    .offset(y = (-20).dp)
                    .clip(CircleShape),
            )
        }
        is UiState.Error -> {
            Text(
                text = pokemonInfo.message!!,
                color = Color.Red,
                modifier = modifier
            )
        }
        is UiState.Loading -> {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = loadingModifier
            )
        }
    }
}

@Composable
fun PokemonDetailSection(
    pokemonInfo: Pokemon,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .offset(y = 120.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "#${pokemonInfo.id} ${pokemonInfo.name.capitalize(Locale.ROOT)}",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (pokemonInfo.types.isNotEmpty()) {
            PokemonTypeSection(types = pokemonInfo.types)
        }
        PokemonDetailDataSection(
            pokemonWeight = pokemonInfo.weight,
            pokemonHeight = pokemonInfo.height
        )
        if (pokemonInfo.stats.isNotEmpty()) {
            PokemonBaseStats(pokemonInfo = pokemonInfo)
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun PokemonTypeSection(types: List<String>) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
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

@Composable
fun PokemonDetailDataSection(
    pokemonWeight: Int,
    pokemonHeight: Int,
    sectionHeight: Dp = 80.dp
){
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
        PokemonDetailDataItem(
            dataValue = pokemonHeightInMeters,
            dataUnit = "m",
            dataIcon = painterResource(id = R.drawable.ic_altura),
            modifier = Modifier.weight(1f)
        )
    }

}

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
        Icon(painter = dataIcon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$dataValue$dataUnit",
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

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
    var animationPlayed by remember { mutableStateOf(false) }
    val curPercent = animateFloatAsState(
        targetValue = if (animationPlayed) {
            statValue / statMaxValue.toFloat()
        } else 0f,
        animationSpec = tween(
            animDuration,
            animDelay
        ), label = "statPercent"
    )
    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }
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

@Composable
fun PokemonBaseStats(
    pokemonInfo: Pokemon,
    animDelayPerItem: Int = 100
) {
    val maxBaseStat = remember(pokemonInfo.stats) {
        pokemonInfo.stats.maxOfOrNull { it.baseStat } ?: 100
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Base stats:",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))

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

@Composable
fun PokemonEvolution(
    pokemonInfo: Pokemon,
    navController: NavController,
    modifier: Modifier = Modifier
){
    val uriHandler = LocalUriHandler.current
    val evolutionUrl = "https://pokeapi.co/api/v2/evolution-chain/${pokemonInfo.id}/"

    var showDialog by remember { mutableStateOf(false) }
    var evolutions by remember { mutableStateOf(listOf<String>()) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun parseNamesRecursively(chainObj: JSONObject, list: MutableList<String>){
        val speciesName = chainObj.getJSONObject("species").getString("name")
        list.add(speciesName)
        val evolvesTo = chainObj.getJSONArray("evolves_to")
        for(i in 0 until evolvesTo.length()){
            parseNamesRecursively(evolvesTo.getJSONObject(i), list)
        }
    }

    fun fetchEvolutions(){
        scope.launch {
            isLoading = true
            try{
                val jsonText = withContext(Dispatchers.IO) {
                    URL(evolutionUrl).readText()
                }
                val root = JSONObject(jsonText)
                val chain = root.getJSONObject("chain")
                val names = mutableListOf<String>()
                parseNamesRecursively(chain, names)
                // remover duplicados e o proprio pokemon
                val filtered = names.distinct().filter { it != pokemonInfo.name.lowercase(Locale.ROOT) }
                evolutions = filtered
                when(filtered.size){
                    0 -> uriHandler.openUri(evolutionUrl)
                    1 -> {
                        // navega direto para detalhe usando rota existente; cor placeholder 0
                        navController.navigate("pokemon_detail_screen/0/${filtered[0]}")
                    }
                    else -> showDialog = true
                }
            } catch (e: Exception){
                uriHandler.openUri(evolutionUrl)
            } finally {
                isLoading = false
            }
        }
    }

    Box(
        contentAlignment = Alignment.TopEnd,
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.Black,
                        Color.Transparent
                    )
                )
            )
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(45.dp)
                .offset((-16).dp, 16.dp)
                .padding(8.dp)
                .clickable{
                    if(!isLoading) fetchEvolutions()
                }
        )
    }

    if(showDialog){
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = { /* vazio, usamos botões por evolução */ },
            text = {
                Column{
                    Text(text = "Escolha uma evolução:")
                    Spacer(modifier = Modifier.height(8.dp))
                    evolutions.forEach { evoName ->
                        TextButton(onClick = {
                            showDialog = false
                            // usa a mesma rota de detalhe; cor placeholder 0
                            navController.navigate("pokemon_detail_screen/0/$evoName")
                        }) {
                            Text(text = evoName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() })
                        }
                    }
                }
            }
        )
    }
}
