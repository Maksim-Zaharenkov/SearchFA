package com.example.searchfa

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.room.Room
import com.example.searchfa.data.UserAuthorizationData
import com.example.searchfa.databinding.ActivityAuthorizationBinding
import com.example.searchfa.sqlite.UserDatabase
import com.example.searchfa.sqlite.UserTable
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.concurrent.thread

class AuthorizationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthorizationBinding
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthorizationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        Thread{
            val sqliteDb = UserDatabase.getDatabase(this)

            if(sqliteDb.getDao().getUser().isNotEmpty()) signIn()

        }.start()

    }

    fun init() = with(binding){
        btnSignIn.setOnClickListener {

            val userData = UserAuthorizationData(
                etName.text.toString(),
                etSurname.text.toString(),
                etStudnetNum.text.toString()
            )

            checkUser(userData)
        }
    }

    private fun checkUser(userData: UserAuthorizationData) {
        var check = false

        db.collection("users")
            .whereEqualTo("code", userData.code)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    check = true
                    if (userData.name == document.data["name"]
                        && userData.surname == document.data["surname"]){
                        check = true

                        val itemUserTable = UserTable(
                            null,
                            userData.name,
                            userData.surname,
                            userData.code
                        )

                        Thread{
                            val sqliteDb = UserDatabase.getDatabase(this)
                            sqliteDb.getDao().insertUser(itemUserTable)
                        }.start()

                        signIn()
                    }
                    else check = false
                }
                if (!check) binding.tvHelp.visibility = View.VISIBLE
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    private fun signIn(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}