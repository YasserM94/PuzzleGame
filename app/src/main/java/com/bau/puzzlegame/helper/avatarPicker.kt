package com.bau.puzzlegame.helper

import com.bau.puzzlegame.R
import java.util.*

class avatarPicker {

    var instance: avatarPicker

    init {
        instance = this
    }

    //avatar selection
    fun chooseAvatar(name: String): Int {
        when (name) {
            "av1" -> return (R.drawable.av1)
            "av2" -> return (R.drawable.av2)
            "av3" -> return (R.drawable.av3)
            "av4" -> return (R.drawable.av4)
            "av5" -> return (R.drawable.av5)
            "av6" -> return (R.drawable.av6)
            else -> {
                return (R.drawable.av0)
            }

        }
    }


}