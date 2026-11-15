package br.edu.arquitetura.newsdemo.core.domain

import br.edu.arquitetura.newsdemo.test.*
import br.edu.arquitetura.newsdemo.core.data.NewsRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveNewsWithFavoritesUseCaseTest {
    @Test fun combinesFavoritesAndSortsNews() = runTest {
        val news = FakeNewsRepository().apply { this.news.value = sampleNews.reversed() }
        val favorites = FakeFavoritesRepository().apply { ids.value = setOf("01") }
        val result = ObserveNewsWithFavoritesUseCase(news, favorites)().first()
        assertEquals(listOf("01", "02"), result.map { it.news.id })
        assertTrue(result.first().isFavorite)
        assertFalse(result.last().isFavorite)
    }
    @Test fun emitsAfterFavoriteChanges() = runTest {
        val favorites = FakeFavoritesRepository()
        val emissions = mutableListOf<Boolean>()
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            ObserveNewsWithFavoritesUseCase(FakeNewsRepository(), favorites)().collect { emissions += it.first().isFavorite }
        }
        runCurrent()
        favorites.setFavorite("01", true); runCurrent()
        favorites.setFavorite("01", false); runCurrent()
        assertEquals(listOf(false, true, false), emissions)
        job.cancel()
    }
    @Test fun staleFavoriteDoesNotCreateNews() = runTest {
        val favorites = FakeFavoritesRepository().apply { ids.value = setOf("removed") }
        val result = ObserveNewsWithFavoritesUseCase(FakeNewsRepository(), favorites)().first()
        assertEquals(2, result.size)
        assertTrue(result.none { it.isFavorite })
    }
    @Test fun emptyCatalogProducesEmptyList() = runTest {
        val news = FakeNewsRepository().apply { this.news.value = emptyList() }
        assertTrue(ObserveNewsWithFavoritesUseCase(news, FakeFavoritesRepository())().first().isEmpty())
    }
    @Test fun waitsForBothSources() = runTest {
        val deferredNews = MutableSharedFlow<List<br.edu.arquitetura.newsdemo.core.model.News>>()
        val news = object : NewsRepository { override fun observeNews() = deferredNews }
        var emitted = false
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            ObserveNewsWithFavoritesUseCase(news, FakeFavoritesRepository())().collect { emitted = true }
        }
        runCurrent(); assertFalse(emitted)
        deferredNews.emit(sampleNews); runCurrent(); assertTrue(emitted)
    }
}
