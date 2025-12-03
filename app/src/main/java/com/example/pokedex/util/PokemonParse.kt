package com.example.pokedex.util

import androidx.compose.ui.graphics.Color
import java.util.Locale

// Mapa de cores por tipo (chaves em minúsculas)
private val TYPE_COLORS: Map<String, Color> = mapOf(
    "normal" to Color(0xFFA8A77A),
    "fire" to Color(0xFFEE8130),
    "water" to Color(0xFF6390F0),
    "electric" to Color(0xFFF7D02C),
    "grass" to Color(0xFF7AC74C),
    "ice" to Color(0xFF96D9D6),
    "fighting" to Color(0xFFC22E28),
    "poison" to Color(0xFFA33EA1),
    "ground" to Color(0xFFE2BF65),
    "flying" to Color(0xFFA98FF3),
    "psychic" to Color(0xFFF95587),
    "bug" to Color(0xFFA6B91A),
    "rock" to Color(0xFFB6A136),
    "ghost" to Color(0xFF735797),
    "dragon" to Color(0xFF6F35FC),
    "dark" to Color(0xFF705746),
    "steel" to Color(0xFFB7B7CE),
    "fairy" to Color(0xFFD685AD)
)

fun parseTypeToColor(typeName: String): Color {
    val key = typeName.lowercase(Locale.ROOT)
    return TYPE_COLORS[key] ?: Color.Black
}

fun parseStatToColor(statName: String): Color {
    return when(statName.lowercase(Locale.ROOT)) {
        "hp" -> Color(0xFFEF5350)
        "attack" -> Color(0xFFFFA726)
        "defense" -> Color(0xFF66BB6A)
        "special-attack" -> Color(0xFF42A5F5)
        "special-defense" -> Color(0xFF26A69A)
        "speed" -> Color(0xFFAB47BC)
        else -> Color.White
    }
}

fun parseStatToAbbr(statName: String): String {
    return when(statName.lowercase(Locale.ROOT)) {
        "hp" -> "HP"
        "attack" -> "Atk"
        "defense" -> "Def"
        "special-attack" -> "SpAtk"
        "special-defense" -> "SpDef"
        "speed" -> "Spd"
        else -> statName.take(3).uppercase(Locale.ROOT)
    }
}