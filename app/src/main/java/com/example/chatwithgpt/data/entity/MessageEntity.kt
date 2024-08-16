package com.example.chatwithgpt.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val roomId: Int,
    val message: String,
    val isUser: Boolean,
    val timeStamp: Long

)
