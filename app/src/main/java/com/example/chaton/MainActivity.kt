package com.example.chaton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chaton.fragments.ChatFragment
import com.example.chaton.fragments.HomeFragment
import com.example.chaton.fragments.LoginFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currAuthInst: FirebaseAuth = FirebaseAuth.getInstance()
        if(currAuthInst.currentUser == null) {
            supportFragmentManager.beginTransaction().replace(
                R.id.home_fragment,LoginFragment()
            ).commit()
        } else {
            supportFragmentManager.beginTransaction().replace(
                R.id.home_fragment,HomeFragment()
            ).commit()
        }
    }

    fun moveToChats(givenNum: String) {
        supportFragmentManager.beginTransaction().replace(
            R.id.home_fragment, ChatFragment(givenNum)
        ).commit()
    }
}