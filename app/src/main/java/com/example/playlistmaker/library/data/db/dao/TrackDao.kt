package com.example.playlistmaker.library.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.library.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert( onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewFavorite(trackEntity: TrackEntity)


    @Query("SELECT * FROM track_table ORDER BY additionDate DESC")
    suspend fun getTracks(): List<TrackEntity>


    @Query("SELECT * FROM track_table WHERE trackId = :id")
    suspend fun getFavoriteId(id: Long): TrackEntity?


    @Delete(entity = TrackEntity::class)
    suspend fun deleteFromFavorites(trackId: TrackEntity)


}

