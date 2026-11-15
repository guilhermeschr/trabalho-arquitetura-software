package br.edu.arquitetura.newsdemo.core.data

import br.edu.arquitetura.newsdemo.core.database.FavoriteDao
import br.edu.arquitetura.newsdemo.core.database.FavoriteEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class RoomFavoritesRepository @Inject constructor(
    private val dao: FavoriteDao,
) : FavoritesRepository {
    override fun observeFavoriteIds(): Flow<Set<String>> = dao.observeIds().map { it.toSet() }
    override suspend fun setFavorite(id: String, selected: Boolean) {
        require(id.isNotBlank()) { "O identificador não pode estar vazio." }
        if (selected) dao.insert(FavoriteEntity(id)) else dao.delete(id)
    }
}
