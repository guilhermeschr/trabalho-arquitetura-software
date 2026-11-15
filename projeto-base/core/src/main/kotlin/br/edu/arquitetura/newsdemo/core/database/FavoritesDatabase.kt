package br.edu.arquitetura.newsdemo.core.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "favorites")
internal data class FavoriteEntity(@PrimaryKey val newsId: String)

@Dao
internal interface FavoriteDao {
    @Query("SELECT newsId FROM favorites ORDER BY newsId")
    fun observeIds(): Flow<List<String>>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favorite: FavoriteEntity)
    @Query("DELETE FROM favorites WHERE newsId = :id")
    suspend fun delete(id: String)
}

@Database(entities = [FavoriteEntity::class], version = 1, exportSchema = true)
internal abstract class FavoritesDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}
