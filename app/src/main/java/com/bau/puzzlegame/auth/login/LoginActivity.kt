package com.bau.puzzlegame.auth.login

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cc.cloudist.acplibrary.ACProgressFlower
import com.bau.puzzlegame.MainActivity
import com.bau.puzzlegame.R
import com.bau.puzzlegame.auth.register.RegisterActivity
import com.bau.puzzlegame.helper.CustomAlertDailog
import com.bau.puzzlegame.helper.MyProgressDialog
import com.bau.puzzlegame.helper.sharedPreferenses
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    //region Variables

    lateinit var username: EditText
    lateinit var password: EditText
    lateinit var loginBtn: Button
    lateinit var newAccount: TextView
    var usernameValue: String = ""
    var passwordValue: String = ""
    var Progressdialog: ACProgressFlower? = null
    lateinit var database: FirebaseDatabase
    lateinit var myRef: DatabaseReference
    lateinit var mAuth: FirebaseAuth

    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initActivty()
        onClickListener()
    }


    override fun onStart() {
        super.onStart()
        if (sharedPreferenses(this).getUserLoginStatus()!!) {
            goToMainActivity()
        }
    }

    //region Methods

    //initial activity variables
    fun initActivty() {
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference("users")
        Progressdialog = MyProgressDialog().getInstanc(this)
        username = findViewById(R.id.edt_username_login)
        password = findViewById(R.id.edt_password_login)
        loginBtn = findViewById(R.id.btn_login_login)
        newAccount = findViewById(R.id.tv_newAccount_login)


    }

    //set on click listener for attributes
    fun onClickListener() {

        loginBtn.setOnClickListener {
            if (checkUserInput()) {
                Progressdialog!!.show()
                login()
            }
        }
        newAccount.setOnClickListener {
            GoToRegister()
        }
    }

    //authentication functions
    fun login() {

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                var y = 0
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(usernameValue)) { // run some code
                    val accountPassword = snapshot.child(usernameValue).child("password").value
                    if (accountPassword == passwordValue) {
                        sharedPreferenses(this@LoginActivity).setUserLogin(true)
                        sharedPreferenses(this@LoginActivity).setUsername(usernameValue, "av2")
                        Progressdialog!!.dismiss()
                        goToMainActivity()
                    } else {
                        Progressdialog!!.dismiss()
                        sharedPreferenses(this@LoginActivity).setUserLogin(false)
                        CustomAlertDailog().instance.showElegantDialogWithOneButtonWithoutListner(
                            this@LoginActivity,
                            "Error",
                            "password is not correct"
                        )
                    }
                    return
                } else {
                    Progressdialog!!.dismiss()
                    sharedPreferenses(this@LoginActivity).setUserLogin(false)
                    CustomAlertDailog().instance.showElegantDialogWithOneButtonWithoutListner(
                        this@LoginActivity,
                        "Error",
                        "you dont have any account yet"
                    )
                }
            }
        })
    }

    fun checkUserInput(): Boolean {
        usernameValue = username.text.toString()
        passwordValue = password.text.toString()

        // Reset errors.
        username.setError(null)
        password.setError(null)

        var cancel = false
        var focusView: View? = null

        if (usernameValue == "") {
            username.setError("enter username")
            cancel = true
            focusView = username
        }
        if (passwordValue == "") {
            password.setError("enter password")
            cancel = true
            focusView = password
        }


        if (cancel) {
            focusView!!.requestFocus()
            return false
        } else {
            return true
        }
    }

    // transitions
    fun GoToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    fun goToMainActivity() {

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
//endregion
}
