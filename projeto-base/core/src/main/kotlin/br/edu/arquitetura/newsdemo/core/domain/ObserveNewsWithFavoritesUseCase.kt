package br.edu.arquitetura.newsdemo.core.domain

import br.edu.arquitetura.newsdemo.core.data.FavoritesRepository
import br.edu.arquitetura.newsdemo.core.data.NewsRepository
import br.edu.arquitetura.newsdemo.core.model.UserNews
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveNewsWithFavoritesUseCase @Inject constructor(
    private val newsRepository: NewsRepository,
    private val favoritesRepository: FavoritesRepository,
) {
    operator fun invoke(): Flow<List<UserNews>> = combine(
        newsRepository.observeNews(), favoritesRepository.observeFavoriteIds(),
    ) { news, favorites ->
        news.sortedWith(compareByDescending<br.edu.arquitetura.newsdemo.core.model.News> { it.publishedAt }.thenBy { it.id })
            .map { UserNews(it, it.id in favorites) }
    }
}
