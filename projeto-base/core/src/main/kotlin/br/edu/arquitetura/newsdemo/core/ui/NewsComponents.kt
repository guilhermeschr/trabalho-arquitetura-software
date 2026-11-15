package br.edu.arquitetura.newsdemo.core.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.edu.arquitetura.newsdemo.core.model.UserNews
import java.time.format.DateTimeFormatter

@Composable
fun FavoriteButton(item: UserNews, pending: Boolean, onFavorite: (String, Boolean) -> Unit) {
    val action = if (item.isFavorite) "Remover dos favoritos" else "Salvar nos favoritos"
    IconButton(
        onClick = { onFavorite(item.news.id, !item.isFavorite) },
        enabled = !pending,
        modifier = Modifier.size(48.dp).testTag("favorite-${item.news.id}"),
    ) {
        Icon(if (item.isFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
            contentDescription = "$action: ${item.news.title}", tint = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun NewsCard(item: UserNews, pending: Boolean, onOpen: (String) -> Unit, onFavorite: (String, Boolean) -> Unit) {
    Card(onClick = { onOpen(item.news.id) }, modifier = Modifier.fillMaxWidth().testTag("news-${item.news.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.padding(start = 20.dp, top = 16.dp, end = 12.dp, bottom = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(item.news.category.uppercase(), style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
                Text(item.news.publishedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    style = MaterialTheme.typography.labelMedium)
            }
            Spacer(Modifier.height(8.dp))
            Text(item.news.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Text(item.news.description, style = MaterialTheme.typography.bodyMedium)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Conteúdo de demonstração", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                FavoriteButton(item, pending, onFavorite)
            }
        }
    }
}

@Composable
fun NewsContent(state: NewsUiState, favorites: Boolean = false, onOpen: (String) -> Unit,
    onFavorite: (String, Boolean) -> Unit, onRetry: () -> Unit, onDismissMessage: () -> Unit) {
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(state.message, state.status) {
        if (state.message != null && state.status != LoadStatus.Error) {
            val result = snackbar.showSnackbar(state.message, actionLabel = "Tentar novamente", withDismissAction = true)
            if (result == SnackbarResult.ActionPerformed) onRetry() else onDismissMessage()
        }
    }
    Box(Modifier.fillMaxSize()) {
        when (state.status) {
            LoadStatus.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center).testTag("loading"))
            LoadStatus.Error -> Message("Não foi possível carregar", state.message ?: "Tente novamente.", onRetry)
            LoadStatus.Empty -> Message(if (favorites) "Seus favoritos aparecem aqui" else "Nenhuma notícia disponível",
                if (favorites) "Salve uma notícia tocando no marcador." else "Volte mais tarde para ver novos conteúdos.")
            LoadStatus.NotFound -> Message("Notícia não encontrada", "Este conteúdo não está no catálogo local.")
            LoadStatus.Success -> LazyColumn(Modifier.fillMaxSize().testTag("news-list"),
                contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                item {
                    Text(if (favorites) "Para ler de novo" else "Explore ideias sobre software",
                        style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text(if (favorites) "Seus conteúdos salvos, sempre disponíveis." else "12 conteúdos fictícios para explorar e salvar.",
                        style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(8.dp))
                }
                items(state.items, key = { it.news.id }) { item -> NewsCard(item, item.news.id in state.pendingIds, onOpen, onFavorite) }
            }
        }
        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun Message(title: String, description: String, retry: (() -> Unit)? = null) {
    Column(Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))
        Text(description, style = MaterialTheme.typography.bodyLarge)
        if (retry != null) {
            Spacer(Modifier.height(20.dp))
            Button(onClick = retry, modifier = Modifier.heightIn(min = 48.dp)) { Text("Tentar novamente") }
        }
    }
}
