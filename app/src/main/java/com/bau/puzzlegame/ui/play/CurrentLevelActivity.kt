package com.bau.puzzlegame.ui.play

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.bau.puzzlegame.R
import com.bau.puzzlegame.helper.changeFragmentInterface
import com.bau.puzzlegame.ui.play.levelsFragment.level1.Level1Fragment
import com.bau.puzzlegame.ui.play.levelsFragment.level2.Level2Fragment
import com.bau.puzzlegame.ui.play.levelsFragment.level3.Level3Fragment
import com.bau.puzzlegame.ui.play.levelsFragment.level4.Level4Fragment
import com.bau.puzzlegame.ui.play.levelsFragment.resultFragment


class CurrentLevelActivity : AppCompatActivity(), changeFragmentInterface {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_level)
        iniActivity()
    }

    fun iniActivity() {
        var LevelId: Int = intent.getIntExtra("level", 1)
        loadFragment(LevelId)
    }

    fun loadFragment(id: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.currentLevel, selectedLevel(id))
        transaction.commit()

    }

    //levels fragments transitions
    fun selectedLevel(id: Int): Fragment {
        return when (id) {
            1 -> Level1Fragment.newInstance(this)
            2 -> Level2Fragment.newInstance(this)
            3 -> Level3Fragment.newInstance(this)
            4 -> Level4Fragment.newInstance(this)
            5 -> resultFragment.newInstance(this)
            else -> {
                Level1Fragment.newInstance(this)
            }
        }
    }

    override fun onChangeRequest(id: Int) {
        loadFragment(id)
    }
}
