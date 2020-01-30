package com.ninovitale.moviedb.app.data

import androidx.paging.DataSource
import androidx.room.*
import io.reactivex.Maybe

@Database(entities = [MovieDbItem::class, ConfigurationDbItem::class], version = 1)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDAO(): MovieDao
    abstract fun configDAO(): ConfigurationDao
}

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies")
    fun loadAll(): List<MovieDbItem>

    @Query("SELECT * FROM movies")
    fun loadPage(): DataSource.Factory<Int, MovieDbItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(movies: List<MovieDbItem>)

    @Query("DELETE FROM movies")
    fun clear()
}

@Entity(tableName = "movies")
data class MovieDbItem(
    @PrimaryKey val id: Int?,
    val originalTitle: String?,
    val overview: String?,
    val posterPath: String?,
    val releaseDate: String?,
    val voteAverage: Double?
)

@Dao
interface ConfigurationDao {
    @Query("SELECT * FROM configuration")
    fun load(): Maybe<ConfigurationDbItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(configurationDbItem: ConfigurationDbItem)

    @Query("DELETE FROM configuration")
    fun clear()
}

@Entity(tableName = "configuration")
data class ConfigurationDbItem(
    @PrimaryKey val imageBaseUrl: String,
    val imageSize: String?,
    val timestamp: Long = System.currentTimeMillis()
)