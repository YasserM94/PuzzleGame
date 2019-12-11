package com.bau.puzzlegame.helper

import android.app.Activity
import android.graphics.Color
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower


class MyProgressDialog() {

    //custom loading indecator
    fun getInstanc(activity: Activity?): ACProgressFlower? {
        val Progressdialog = ACProgressFlower.Builder(activity)
            .direction(ACProgressConstant.DIRECT_CLOCKWISE)
            .themeColor(Color.WHITE)
            .fadeColor(Color.DKGRAY).build()
        Progressdialog.setCancelable(false)
        return Progressdialog
    }
}