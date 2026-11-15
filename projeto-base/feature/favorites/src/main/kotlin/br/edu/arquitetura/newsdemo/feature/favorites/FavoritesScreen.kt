package br.edu.arquitetura.newsdemo.feature.favorites

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.edu.arquitetura.newsdemo.core.ui.NewsContent

@Composable
fun FavoritesRoute(viewModel: FavoritesViewModel, onOpen: (String) -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    NewsContent(state, favorites = true, onOpen = onOpen, onFavorite = viewModel::setFavorite,
        onRetry = viewModel::retry, onDismissMessage = viewModel::dismissMessage)
}
