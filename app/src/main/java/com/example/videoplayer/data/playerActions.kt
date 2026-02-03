package com.example.videoplayer.data

data class playerActions(
    val actionType: ActionType,
    val data: Any? =  null,
)

enum class ActionType {
    PLAY, PAUSE, REWIND, FORWARD, PREVIOUS, NEXT
}
