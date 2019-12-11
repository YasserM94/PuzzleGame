package com.bau.puzzlegame.auth.register

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import cc.cloudist.acplibrary.ACProgressFlower
import com.bau.puzzlegame.MainActivity
import com.bau.puzzlegame.R
import com.bau.puzzlegame.auth.register.avatarPicker.avatarInterface
import com.bau.puzzlegame.auth.register.avatarPicker.avatarPickerActivity
import com.bau.puzzlegame.helper.*
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_register.*
import java.io.Console
import java.io.File
import java.util.concurrent.TimeUnit


class RegisterActivity : AppCompatActivity(), avatarInterface {

    //region Variables


    private lateinit var downloadUri: Uri
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var code: EditText
    private lateinit var registerBtn: Button
    private lateinit var submitCodeBtn: Button

    private lateinit var pickImage: Button
    private lateinit var pickAvatar: Button

    private lateinit var goToLogin: TextView
    private lateinit var avatarImage: ImageView
    private lateinit var info_layout: ConstraintLayout
    private lateinit var code_layout: LinearLayout
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private var usernameValue: String = ""
    private var passwordValue: String = ""
    private var phoneNumberdValue: String = "Null"
    private var codeValue: String = ""
    private var avatarlink: String = ""
    private var Progressdialog: ACProgressFlower? = null
    private var usedPhone: Boolean = false
    private var mStorageRef: StorageReference? = null
    private lateinit var filePath: String
    var localAvatar: Boolean = true


