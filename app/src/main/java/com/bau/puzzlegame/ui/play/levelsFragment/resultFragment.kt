package com.bau.puzzlegame.ui.play.levelsFragment


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import cc.cloudist.acplibrary.ACProgressFlower
import com.bau.puzzlegame.R
import com.bau.puzzlegame.helper.MyProgressDialog
import com.bau.puzzlegame.helper.avatarPicker
import com.bau.puzzlegame.helper.changeFragmentInterface
import com.bau.puzzlegame.helper.sharedPreferenses
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

class resultFragment : Fragment() {

    companion object {
        fun newInstance(currentActivity: changeFragmentInterface): resultFragment {
            val newinsta = resultFragment()
            newinsta.interfaceMain = currentActivity
            return newinsta
        }
    }

    //region Variables
    lateinit var root: View
    lateinit var interfaceMain: changeFragmentInterface
    lateinit var image: ImageView
    lateinit var name: TextView
    lateinit var score: TextView
    lateinit var result_ok: Button
    lateinit var viewKonfetti: KonfettiView

    lateinit var usernameValue: String
    var Progressdialog: ACProgressFlower? = null
    lateinit var database: FirebaseDatabase
    lateinit var myRef: DatabaseReference
    lateinit var mAuth: FirebaseAuth


    var localAvatar: Boolean = false
    lateinit var avatarValue: String
    lateinit var scoreValue: String

    //endregion

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_result, container, false)
        initFragment()
        onClickListner()
        return root
    }

    fun initFragment() {
        viewKonfetti = root.findViewById(R.id.result_viewKonfetti)
        image = root.findViewById(R.id.result_img_avatar)
        name = root.findViewById(R.id.result_name)
        score = root.findViewById(R.id.result_score)
        result_ok = root.findViewById(R.id.result_ok)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference("users")
        Progressdialog = MyProgressDialog().getInstanc(this.requireActivity())
        usernameValue = sharedPreferenses(activity).getUsername()!!
        getResultsFromFirebase()
    }

    fun onClickListner() {
        result_ok.setOnClickListener {
            getActivity()!!.finish()
        }
    }

    //get result user score from firebase database realtime
    fun getResultsFromFirebase() {
        if (usernameValue != "Null") {
            Progressdialog!!.show()
            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild(usernameValue)) { // run some code
                        localAvatar =
                            snapshot.child(usernameValue).child("localAvatar").value as Boolean


                        avatarValue = snapshot.child(usernameValue).child("avatar").value.toString()


                        scoreValue = snapshot.child(usernameValue).child("score").value.toString()

                        setValues()
                    } else {
                        Progressdialog!!.dismiss()
                    }
                }
            })
        } else {
            Progressdialog!!.dismiss()
        }
    }

    //setup congratulation animation for result fragment
    fun setupAnimation() {
        viewKonfetti.build()
            .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.RED, Color.BLUE)
            .setDirection(0.0, 359.0)
            .setSpeed(1f, 5f)
            .setFadeOutEnabled(true)
            .setTimeToLive(2000L)
            .addShapes(Shape.RECT, Shape.CIRCLE)
            .addSizes(Size(12))
            .setPosition(-50f, viewKonfetti.width + 50f, -50f, -50f)
            .streamFor(300, 5000L)
    }

    //set user info
    fun setValues() {
        Progressdialog!!.dismiss()
        if (localAvatar) {
            image.setImageDrawable(
                AppCompatResources.getDrawable(
                    this.context!!,
                    avatarPicker().instance.chooseAvatar(avatarValue)
                )
            )
        } else {
            Picasso.get().load(avatarValue).into(image)

        }

        name.setText(usernameValue)

        score.setText(scoreValue)
        setupAnimation()
    }

}
