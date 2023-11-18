package com.MendozaTan.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {


    lateinit var auth: FirebaseAuth
    lateinit var _btnLogin: Button
    lateinit var _btnRegister: Button
    lateinit var _textName: EditText
    lateinit var _textPass: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        _btnRegister = findViewById(R.id.btnRegister)
        _btnLogin = findViewById(R.id.btnLogin)
        _textName = findViewById(R.id.textDisplayName)
        _textPass = findViewById(R.id.textPassword)

       _btnLogin.setOnClickListener {

            val email = _textName.text.toString()
            val pass = _textPass.text.toString()

           if(email.equals("")||pass.equals("")){
                   Toast.makeText(this, "Required Fields", Toast.LENGTH_SHORT).show()
           }else{
               signInAccount(email,pass)

           }
       }
        _btnRegister.setOnClickListener {
            var intent =Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        // This function is meant to update the UI based on the authentication state.

        if (user != null) {
            var intent =Intent(this,MainActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(
                baseContext, "You Did not Login", Toast.LENGTH_SHORT,).show()
        }
    }

    override fun onResume() {
        super.onResume()
        _textName.setText("")
        _textPass.setText("")
    }
    public override fun onStart() {
        super.onStart()
        _textName.setText("")
        _textPass.setText("")
        val currentUser = auth.currentUser
        if (currentUser != null) {
            updateUI(currentUser)
        }
    }
    fun signInAccount(email: String, password: String) {
        val TAG = "Firebase"
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                }
            }
    }




}