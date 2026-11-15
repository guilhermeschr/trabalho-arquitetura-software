package br.edu.arquitetura.newsdemo.core.data

import br.edu.arquitetura.newsdemo.core.database.FavoriteDao
import br.edu.arquitetura.newsdemo.core.database.FavoriteEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class RepositoryTest {
    private class FakeDao : FavoriteDao {
        val ids = MutableStateFlow<List<String>>(emptyList())
        override fun observeIds() = ids
        override suspend fun insert(favorite: FavoriteEntity) { ids.value = (ids.value + favorite.newsId).distinct() }
        override suspend fun delete(id: String) { ids.value = ids.value - id }
    }
    @Test fun localCatalogHasTwelveUniqueItems() = runTest {
        val items = LocalNewsRepository().observeNews().first()
        assertEquals(12, items.size)
        assertEquals(12, items.map { it.id }.toSet().size)
        assertTrue(items.all { it.content.isNotBlank() && it.description.isNotBlank() })
    }
    @Test fun saveIsIdempotent() = runTest {
        val repo = RoomFavoritesRepository(FakeDao())
        repo.setFavorite("01", true); repo.setFavorite("01", true)
        assertEquals(setOf("01"), repo.observeFavoriteIds().first())
    }
    @Test fun removeIsIdempotent() = runTest {
        val repo = RoomFavoritesRepository(FakeDao())
        repo.setFavorite("01", true); repo.setFavorite("01", false); repo.setFavorite("01", false)
        assertTrue(repo.observeFavoriteIds().first().isEmpty())
    }
    @Test fun multipleFavoritesAreIndependent() = runTest {
        val repo = RoomFavoritesRepository(FakeDao())
        repo.setFavorite("01", true); repo.setFavorite("02", true); repo.setFavorite("01", false)
        assertEquals(setOf("02"), repo.observeFavoriteIds().first())
    }
    @Test(expected = IllegalArgumentException::class)
    fun blankIdIsRejected() = runTest { RoomFavoritesRepository(FakeDao()).setFavorite(" ", true) }
}
