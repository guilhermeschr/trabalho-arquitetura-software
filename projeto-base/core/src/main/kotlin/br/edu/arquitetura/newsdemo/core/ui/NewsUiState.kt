package br.edu.arquitetura.newsdemo.core.ui

import br.edu.arquitetura.newsdemo.core.model.UserNews

enum class LoadStatus { Loading, Success, Empty, Error, NotFound }
data class NewsUiState(
    val status: LoadStatus = LoadStatus.Loading,
    val items: List<UserNews> = emptyList(),
    val pendingIds: Set<String> = emptySet(),
    val message: String? = null,
)
