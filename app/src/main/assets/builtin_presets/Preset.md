preset文件中包含四个字段：

- baseNote: 当前基准音符，每一个pad代表的音符是它的offset + baseNote
- regionSpan: 用户调用Rgn+和Rgn-时的音符跨度
- padsPerLine: 一行的pad个数
- padSettings: 每一个打击垫的具体设置。按照指定的列表顺序从上到下从左到右排列，每一行有padsPerLine个

通过这四个字段，用户可以高度定制打击垫的行为，比如下面的这个例子是一个传统打击垫的样式

```json
{
  "baseNote": 35,
  "regionSpan": 16,
  "padsPerLine": 4,
  "padSettings": [
    {
      "offset": 12,
      "title": "Pad12",
      "mode": "Pad",
      "velocity": 90
    },
    {
      "offset": 13,
      "title": "Pad13",
      "velocity": 90
    },
    {
      "offset": 14,
      "title": "Pad14",
      "mode": "Pad",
      "velocity": 90
    },
    {
      "offset": 15,
      "title": "Pad15",
      "mode": "Pad",
      "velocity": 90
    },
    {
      "offset": 8,
      "title": "Pad8",
      "mode": "Pad",
      "velocity": 90
    },
    {
      "offset": 9,
      "title": "Pad9",
      "mode": "Pad",
      "velocity": 90
    },
    {
      "offset": 10,
      "title": "Pad10",
      "mode": "Pad",
      "velocity": 90
    },
    {
      "offset": 11,
      "title": "Pad11",
      "mode": "Pad",
      "velocity": 90
    },
    {
      "offset": 4,
      "title": "Pad4",
      "mode": "Pad",
      "velocity": 90
    },
    {
      "offset": 5,
      "title": "Pad5",
      "mode": "Pad",
      "velocity": 90
    },
    {
      "offset": 6,
      "title": "Pad6",
      "mode": "Pad",
      "velocity": 90
    },
    {
      "offset": 7,
      "title": "Pad7",
      "mode": "Pad",
      "velocity": 90
    },
    {
      "offset": 0,
      "title": "Pad0",
      "mode": "Pad",
      "velocity": 90
    },
    {
      "offset": 1,
      "title": "Pad1",
      "mode": "Pad",
      "velocity": 90
    },
    {
      "offset": 2,
      "title": "Pad2",
      "mode": "Pad",
      "velocity": 90
    },
    {
      "offset": 3,
      "title": "Pad3",
      "mode": "Pad",
      "velocity": 90
    }
  ]
}
```

如下是一个只有三个按钮的，可以按出251和弦的预制
```json
{
  "baseNote": 72,
  "regionSpan": 1,
  "padsPerLine": 3,
  "padSettings": [
    {
      "offset": 2,
      "title": "2Min7",
      "mode": "Chord",
      "velocity": "90",
      "specificModeSetting": {
        "chordLevel": 1,
        "chordType": 1
      }
    },
    {
      "offset": 7,
      "title": "5Maj7",
      "mode": "Chord",
      "velocity": "90",
      "specificModeSetting": {
        "chordLevel": 1,
        "chordType": 0,
        "transpose": 1
      }
    },
    {
      "offset": 0,
      "title": "1Maj7",
      "mode": "Chord",
      "velocity": "90",
      "specificModeSetting": {
        "chordLevel": 1,
        "chordType": 0
      }
    }
  ]
}
```