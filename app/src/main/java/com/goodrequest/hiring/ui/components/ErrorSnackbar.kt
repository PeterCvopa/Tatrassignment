package com.goodrequest.hiring.ui.components

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.goodrequest.hiring.model.PokemonError
import kotlinx.coroutines.launch

@Composable
fun SnackbarScreen(pokemonError: PokemonError?) {
    val snackbarHostState = remember { SnackbarHostState() }
    if (pokemonError != null) {
        val scope = rememberCoroutineScope()
        LaunchedEffect(key1 = pokemonError) {
            scope.launch {
                snackbarHostState.showSnackbar("Error: ${pokemonError.messageRes}")
            }
        }
        SnackbarHost(hostState = snackbarHostState)
    }
}