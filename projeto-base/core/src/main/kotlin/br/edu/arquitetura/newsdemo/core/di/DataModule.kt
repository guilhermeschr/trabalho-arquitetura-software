package br.edu.arquitetura.newsdemo.core.di

import android.content.Context
import androidx.room.Room
import br.edu.arquitetura.newsdemo.core.data.FavoritesRepository
import br.edu.arquitetura.newsdemo.core.data.LocalNewsRepository
import br.edu.arquitetura.newsdemo.core.data.NewsRepository
import br.edu.arquitetura.newsdemo.core.data.RoomFavoritesRepository
import br.edu.arquitetura.newsdemo.core.database.FavoriteDao
import br.edu.arquitetura.newsdemo.core.database.FavoritesDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {
    @Binds @Singleton abstract fun news(impl: LocalNewsRepository): NewsRepository
    @Binds @Singleton abstract fun favorites(impl: RoomFavoritesRepository): FavoritesRepository
}

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides @Singleton
    fun database(@ApplicationContext context: Context): FavoritesDatabase =
        Room.databaseBuilder(context, FavoritesDatabase::class.java, "news-favorites.db").build()
    @Provides fun dao(database: FavoritesDatabase): FavoriteDao = database.favoriteDao()
}
