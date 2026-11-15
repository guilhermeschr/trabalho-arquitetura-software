package br.edu.arquitetura.newsdemo.core.database

import android.content.Context
import android.os.SystemClock
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.edu.arquitetura.newsdemo.core.data.LocalNewsRepository
import br.edu.arquitetura.newsdemo.core.data.RoomFavoritesRepository
import br.edu.arquitetura.newsdemo.core.domain.ObserveNewsWithFavoritesUseCase
import java.io.File
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoritesDatabaseTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private fun memory() = Room.inMemoryDatabaseBuilder(context, FavoritesDatabase::class.java).build()

    @Test fun insertAndRemoveAreIdempotent() = runBlocking<Unit> {
        val db = memory()
        try {
            val repo = RoomFavoritesRepository(db.favoriteDao())
            repo.setFavorite("01", true); repo.setFavorite("01", true)
            assertEquals(setOf("01"), repo.observeFavoriteIds().first())
            repo.setFavorite("01", false); repo.setFavorite("01", false)
            assertTrue(repo.observeFavoriteIds().first().isEmpty())
        } finally { db.close() }
    }
    @Test fun favoritesSurviveDatabaseReopening() = runBlocking<Unit> {
        val name = "persistence-test-${System.nanoTime()}.db"
        var db = Room.databaseBuilder(context, FavoritesDatabase::class.java, name).build()
        try {
            RoomFavoritesRepository(db.favoriteDao()).setFavorite("02", true)
            db.close()
            db = Room.databaseBuilder(context, FavoritesDatabase::class.java, name).build()
            assertEquals(setOf("02"), RoomFavoritesRepository(db.favoriteDao()).observeFavoriteIds().first())
        } finally { db.close(); context.deleteDatabase(name) }
    }
    @Test fun reactiveFlowUpdatesAfterWrites() = runBlocking<Unit> {
        val db = memory()
        try {
            val repo = RoomFavoritesRepository(db.favoriteDao())
            val channel = Channel<Set<String>>(Channel.UNLIMITED)
            val job = launch(Dispatchers.Default) { repo.observeFavoriteIds().collect { channel.send(it) } }
            assertTrue(withTimeout(5000) { channel.receive() }.isEmpty())
            repo.setFavorite("01", true)
            assertEquals(setOf("01"), withTimeout(5000) { channel.receive() })
            job.cancelAndJoin()
        } finally { db.close() }
    }
    @Test fun measuresTenFavoriteFlowLatencies() = runBlocking<Unit> {
        val db = memory()
        try {
            val repo = RoomFavoritesRepository(db.favoriteDao())
            val channel = Channel<Boolean>(Channel.UNLIMITED)
            val observe = ObserveNewsWithFavoritesUseCase(LocalNewsRepository(), repo)
            val job = launch(Dispatchers.Default) { observe().collect { channel.send(it.first().isFavorite) } }
            assertFalse(withTimeout(5000) { channel.receive() })
            val times = mutableListOf<Double>()
            repeat(10) { i ->
                val selected = i % 2 == 0
                val start = SystemClock.elapsedRealtimeNanos()
                repo.setFavorite("01", selected)
                val actual = withTimeout(5000) { channel.receive() }
                times += (SystemClock.elapsedRealtimeNanos() - start) / 1_000_000.0
                assertEquals(selected, actual)
            }
            job.cancelAndJoin()
            val json = "{\"favorite_latency_ms\":[" + times.joinToString(",") + "]}"
            File(context.filesDir, "favorite-latencies.json").writeText(json)
            Log.i("NewsDemoMetrics", json)
        } finally { db.close() }
    }
}