    lateinit var mAuth: FirebaseAuth
    lateinit var storedVerificationId: String
    lateinit var resendToken: String
    private var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Progressdialog!!.dismiss()
            CustomAlertDailog().instance.showElegantDialogWithTwoButtons(
                this@RegisterActivity,
                "Error",
                e.message.toString(),
                DialogInterface.OnClickListener({ dialog: DialogInterface, which: Int ->
                    {

                    }
                }),
                DialogInterface.OnClickListener({ dialog: DialogInterface, which: Int ->
                    {

                    }
                })
            )
            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                // ...
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // ...
            }

            // Show a message and update the UI
            // ...
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d("", "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            resendToken = token.toString()
            Progressdialog!!.dismiss()
            showInfoLayout(false)
        }
    }

    internal var avatarDialog: avatarPickerActivity? = null

    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initActivty()
        onClickListner()
    }

    //region Methods

    // initial activity function
    fun initActivty() {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()
        mStorageRef = FirebaseStorage.getInstance().reference
        Progressdialog = MyProgressDialog().getInstanc(this)
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference("users")
        username = findViewById(R.id.edt_username_register)
        password = findViewById(R.id.edt_password_register)
        phoneNumber = findViewById(R.id.edt_phone_register)

        pickImage = findViewById(R.id.register_btn_chooseImage)
        pickAvatar = findViewById(R.id.register_btn_avatarPicker)

        code = findViewById(R.id.edt_code_register)
        info_layout = findViewById(R.id.info_layout_register)
        code_layout = findViewById(R.id.code_layout_register)
        registerBtn = findViewById(R.id.btn_register_register)
        submitCodeBtn = findViewById(R.id.btn_submitCode_register)
        goToLogin = findViewById(R.id.tv_login_register)
        avatarImage = findViewById(R.id.img_avatar_register)

        showInfoLayout(true)


    }

    //set information register view is visible or code phone number view
    private fun showInfoLayout(status: Boolean) {
        if (status) {
            info_layout.isVisible = true
            code_layout.isVisible = false
        } else {
            info_layout.isVisible = false
            code_layout_register.isVisible = true
        }
    }

    //firebase auth
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("", "signInWithCredential:success")


                    uploadAvatar()
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w("", "signInWithCredential:failure", task.exception)
                    Progressdialog!!.dismiss()
                    CustomAlertDailog().instance.showElegantDialogWithOneButtonWithoutListner(
                        this,
                        "Error",
                        task.exception!!.message.toString()
                    )
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                }
            }
    }

    private fun registerInformationInFirebase() {
        val newUser = myRef.child(usernameValue)
        newUser.child("avatar").setValue(avatarlink)
        newUser.child("password").setValue(passwordValue)
        newUser.child("phone").setValue(phoneNumberdValue)
        newUser.child("username").setValue(usernameValue)

        newUser.child("localAvatar").setValue(localAvatar)

        newUser.child("score").setValue(0)
        sharedPreferenses(this).setUserLogin(true)
        sharedPreferenses(this).setUsername(usernameValue, avatarIdSelected)
        goToMainActivity()
    }

    fun onClickListner() {

        registerBtn.setOnClickListener {
            if (checkUserInput()) {
                Progressdialog!!.dismiss()
                if (usedPhone) {//send code to phone number
                    checkUserNameIfExist()
                } else { // send information to register in firebase
                    uploadAvatar()
                }
            }
        }


        submitCodeBtn.setOnClickListener {
            codeValue = code.text.toString()
            if (!codeValue.isEmpty()) {
                Progressdialog!!.show()
                checkCode(codeValue, storedVerificationId)
            }
        }

        goToLogin.setOnClickListener {
            GoToLogin()
        }

        pickAvatar.setOnClickListener {
            showAvatarPicker()
        }
        pickImage.setOnClickListener {
            showDialogImgPicker()
        }
    }

    //check username uniqe
    fun checkUserNameIfExist() {
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                var y = 0
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var x = 0
                if (snapshot.hasChild(usernameValue)) { // run some code
                    Progressdialog!!.dismiss()
                    Toast.makeText(this@RegisterActivity, "User already exist", Toast.LENGTH_SHORT)
                        .show()
                    return
                } else {
                    sendCodeToPhoneNumber()
                }
            }
        })
    }


    fun sendCodeToPhoneNumber() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumberdValue, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            callbacks
        ) // OnVerificationStateChangedCallbacks
    }

    //check received sms code
    fun checkCode(code: String, verificationId: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

    //check auth information
    fun checkUserInput(): Boolean {
        usernameValue = username.text.toString()
        passwordValue = password.text.toString()
        phoneNumberdValue = phoneNumber.text.toString()

        // Reset errors.
        username.error = null
        password.error = null
        phoneNumber.error = null

        var cancel = false
        var focusView: View? = null

        if (usernameValue == "") {
            username.error = "enter username"
            cancel = true
            focusView = username
        }
        if (passwordValue == "") {
            password.error = "enter password"
            cancel = true
            focusView = password
        }

        if (phoneNumberdValue != "") {
            usedPhone = true
            if (!Validation().instance.phoneValidate(phoneNumberdValue)) {
                phoneNumber.error = "enter phone number"
                cancel = true
                focusView = phoneNumber
            }
        } else {
            usedPhone = false
        }
        if (cancel) {
            focusView!!.requestFocus()
            return false
        } else {
            return true
        }
    }

    //set avatar
    fun showAvatarPicker() {

        avatarDialog = avatarPickerActivity(this, this)
        avatarDialog!!.show()
    }

    fun uploadAvatar() {
        if (localAvatar) {
            avatarlink = avatarIdSelected
            registerInformationInFirebase()
        } else {
            val file: Uri = Uri.fromFile(File(filePath))
//        val imageName = filePath.substring(filePath.lastIndexOf("/") + 1)
            val riversRef: StorageReference =
                mStorageRef!!.child("images/uploadedAvatars/$usernameValue.jpg")


            val urlTask = riversRef.putFile(file).continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                riversRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    downloadUri = task.result!!
                    avatarlink = downloadUri.toString()
                    registerInformationInFirebase()
                } else {


                }
            }

        }


    }

    fun showDialogImgPicker() {
        ImagePicker.with(this)
            .cropSquare()                    //Crop image(Optional), Check Customization for more option
            .compress(1024)            //Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            )    //Final image resolution will be less than 1080 x 1080(Optional)
            .start()
    }

    //transitions
    fun GoToLogin() {
        onBackPressed()
        finish()
    }

    private fun goToMainActivity() {
        Progressdialog!!.dismiss()
//        val user = mAuth.currentUser
//
//        val profileUpdates =
//            UserProfileChangeRequest.Builder().setDisplayName(usernameValue).build()
//
//        user!!.updateProfile(profileUpdates)
        val intent = Intent(baseContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    //endregion


    override fun onAvatarSelected(name: String) {
        avatarImage.setImageDrawable(getDrawable(avatarPicker().instance.chooseAvatar(name)))
        localAvatar = true

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val fileUri = data?.data
            avatarImage.setImageURI(fileUri)

            //You can get File object from intent
            val file: File = ImagePicker.getFile(data)!!

            //You can also get File Path from intent
            filePath = ImagePicker.getFilePath(data)!!
            localAvatar = false

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
}
