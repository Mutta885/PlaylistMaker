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
    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrack(track: TrackEntity)

    @Query("SELECT * FROM track_table WHERE isFavorite = 1 AND trackId = :id")
    suspend fun checkIfTrackIsFavorite(id: Int): TrackEntity?

    @Query("SELECT * FROM track_table  WHERE isFavorite = 1 ORDER BY timestamp DESC")
    suspend fun getFavoriteTracks(): List<TrackEntity>

    @Query("SELECT * FROM track_table WHERE trackId IN (:ids)")
    suspend fun getTracksByIds(ids: List<Int>): List<TrackEntity>

    @Query("DELETE FROM track_table WHERE trackId = :id")
    suspend fun deleteTracksById(id: Int)


}