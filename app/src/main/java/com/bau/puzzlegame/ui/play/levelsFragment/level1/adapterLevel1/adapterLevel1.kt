package com.bau.puzzlegame.ui.play.levelsFragment.level1.adapterLevel1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bau.puzzlegame.R
import com.bau.puzzlegame.model.PuzzleModel
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso


class adapterLevel1(private val list: Array<PuzzleModel>?) :
    RecyclerView.Adapter<adapterLevel1.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item: PuzzleModel = list!!.get(position)
        holder.bind(item)
    }

    override fun getItemCount(): Int = list!!.size

    class MyViewHolder(inflater: LayoutInflater, parent: ViewGroup) :

        RecyclerView.ViewHolder(inflater.inflate(R.layout.puzzle_card, parent, false)) {

        private var image: ImageView? = null
        private var view: RelativeLayout? = null


        val mstorage = FirebaseStorage.getInstance().getReference("images/puzzleImages")


        init {
            image = itemView.findViewById(R.id.puzzle_card_image)
            view = itemView.findViewById(R.id.puzzle_card_view)

        }

        fun bind(item: PuzzleModel) {

            mstorage.child(item.urlImage).downloadUrl
                .addOnSuccessListener {
                    // Got the download URL for 'users/me/profile.png'
                    Picasso.get().load(it).into(image)
                }.addOnFailureListener {
                    // Handle any errors
                }

            view!!.isVisible = item.visible
            image!!.alpha = 0f


        }

    }


}