package br.edu.arquitetura.newsdemo.test

import br.edu.arquitetura.newsdemo.core.data.FavoritesRepository
import br.edu.arquitetura.newsdemo.core.data.NewsRepository
import br.edu.arquitetura.newsdemo.core.model.News
import java.time.LocalDate
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule : TestWatcher() {
    val dispatcher = StandardTestDispatcher()
    override fun starting(description: Description) { Dispatchers.setMain(dispatcher) }
    override fun finished(description: Description) { Dispatchers.resetMain() }
}

val sampleNews = listOf(
    News("01", "Primeira", "Resumo", "Conteúdo", "Android", LocalDate.of(2026, 11, 15)),
    News("02", "Segunda", "Resumo", "Conteúdo", "Kotlin", LocalDate.of(2026, 11, 14)),
)
class FakeNewsRepository : NewsRepository {
    val news = MutableStateFlow(sampleNews)
    var failReads = false
    override fun observeNews(): Flow<List<News>> = flow {
        if (failReads) throw IllegalStateException("Leitura indisponível")
        emitAll(news)
    }
}
class FakeFavoritesRepository : FavoritesRepository {
    val ids = MutableStateFlow<Set<String>>(emptySet())
    var failWrites = false
    var gate: CompletableDeferred<Unit>? = null
    var writes = 0
    override fun observeFavoriteIds(): Flow<Set<String>> = ids
    override suspend fun setFavorite(id: String, selected: Boolean) {
        writes++
        gate?.await()
        if (failWrites) throw IllegalStateException("Gravação indisponível")
        ids.value = if (selected) ids.value + id else ids.value - id
    }
}
