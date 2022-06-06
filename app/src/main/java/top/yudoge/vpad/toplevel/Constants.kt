package top.yudoge.vpad.toplevel

import top.yudoge.vpad.pojo.Fader
import top.yudoge.vpad.pojo.FaderMode

object Constants {
    const val DB_NAME = "vpad_db"
    const val DEFAULT_PAD_SETTING_FILE_NAME = "default_pad_setting.json"
    const val DEFAULT_BPM: Int = 130

    val DEFAULT_TRACK_FADERS = lazy {
        listOf(
            Fader(0, 100, FaderMode.Track, 1),
            Fader(1, 100, FaderMode.Track, 2),
            Fader(2, 100, FaderMode.Track, 3),
            Fader(3, 100, FaderMode.Track, 4),
            Fader(4, 100, FaderMode.Track, 5),
            Fader(5, 100, FaderMode.Track, 6),
            Fader(6, 100, FaderMode.Track, 7),
            Fader(7, 100, FaderMode.Track, 8),
        )
    }

    val DEFAULT_CC_FADERS = lazy {
        listOf(
            Fader(0, 101, FaderMode.CC, 1),
            Fader(1, 101, FaderMode.CC, 7),
            Fader(2, 101, FaderMode.CC, 10),
            Fader(3, 101, FaderMode.CC, 11),
            Fader(4, 101, FaderMode.CC, 64),
            Fader(5, 101, FaderMode.CC, 65),
            Fader(6, 101, FaderMode.CC, 3),
            Fader(7, 101, FaderMode.CC, 9),
        )
    }


    const val CRASH_LOG_NAME = "crash.log"
}