package top.yudoge.vpad.toplevel

/**
 * TriggerMode规定了打击垫按钮的触发模式
 */
enum class TriggerMode {
    // 按下发送On，抬起发送Off
    Trigger,
    // 按下发送On，再次按下发送Off
    Hold
}