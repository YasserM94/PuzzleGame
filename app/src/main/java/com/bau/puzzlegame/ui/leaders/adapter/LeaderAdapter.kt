package com.bau.puzzlegame.ui.leaders.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bau.puzzlegame.R
import com.bau.puzzlegame.helper.avatarPicker
import com.bau.puzzlegame.helper.sharedPreferenses
import com.bau.puzzlegame.model.userModel
import kotlinx.android.synthetic.main.leaders_card.view.*

class LeaderAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    fun bindLeader(user: userModel?) {
        with(user!!) {
            itemView.card_tv_name.text = user.username
            if (sharedPreferenses(itemView.context).getUsername() == user.username) {
                itemView.leader_card.background = ContextCompat.getDrawable(
                    itemView.context,
                    R.drawable.shape_leader_card_background
                )
                //itemView.leader_card.setBackgroundColor(R.color.colorAccent)
            }
            itemView.card_tv_score.text = "" + user.score
            itemView.card_img_avatar.setImageDrawable(
                AppCompatResources.getDrawable(
                    itemView.context,
                    avatarPicker().instance.chooseAvatar(user.avatar!!)
                )
            )

        }
    }
}