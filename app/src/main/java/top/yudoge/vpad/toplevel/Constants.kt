package top.yudoge.vpad.toplevel

import top.yudoge.vpad.pojo.Fader
import top.yudoge.vpad.pojo.FaderMode

object Constants {
    val DEFAULT_SHOW_NOTE_NAME: Int = 0
    const val DB_NAME = "vpad.db"
    const val DEFAULT_PRESET_FILE_NAME = "builtin_presets/3 x 3 Pad.preset.json"
    val BUILTIN_PRESET_FILE_NAMES = listOf<String>(
        "builtin_presets/4 x 4 Pad.preset.json",
        "builtin_presets/6 x 6 Pad.preset.json",
        "builtin_presets/3 x 3 Pad.preset.json",
        "builtin_presets/2-5-1 Chord.preset.json",
        "builtin_presets/Lofi Chord.preset.json"
    )
    const val DEFAULT_BPM: Int = 130
    const val DEFAULT_BASE: Int = 35

    val DEFAULT_TRACK_FADERS = lazy {
        listOf(
            Fader(0, 97, FaderMode.Track, 1),
            Fader(1, 97, FaderMode.Track, 2),
            Fader(2, 97, FaderMode.Track, 3),
            Fader(3, 97, FaderMode.Track, 4),
            Fader(4, 97, FaderMode.Track, 5),
            Fader(5, 97, FaderMode.Track, 6),
            Fader(6, 97, FaderMode.Track, 7),
            Fader(7, 97, FaderMode.Track, 8),
        )
    }

    val DEFAULT_CC_FADERS = lazy {
        listOf(
            Fader(0, 97, FaderMode.CC, 1),
            Fader(1, 97, FaderMode.CC, 7),
            Fader(2, 97, FaderMode.CC, 10),
            Fader(3, 97, FaderMode.CC, 11),
            Fader(4, 97, FaderMode.CC, 64),
            Fader(5, 97, FaderMode.CC, 65),
            Fader(6, 97, FaderMode.CC, 3),
            Fader(7, 97, FaderMode.CC, 9),
        )
    }


    const val CRASH_LOG_NAME = "crash.log"
    const val PRESET_DIR = "presets"
}