package com.example.dailyexpenses.repository

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.dailyexpenses.api.ApiResponse
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging

class FirebaseRepository() {
    companion object{
        fun saveToken(userLogin: String){
            val database = FirebaseDatabase.getInstance()
            val rootRef = database.reference
            val query = rootRef.child("Tokens").equalTo(userLogin)
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()){
                        val reference = database.getReference("Tokens").child(userLogin)
                        FirebaseMessaging.getInstance().token.addOnCompleteListener{ task ->
                            val token = task.result
                            val tokenMap = mapOf("token" to token)
                            reference.setValue(tokenMap).addOnCompleteListener{task ->
                                Log.d("Token", token)
                            }
                        }.addOnSuccessListener {
                        }.addOnFailureListener {
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

}