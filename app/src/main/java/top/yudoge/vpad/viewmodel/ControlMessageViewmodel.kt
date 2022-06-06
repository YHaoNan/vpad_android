package top.yudoge.vpad.viewmodel

import top.yudoge.vpad.api.ControlOperations
import top.yudoge.vpadapi.Operations
import top.yudoge.vpadapi.structure.ControlMessage
import javax.inject.Inject

class ControlMessageViewmodel @Inject constructor(){
    val playMessage = ControlMessage(ControlOperations.OP_PLAY, ControlMessage.STATE_ON, ControlMessage.AUTO_CLOSE_ON)
    val stopMessage = ControlMessage(ControlOperations.OP_STOP, ControlMessage.STATE_ON, ControlMessage.AUTO_CLOSE_ON)
    val recordMessage = ControlMessage(ControlOperations.OP_RECORD, ControlMessage.STATE_ON, ControlMessage.AUTO_CLOSE_ON)
    val loopMessage = ControlMessage(ControlOperations.OP_LOOP, ControlMessage.STATE_ON, ControlMessage.AUTO_CLOSE_ON)
    val undoMessage = ControlMessage(ControlOperations.OP_UNDO, ControlMessage.STATE_ON, ControlMessage.AUTO_CLOSE_ON)
    val redoMessage = ControlMessage(ControlOperations.OP_REDO, ControlMessage.STATE_ON, ControlMessage.AUTO_CLOSE_ON)
    val clickMessage = ControlMessage(ControlOperations.OP_CLICK, ControlMessage.STATE_ON, ControlMessage.AUTO_CLOSE_ON)
    val saveMessage = ControlMessage(ControlOperations.OP_SAVE, ControlMessage.STATE_ON, ControlMessage.AUTO_CLOSE_ON)
    val bankLeftMessage = ControlMessage(ControlOperations.OP_TRACK_BANK_LEFT, ControlMessage.STATE_ON, ControlMessage.AUTO_CLOSE_ON)
    val bankRightMessage = ControlMessage(ControlOperations.OP_TRACK_BANK_RIGHT, ControlMessage.STATE_ON, ControlMessage.AUTO_CLOSE_ON)
}