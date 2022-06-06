package top.yudoge.vpad.toplevel

inline fun <reified T : Enum<*>> Array<T>.names() = map { it.name }