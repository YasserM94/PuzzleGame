package com.bau.puzzlegame.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import cc.cloudist.acplibrary.ACProgressFlower
import com.bau.puzzlegame.R
import com.bau.puzzlegame.auth.register.avatarPicker.avatarInterface
import com.bau.puzzlegame.auth.register.avatarPicker.avatarPickerActivity
import com.bau.puzzlegame.helper.MyProgressDialog
import com.bau.puzzlegame.helper.avatarIdSelected
import com.bau.puzzlegame.helper.avatarPicker
import com.bau.puzzlegame.helper.sharedPreferenses
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment(), avatarInterface {

    //region Variables
    lateinit var root: View
    lateinit var avatar: ImageView
    lateinit var username: EditText
    lateinit var phoneNumber: EditText
    lateinit var score: EditText
    lateinit var newPassword: EditText
    lateinit var repeatPassword: EditText
    lateinit var newPasswordTv: TextView
    lateinit var pickAvatar: TextView
    lateinit var updateBtn: Button
    private var Progressdialog: ACProgressFlower? = null
    lateinit var database: FirebaseDatabase
    lateinit var myRef: DatabaseReference
    lateinit var mAuth: FirebaseAuth
    lateinit var usernameValue: String
    lateinit var phoneNumberValue: String
    lateinit var scoreValue: String
    lateinit var avatarValue: String
    lateinit var passwordValue: String
    lateinit var newPasswordValue: String
    lateinit var repeatPasswordValue: String
    private var originalMode: Int? = null
    private var editeEnabled = false
    private var localAvatar = false
    internal var avatarDialog: avatarPickerActivity? = null

    //endregion

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_profile, container, false)

        enableScrollWithKeybourd()
        initFragment()
        onClickListner()
        return root
    }

    fun enableScrollWithKeybourd() {
        activity?.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )
    }

    fun initFragment() {
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference("users")
        Progressdialog = MyProgressDialog().getInstanc(this.requireActivity())

        avatar = root.findViewById(R.id.profile_img_avatar)
        username = root.findViewById(R.id.profile_edt_username)
        phoneNumber = root.findViewById(R.id.profile_edt_phone)
        score = root.findViewById(R.id.profile_edt_score)
        newPassword = root.findViewById(R.id.profile_edt_newPassword)
        repeatPassword = root.findViewById(R.id.profile_edt_repeatPassword)
        updateBtn = root.findViewById(R.id.profile_btn_update)
        newPasswordTv = root.findViewById(R.id.profile_tv_newPassword)
        pickAvatar = root.findViewById(R.id.profile_avatarUpdate)

        usernameValue = sharedPreferenses(activity).getUsername()!!

        getFirebaseData()
        setPasswordVisibale(false)

    }

    fun onClickListner() {
        updateBtn.setOnClickListener {
            if (editeEnabled) {
                update()
                setPasswordVisibale(false)
                updateBtn.text = "Update Profile"
                editeEnabled = false

            } else {
                setPasswordVisibale(true)
                updateBtn.text = "Done"
                editeEnabled = true
            }

        }

        avatar.setOnClickListener {
            if (editeEnabled)
                showAvatarPicker()
        }
    }

    //update profile info
    fun update() {
        getvalues()

        if (newPasswordValue == repeatPasswordValue) {
            Progressdialog!!.show()
            sendInformationInFirebase()
        } else {
            Progressdialog!!.dismiss()
            Toast.makeText(this.requireContext(), "password dont match", Toast.LENGTH_SHORT).show()
        }
    }

    fun setValues() {
        Progressdialog!!.dismiss()
        if (localAvatar) {
            avatar.setImageDrawable(
                getDrawable(
                    this.context!!,
                    avatarPicker().instance.chooseAvatar(avatarValue)
                )
            )
        } else {
            Picasso.get().load(avatarValue).into(avatar)

        }

        username.setText(usernameValue)
        if (phoneNumberValue == "Null") {
            phoneNumber.isVisible = false
        } else {
            phoneNumber.isVisible = true
            phoneNumber.setText(phoneNumberValue)
        }
        score.setText(scoreValue)
    }

    //get profile info
    fun getFirebaseData() {
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

                        phoneNumberValue =
                            snapshot.child(usernameValue).child("phone").value.toString()
                        scoreValue = snapshot.child(usernameValue).child("score").value.toString()
                        passwordValue =
                            snapshot.child(usernameValue).child("password").value.toString()
                        setValues()
                    } else {

                    }
                }
            })
        } else {
            Progressdialog!!.dismiss()
        }

    }

    fun getvalues() {
        avatarValue = avatarIdSelected
        phoneNumberValue = phoneNumber.text.toString()
        newPasswordValue = newPassword.text.toString()
        repeatPasswordValue = repeatPassword.text.toString()
    }


    private fun sendInformationInFirebase() {

        val newUser = myRef.child(usernameValue)
        newUser.child("avatar").setValue(avatarIdSelected)
        newUser.child("localAvatar").setValue(localAvatar)
        if (newPasswordValue != "") {
            newUser.child("password").setValue(newPasswordValue)
            newUser.child("phone").setValue(phoneNumberValue)
        }
        Progressdialog!!.dismiss()

    }

    //update password
    fun setPasswordVisibale(status: Boolean) {
        newPassword.isVisible = status
        repeatPassword.isVisible = status
        pickAvatar.isVisible = status
        newPasswordTv.isVisible = status

        username.isEnabled = false
        score.isEnabled = false
        phoneNumber.isEnabled = status
        pickAvatar.isVisible = status
        newPasswordTv.isVisible = status
    }

    fun showAvatarPicker() {

        avatarDialog = avatarPickerActivity(this, this.requireActivity())
        avatarDialog!!.show()
    }

    override fun onAvatarSelected(name: String) {
        avatar.setImageDrawable(
            getDrawable(
                this.context!!,
                avatarPicker().instance.chooseAvatar(name)
            )
        )
        localAvatar = true
    }


}