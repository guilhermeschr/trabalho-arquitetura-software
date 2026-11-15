package br.edu.arquitetura.newsdemo.feature.favorites

import br.edu.arquitetura.newsdemo.core.domain.ObserveNewsWithFavoritesUseCase
import br.edu.arquitetura.newsdemo.core.ui.LoadStatus
import br.edu.arquitetura.newsdemo.test.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {
    @get:Rule val main = MainDispatcherRule()
    private val news = FakeNewsRepository()
    private val favorites = FakeFavoritesRepository().apply { ids.value = setOf("01") }
    private fun viewModel() = FavoritesViewModel(ObserveNewsWithFavoritesUseCase(news, favorites), favorites )
    @Test fun initiallyLoading() = runTest {
        val vm = viewModel()
        assertEquals(LoadStatus.Loading, vm.state.value.status)
        runCurrent()
    }
    @Test fun loadsExpectedContent() = runTest {
        val vm = viewModel(); runCurrent()
        assertEquals(LoadStatus.Success, vm.state.value.status)
        assertEquals(1, vm.state.value.items.size)
    }
    @Test fun handlesEmptyCatalog() = runTest {
        news.news.value = emptyList()
        val vm = viewModel(); runCurrent()
        assertEquals(LoadStatus.Empty, vm.state.value.status)
    }
    @Test fun readErrorCanBeRetried() = runTest {
        news.failReads = true
        val vm = viewModel(); runCurrent()
        assertEquals(LoadStatus.Error, vm.state.value.status)
        news.failReads = false
        vm.retry(); runCurrent()
        assertEquals(LoadStatus.Success, vm.state.value.status)
    }
    @Test fun removingFavoriteUpdatesState() = runTest {
        val vm = viewModel(); runCurrent()
        vm.setFavorite("01", false); runCurrent()
        assertFalse("01" in favorites.ids.value)
        assertEquals(LoadStatus.Empty, vm.state.value.status)
    }
    @Test fun failedWritePreservesContentAndRetrySucceeds() = runTest {
        val vm = viewModel(); runCurrent()
        val before = vm.state.value.items
        favorites.failWrites = true
        vm.setFavorite("01", false); runCurrent()
        assertEquals(before, vm.state.value.items)
        assertNotNull(vm.state.value.message)
        assertTrue(vm.state.value.pendingIds.isEmpty())
        favorites.failWrites = false
        vm.retry(); runCurrent()
        assertFalse("01" in favorites.ids.value)
        assertNull(vm.state.value.message)
    }
    @Test fun disablesDuplicateWriteWhilePending() = runTest {
        val vm = viewModel(); runCurrent()
        val gate = CompletableDeferred<Unit>()
        favorites.gate = gate
        vm.setFavorite("01", false); runCurrent()
        vm.setFavorite("01", false); runCurrent()
        assertEquals(setOf("01"), vm.state.value.pendingIds)
        assertEquals(1, favorites.writes)
        gate.complete(Unit); runCurrent()
        assertTrue(vm.state.value.pendingIds.isEmpty())
    }
    @Test fun dismissesWriteError() = runTest {
        val vm = viewModel(); runCurrent()
        favorites.failWrites = true
        vm.setFavorite("01", false); runCurrent()
        vm.dismissMessage()
        assertNull(vm.state.value.message)
    }
    @Test fun newFavoriteAppearsWithoutReload() = runTest {
        val vm = viewModel(); runCurrent()
        favorites.setFavorite("02", true); runCurrent()
        assertEquals(listOf("01", "02"), vm.state.value.items.map { it.news.id })
    }
}
