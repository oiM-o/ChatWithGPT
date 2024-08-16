package com.example.chatwithgpt.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rooms")
data class RoomEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val createdAt: Long
)
