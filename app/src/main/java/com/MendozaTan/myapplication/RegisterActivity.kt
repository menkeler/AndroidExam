package com.MendozaTan.myapplication


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.MendozaTan.myapplication.MainActivity
import com.MendozaTan.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var _btnRegister: Button
    private lateinit var _textName: EditText
    private lateinit var _textPass: EditText
    private lateinit var _textEmail: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        _textName = findViewById(R.id.textDisplayName)
        _textPass = findViewById(R.id.textPassword)
        _textEmail = findViewById(R.id.textEmail)

        auth = FirebaseAuth.getInstance()

        _btnRegister = findViewById(R.id.btnRegister)
        _btnRegister.setOnClickListener {
            val displayName = _textName.text.toString()
            val email = _textEmail.text.toString()
            val password = _textPass.text.toString()

            if (displayName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                // Call your signInAccount function with the provided parameters
                signInAccount(displayName, email, password)
            } else {
                Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInAccount(displayName: String, email: String, password: String) {
        val TAG = "Firebase"
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    // You might want to update the UI or navigate to another activity here
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Register failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    // Update UI to reflect the authentication failure
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        // You can implement your UI update logic here
        // For example, navigate to the main activity if the user is not null
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Optional: finish the current activity
        }
    }
}
