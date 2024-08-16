package com.example.chatwithgpt.data.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.chatwithgpt.data.entity.MessageEntity

@Dao
interface MessageDao {
    @Insert
    suspend fun insertMessage(message: MessageEntity)
}
