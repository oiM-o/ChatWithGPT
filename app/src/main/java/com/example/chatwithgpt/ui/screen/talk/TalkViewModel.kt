package com.example.chatwithgpt.ui.screen.talk

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatwithgpt.BuildConfig
import com.example.chatwithgpt.data.database.AppDatabase
import com.example.chatwithgpt.data.entity.MessageEntity
import com.example.chatwithgpt.data.entity.RoomEntity
import com.example.chatwithgpt.model.GptMessage
import com.example.chatwithgpt.model.GptRequest
import com.example.chatwithgpt.network.OpenAIApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData


class TalkViewModel(application: Application) : AndroidViewModel(application) {
    private val _messages = MutableStateFlow<List<Pair<String, Boolean>>>(emptyList())
    val messages: StateFlow<List<Pair<String, Boolean>>> = _messages

    private val BASE_URL =
        "https://api.openai.com/"

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    private val openAIApi = retrofit.create(OpenAIApiService::class.java)

    // BuildConfig 経由で API キーを取得
    private val apiKey = BuildConfig.OPENAI_API_KEY

    private val db = AppDatabase.getDatabase(application)
    private val roomDao = db.roomDao()
    private val messageDao = db.messageDao()

    private val _currentRoom = MutableStateFlow<RoomEntity?>(null)
    val currentRoom: StateFlow<RoomEntity?> = _currentRoom

    // トークルームリストを取得するLiveData
    val talkRooms: LiveData<List<RoomEntity>> = roomDao.getAllRooms().asLiveData()

    fun addMessage(message: String, isUser: Boolean){
        _messages.value = _messages.value + (message to isUser)
    }

    fun sendMessageToGpt(inputText: String){
        addMessage(inputText, isUser = true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = GptRequest(
                    messages = listOf(
                        GptMessage("user", inputText)
                    )
                )

                // APIキーを"Bearer "と一緒にAuthorizationヘッダーに渡す
                val authHeader = "Bearer $apiKey"
                val response = openAIApi.sendMessage(authHeader, request).awaitResponse()

                if (response.isSuccessful) {
                    val gptResponse = response.body()
                    val gptMessage = gptResponse?.choices?.firstOrNull()?.message?.content

                    gptMessage?.let {
                        addMessage(it, isUser = false)
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    addMessage("Error: ${response.code()} - $errorBody", isUser = false)
                }
            } catch (e: Exception) {
                addMessage("Failed to communicate with GPT: ${e.message}", isUser = false)
            }
        }
    }

    fun createRoom() {
        viewModelScope.launch {
            val currentRoomCount = roomDao.getRoomCount()
            val newRoomName = "New Room ${ currentRoomCount + 1 } "
            val room = RoomEntity(name = newRoomName, createdAt = System.currentTimeMillis())

            roomDao.insertRoom(room)
            _currentRoom.value = room
        }
    }

    fun switchRoom(room: RoomEntity) {
        _currentRoom.value = room
    }

    fun deleteRoom(room: RoomEntity){
        viewModelScope.launch {
            roomDao.deleteRoom(room)
        }
    }

    fun saveMessage(roomId: Int, message: String, isUser: Boolean) {
        viewModelScope.launch {
            val msg = MessageEntity(
                roomId = roomId,
                message = message,
                isUser = isUser,
                timeStamp = System.currentTimeMillis()
            )
            messageDao.insertMessage(msg)
        }
    }
}