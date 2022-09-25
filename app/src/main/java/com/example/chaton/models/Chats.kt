package com.example.chaton.models

data class Chats(
    val messageText: String = "",
    val sentTime: String = "",
    val phoneNumber: String = "",
    val isSender: Boolean = false
)
