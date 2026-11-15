package br.edu.arquitetura.newsdemo.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.arquitetura.newsdemo.core.data.FavoritesRepository
import br.edu.arquitetura.newsdemo.core.domain.ObserveNewsWithFavoritesUseCase
import br.edu.arquitetura.newsdemo.core.ui.FeedStateHolder
import br.edu.arquitetura.newsdemo.core.ui.LoadStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    observeNews: ObserveNewsWithFavoritesUseCase,
    favorites: FavoritesRepository,
 ) : ViewModel() {
    private val holder = FeedStateHolder(observeNews, favorites, viewModelScope,
        select = { items -> items.filter { it.isFavorite } } )
    val state = holder.state
    fun setFavorite(id: String, selected: Boolean) = holder.setFavorite(id, selected)
    fun retry() = holder.retry()
    fun dismissMessage() = holder.dismissMessage()
}
