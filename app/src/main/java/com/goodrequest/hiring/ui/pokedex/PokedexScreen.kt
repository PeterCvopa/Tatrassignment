package com.goodrequest.hiring.ui.pokedex

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.cvopa.peter.fetchy.ui.theme.PokedexTheme
import com.cvopa.peter.fetchy.util.compose.SingleEventEffect
import com.goodrequest.hiring.model.PokemonListState
import com.goodrequest.hiring.ui.components.PullToRefreshLazyColumn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

@Composable
fun PokemonsScreen() {
    PokedexTheme {
        val viewModel: PokemonViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        PokemonsScreen(state, actionFlow = viewModel.sideEffectFlow) { viewModel.onEvent(it) }
    }
}

@Composable
private fun PokemonsScreen(
    state: PokemonListState,
    actionFlow: Flow<Actions> = emptyFlow(),
    onEvent: (Event) -> Unit = {},
) {

    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycle = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    SingleEventEffect(sideEffectFlow = actionFlow) { action ->
        when (action) {
            is Actions.ShowSnackbar -> {
                scope.launch {
                    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        val errorText = context.resources.getString(action.messageRes)
                        snackbarHostState.showSnackbar("Error: $errorText")
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp), text = "Catch 'em all!"
                )
            }
        },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            if (state.error != null) {
                Button(
                    modifier = Modifier.padding(16.dp),
                    onClick = { onEvent(Event.OnLoad) }) {
                    Text("Retry")
                }
            }
            Pokemons(state, onEvent)
        }
    }
}

@Composable
fun Pokemons(pokemonListState: PokemonListState, onEvent: (Event) -> Unit = {}) {
    PullToRefreshLazyColumn(
        items = pokemonListState.pokemonList,
        isRefreshing = pokemonListState.isRefreshing,
        onRefresh = { onEvent(Event.OnLoad) }
    ) { pokemon ->
        PokemonItem(pokemon)
    }
}

@Preview(apiLevel = 33, device = "id:Nexus 4")
@Composable
fun PokemonScreenPreview() {
    PokemonsScreen(PokemonListState())
}
