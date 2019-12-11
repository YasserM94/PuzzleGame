package com.bau.puzzlegame.helper

import android.content.Context

class sharedPreferenses(context: Context?) {

    var context: Context

    init {
        this.context = context!!
    }


    //store user details locally
    fun setUsername(token: String?, avatar: String) {
        val sharedPreferences =
            context.getSharedPreferences("User_Name", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", token)
        editor.putString("userAvatar", avatar)
        editor.apply()
    }

    fun getUsername(): String? {
        val sharedPreferences =
            context.getSharedPreferences("User_Name", Context.MODE_PRIVATE)
        return sharedPreferences.getString("username", "Null")
    }

    fun getUserAvatar(): String? {
        val sharedPreferences =
            context.getSharedPreferences("User_Name", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userAvatar", "av1")
    }

    fun setUserLogin(state: Boolean) {
        val sharedPreferences =
            context.getSharedPreferences("User_login", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("loginState", state)
        editor.apply()
    }

    fun getUserLoginStatus(): Boolean? {
        val sharedPreferences =
            context.getSharedPreferences("User_login", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("loginState", false)
    }


}