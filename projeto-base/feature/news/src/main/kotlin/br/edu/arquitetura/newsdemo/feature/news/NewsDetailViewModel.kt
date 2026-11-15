package br.edu.arquitetura.newsdemo.feature.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.arquitetura.newsdemo.core.data.FavoritesRepository
import br.edu.arquitetura.newsdemo.core.domain.ObserveNewsWithFavoritesUseCase
import br.edu.arquitetura.newsdemo.core.ui.FeedStateHolder
import br.edu.arquitetura.newsdemo.core.ui.LoadStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.SavedStateHandle

@HiltViewModel
class NewsDetailViewModel @Inject constructor(
    observeNews: ObserveNewsWithFavoritesUseCase,
    favorites: FavoritesRepository,
    savedStateHandle: SavedStateHandle,
 ) : ViewModel() {
    private val holder = FeedStateHolder(observeNews, favorites, viewModelScope,
        select = { items -> items.filter { it.news.id == savedStateHandle.get<String>("newsId") } },
        emptyStatus = LoadStatus.NotFound )
    val state = holder.state
    fun setFavorite(id: String, selected: Boolean) = holder.setFavorite(id, selected)
    fun retry() = holder.retry()
    fun dismissMessage() = holder.dismissMessage()
}
