package br.edu.arquitetura.newsdemo.feature.news

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.edu.arquitetura.newsdemo.core.ui.*
import java.time.format.DateTimeFormatter

@Composable
fun NewsRoute(viewModel: NewsViewModel, onOpen: (String) -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    NewsContent(state, onOpen = onOpen, onFavorite = viewModel::setFavorite,
        onRetry = viewModel::retry, onDismissMessage = viewModel::dismissMessage)
}

@Composable
fun NewsDetailRoute(viewModel: NewsDetailViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    if (state.status != LoadStatus.Success) {
        NewsContent(state, onOpen = {}, onFavorite = viewModel::setFavorite,
            onRetry = viewModel::retry, onDismissMessage = viewModel::dismissMessage)
        return
    }
    val item = state.items.single()
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(state.message) {
        state.message?.let {
            if (snackbar.showSnackbar(it, "Tentar novamente", withDismissAction = true) == SnackbarResult.ActionPerformed)
                viewModel.retry() else viewModel.dismissMessage()
        }
    }
    Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { insets ->
        Column(Modifier.fillMaxSize().padding(insets).verticalScroll(rememberScrollState()).padding(24.dp)) {
            Text(item.news.category.uppercase(), color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(12.dp))
            Text(item.news.title, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.SemiBold)
            Row(Modifier.fillMaxWidth(), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text(item.news.publishedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                FavoriteButton(item, item.news.id in state.pendingIds, viewModel::setFavorite)
            }
            HorizontalDivider()
            Spacer(Modifier.height(24.dp))
            Text(item.news.content, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(28.dp))
            Text("Notícia fictícia · Demonstração acadêmica", style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
