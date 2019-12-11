package com.bau.puzzlegame.auth.register.avatarPicker

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.bau.puzzlegame.R
import com.bau.puzzlegame.helper.avatarIdSelected

class avatarPickerActivity(activity: avatarInterface, activity2: Activity) : Dialog(activity2),
    View.OnClickListener {


    lateinit var av1: ImageView
    lateinit var av2: ImageView
    lateinit var av3: ImageView
    lateinit var av4: ImageView
    lateinit var av5: ImageView
    lateinit var av6: ImageView
    var Fromactivity: avatarInterface

    init {
        Fromactivity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_avatar_picker)
        initActivity()
    }

    fun initActivity() {
        av1 = findViewById(R.id.av1)
        av2 = findViewById(R.id.av2)
        av3 = findViewById(R.id.av3)
        av4 = findViewById(R.id.av4)
        av5 = findViewById(R.id.av5)
        av6 = findViewById(R.id.av6)

        av1.setOnClickListener(this)
        av2.setOnClickListener(this)
        av3.setOnClickListener(this)
        av4.setOnClickListener(this)
        av5.setOnClickListener(this)
        av6.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {
        val avatarId = p0!!.id
        when (avatarId) {
            R.id.av1 -> saveAvatar("av1")
            R.id.av2 -> saveAvatar("av2")
            R.id.av3 -> saveAvatar("av3")
            R.id.av4 -> saveAvatar("av4")
            R.id.av5 -> saveAvatar("av5")
            R.id.av6 -> saveAvatar("av6")
            else -> {
                saveAvatar("av1")
            }
        }
    }

    fun saveAvatar(name: String) {
        avatarIdSelected = name
        Fromactivity.onAvatarSelected(name)
        //    Toast.makeText(context, "$id", Toast.LENGTH_SHORT).show()
        dismiss()
    }


}
