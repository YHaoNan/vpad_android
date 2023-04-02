# 关于VPad Android端Preset的设计

VPad Preset是一个用于配置VPad中打击垫按钮的JSON文件，它可以指定如下内容：
```
{
  "presetName": "Preset名称",
  "author": "Preset作者",
  "description": "Preset描述",
  "baseNote": 该Preset所在的基音符,
  "regionSpan": 该Preset的跨区范围,
  "padsPerLine": 每一行的pad数,
  "padSettings": [
    详细的per-pad设置
  ]
}
```

## baseNote
当你使用一个实体打击垫时，一般情况下，最左下角的pad的音符是MIDI标准中编号为34（也许大概是吧我记不太清了）的音符。这没有什么道理可言，只是约定俗成。 但对于用于弹奏的preset，比如2-5-1和弦的preset，34这个音显得太低了。

`baseNote`的含义就是该preset的基本音符，每一个pad有一个`offset`——偏移量，一个pad触发后最终发给服务端的音符，实际上就是当前的`baseNote`+按下的pad的`offset`。

举个例子来说，当你想要提供一个传统的4x4的打击垫预设时，你可以将`baseNote`设置为`34`，并将`padSettings`中最左下角的pad的`offset`设置为`0`。而当你想要提供一个2-5-1和弦的preset，你可能希望`baseNote`为音符C5代表的编号。

## regionSpan
无论是打击垫还是MIDI键盘，除非它能提供完整的127个音符（或者说传统钢琴上的所有按键），否则它都会提供上下移动的范围。

比如AKAI MPD218是一个4x4的打击垫，这些打击垫能覆盖16个音符，但它提供了一个按钮可以将打击垫覆盖的区域向上16个音符以及32个音符，这样，它就覆盖了3组16个音符，这让打击垫能做的事大大提升。

VPad中提供了Rgn+和Rgn-两个按钮，用来改变当前打击垫覆盖的范围。说的明白一点，你点一次Rgn+，`baseNote+=regionSpan`，你点一次Rgn-，`baseNote-=regionSpan`。

对于4x4打击垫的预制，你可能希望`regionSpan`为16，因为你的屏幕上有16个pad，你希望每一次你点击Rgn+，pads们覆盖的音符范围向上移动16个，对于Rgn-则相反。

而对于2-5-1和弦，你可能希望`regionSpan`为1，因为你的屏幕上只有三个和弦模式的pad，分别代表`baseNote`为根音的大调内的251和弦，你希望每次点击Rgn+，移动到下一个调，比如从`CMajor`移动到`C#Major`。

## padsPerLine

一般的实体打击垫都是4x4的，也有33的或者如Launchpad、Ableton的那种很多按钮的。padsPerLine用于配置一行有多少个pad。

## padSettings
