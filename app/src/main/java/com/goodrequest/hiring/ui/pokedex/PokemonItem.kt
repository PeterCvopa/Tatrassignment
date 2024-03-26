package com.goodrequest.hiring.ui.pokedex

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.goodrequest.hiring.model.PokemonData


@Composable
fun PokemonItem(pokemon: PokemonData, onClick: (PokemonData) -> Unit = {}) {
    Row(modifier = Modifier
        .padding(16.dp)
        .clickable { onClick(pokemon) }) {
        Text(text = pokemon.name)
    }
}