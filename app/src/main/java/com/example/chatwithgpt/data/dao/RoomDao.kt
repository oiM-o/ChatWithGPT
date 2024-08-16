package com.example.chatwithgpt.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import com.example.chatwithgpt.data.entity.RoomEntity

@Dao
interface RoomDao {
    @Insert
    suspend fun insertRoom(room: RoomEntity)

    @Delete
    suspend fun deleteRoom(room: RoomEntity)
}
