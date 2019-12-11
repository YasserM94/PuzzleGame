package com.bau.puzzlegame.helper

class Validation {
    var instance: Validation

    init {
        instance = this
    }

    //phone number validation
    fun phoneValidate(phone: String): Boolean {
        return android.util.Patterns.PHONE.matcher(phone).matches()
    }


}