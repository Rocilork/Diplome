package com.example.electronicmagazine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class Administrator : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_administrator)
    }

    fun onBack (view: View){
        val intent = Intent(this, Avtorizathion::class.java)
        startActivity(intent)
    }
    fun onListCurators (view: View){
        val intent = Intent(this, ListCurators::class.java)
        startActivity(intent)
    }
    fun onCreateGroup (view: View){
        val intent = Intent(this, CreateEditGroup::class.java)
        startActivity(intent)
    }
    fun onGroups (view: View){
        val intent = Intent(this, Groups::class.java)
        startActivity(intent)
    }
}