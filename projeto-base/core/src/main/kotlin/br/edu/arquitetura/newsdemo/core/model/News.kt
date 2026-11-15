package br.edu.arquitetura.newsdemo.core.model

import java.time.LocalDate

data class News(
    val id: String,
    val title: String,
    val description: String,
    val content: String,
    val category: String,
    val publishedAt: LocalDate,
)

data class UserNews(val news: News, val isFavorite: Boolean)
