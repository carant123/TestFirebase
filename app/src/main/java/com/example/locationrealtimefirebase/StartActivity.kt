package com.example.locationrealtimefirebase

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {

    lateinit var firebaseUser : FirebaseUser

    override fun onStart() {
        super.onStart()

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        if(firebaseUser != null) {
            var intent = Intent(this@StartActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        login.setOnClickListener {
            startActivity(Intent(this@StartActivity, LoginActivity::class.java))
        }

        register.setOnClickListener {
            startActivity(Intent(this@StartActivity, RegisterActivity::class.java))
        }

    }

}