package com.example.chatwithgpt.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.chatwithgpt.data.entity.RoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomDao {
    @Insert
    suspend fun insertRoom(room: RoomEntity)

    @Delete
    suspend fun deleteRoom(room: RoomEntity)

    @Query("SELECT * FROM rooms ORDER BY createdAt DESC")
    fun getAllRooms(): Flow<List<RoomEntity>>

    @Query("SELECT COUNT(*) FROM rooms")
    suspend fun getRoomCount(): Int
}
