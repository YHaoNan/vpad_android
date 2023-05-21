package top.yudoge.vpad.viewmodel

import top.yudoge.vpad.R
import top.yudoge.vpad.pojo.ButtonLabel
import top.yudoge.vpad.toplevel.Constants

class ButtonGroupViewModel {
    val buttonGroupLabels = arrayListOf(
        ButtonLabel("PLAY","" , R.color.darkblue, R.color.blue),
        ButtonLabel("REC", "", R.color.darkred, R.color.red),
        ButtonLabel("STOP", "", R.color.darkgreen, R.color.green),
        ButtonLabel("LOOP", "", R.color.darkyellow, R.color.yellow),
        ButtonLabel("UNDO", labelColor = R.color.grey),
        ButtonLabel("REDO", labelColor = R.color.grey),
        ButtonLabel("CLK", labelColor = R.color.grey),
        ButtonLabel("SAVE", labelColor = R.color.grey),
        ButtonLabel("BPM", Constants.DEFAULT_BPM.toString()),
        ButtonLabel("PDST"),
        ButtonLabel("Rgn-"),
        ButtonLabel("Rgn+"),
        ButtonLabel("Pst"),
        ButtonLabel("Exp"),
        ButtonLabel("Set"),
        ButtonLabel("Tog"),
    )

}