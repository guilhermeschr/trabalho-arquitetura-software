package br.edu.arquitetura.newsdemo.core.ui

import br.edu.arquitetura.newsdemo.core.data.FavoritesRepository
import br.edu.arquitetura.newsdemo.core.domain.ObserveNewsWithFavoritesUseCase
import br.edu.arquitetura.newsdemo.core.model.UserNews
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Compartilha somente o comportamento de apresentação entre os três ViewModels.
class FeedStateHolder(
    private val observeNews: ObserveNewsWithFavoritesUseCase,
    private val favorites: FavoritesRepository,
    private val scope: CoroutineScope,
    private val select: (List<UserNews>) -> List<UserNews> = { it },
    private val emptyStatus: LoadStatus = LoadStatus.Empty,
) {
    private val mutableState = MutableStateFlow(NewsUiState())
    val state: StateFlow<NewsUiState> = mutableState.asStateFlow()
    private var observation: Job? = null
    private var failedAction: Pair<String, Boolean>? = null

    init { observe() }

    private fun observe() {
        observation?.cancel()
        mutableState.update { it.copy(status = LoadStatus.Loading, message = null) }
        observation = scope.launch {
            observeNews().catch { error ->
                if (error is CancellationException) throw error
                mutableState.update { it.copy(status = LoadStatus.Error, message = "Não foi possível carregar as notícias.") }
            }.collect { news ->
                val selected = select(news)
                mutableState.update {
                    it.copy(items = selected, status = if (selected.isEmpty()) emptyStatus else LoadStatus.Success)
                }
            }
        }
    }

    fun setFavorite(id: String, selected: Boolean) {
        if (id in state.value.pendingIds) return
        mutableState.update { it.copy(pendingIds = it.pendingIds + id, message = null) }
        scope.launch {
            try {
                favorites.setFavorite(id, selected)
                failedAction = null
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                failedAction = id to selected
                mutableState.update { it.copy(message = "Não foi possível salvar a alteração.") }
            } finally {
                mutableState.update { it.copy(pendingIds = it.pendingIds - id) }
            }
        }
    }

    fun retry() {
        val action = failedAction
        if (action != null) setFavorite(action.first, action.second) else observe()
    }
    fun dismissMessage() {
        failedAction = null
        mutableState.update { it.copy(message = null) }
    }
}
