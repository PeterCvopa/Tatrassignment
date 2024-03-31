package com.goodrequest.hiring.ui.pokedex

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.goodrequest.hiring.R
import com.goodrequest.hiring.model.PokemonWithDetail


@Composable
fun PokemonItem(pokemon: PokemonWithDetail, onClick: (PokemonWithDetail) -> Unit = {}) {

    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick(pokemon) }) {

        AsyncImage(
            modifier = Modifier
                .size(68.dp)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            model = pokemon.detail?.imageUrl,
            contentDescription = null,
            fallback = painterResource(id = R.drawable.ic_image),
        )

        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.Center,
        ) {
            Row {
                Text(
                    modifier = Modifier.padding(top = 2.dp, bottom = 2.dp),
                    text = stringResource(id = R.string.name),
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    modifier = Modifier.padding(start = 2.dp, top = 2.dp, bottom = 2.dp),
                    textAlign = TextAlign.Center,
                    text = pokemon.name
                )
            }

            Row {
                Text(
                    modifier = Modifier.padding(top = 2.dp, bottom = 2.dp),
                    text = stringResource(id = R.string.move),
                    fontWeight = FontWeight.Bold,
                )
                pokemon.detail
                    ?.move
                    ?.let { move ->
                        Text(text = move, modifier = Modifier.padding(start = 2.dp, top = 2.dp, bottom = 2.dp))
                    }
            }
        }
    }
}