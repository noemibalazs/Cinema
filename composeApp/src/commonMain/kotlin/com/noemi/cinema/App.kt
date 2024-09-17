package com.noemi.cinema

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.noemi.cinema.screens.main.MoviesApp
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.noemi.cinema.theme.CinemaTheme
import moe.tlaster.precompose.PreComposeApp

@Composable
@Preview
fun App() {
    val snackBarHostState = remember { SnackbarHostState() }

    CinemaTheme {

        PreComposeApp {

            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary),
                content = {
                    MoviesApp(snackBarHostState)
                },
                snackbarHost = {
                    SnackbarHost(hostState = snackBarHostState)
                }
            )
        }
    }
}