package com.example.chatwithgpt.ui.screen.talk

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TalkScreen(
        talkViewModel: TalkViewModel = viewModel()
) {

    val messages by talkViewModel.messages.collectAsState()
    var inputText by remember { mutableStateOf("") }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    //Roomを導入して新規トークルームを作れるようになったら変更するダミーデータ
    val talkRooms = listOf("Room 1", "Room 2", "Room 3", "Room 4")

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(260.dp)
            ) {
                Text(
                    "Other Talk Room",
                    modifier = Modifier.padding(16.dp),
                    style = TextStyle(fontSize = 24.sp),
                    fontWeight = FontWeight.Bold
                    )
                Divider()
                LazyColumn() {
                    items(talkRooms) {room ->
                        NavigationDrawerItem(
                            label = {
                                Text(
                                    text = room,
                                    style = TextStyle(fontSize = 16.sp)
                                )
                            },
                            selected = false,
                            onClick = { /*TODO*/ }
                        )
                    }
                }
                // ...other drawer items
            }
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "TalkRoom",
                            style = TextStyle(fontSize = 24.sp),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.apply {
                                        if (isClosed) open() else close()
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "SettingDrawer"
                            )
                        }
                    }
                )
            },
            bottomBar = {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { newText -> inputText = newText },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                talkViewModel.sendMessageToGpt(inputText)
                                inputText = "" // 送信後にテキストフィールドをクリア
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "send_message_to_GPT"
                            )
                        }
                    },
                    label = { Text(text = "Type your message...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        talkViewModel.createRoom("New Room")
                     }
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "New_Talk")
                }
            }
        ) { innerpadding ->
            LazyColumn(
                contentPadding = innerpadding,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(messages) { message ->
                    Text(text = message.first)
                }
            }

        }
    }
}

@Preview
@Composable
fun TalkScreenPreview(){
    TalkScreen()
}