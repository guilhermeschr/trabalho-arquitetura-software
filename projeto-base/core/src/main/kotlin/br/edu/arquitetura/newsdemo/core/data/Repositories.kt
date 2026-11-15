package br.edu.arquitetura.newsdemo.core.data

import br.edu.arquitetura.newsdemo.core.model.News
import kotlinx.coroutines.flow.Flow

interface NewsRepository { fun observeNews(): Flow<List<News>> }
interface FavoritesRepository {
    fun observeFavoriteIds(): Flow<Set<String>>
    suspend fun setFavorite(id: String, selected: Boolean)
}
