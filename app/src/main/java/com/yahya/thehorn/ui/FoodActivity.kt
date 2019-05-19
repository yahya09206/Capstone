package com.yahya.thehorn.ui

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.firebase.firestore.FirebaseFirestore
import com.yahya.thehorn.R
import com.yahya.thehorn.models.FoodData

class FoodActivity : AppCompatActivity() {

    private val foodsCollectionRef = FirebaseFirestore.getInstance().collection("food")
    private val foods = mutableListOf<FoodData>()
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Foods"
    }

    override fun onOptionsItemSelected(item: MenuItem?) = super.onOptionsItemSelected(item).also {
        when (item?.itemId){
            android.R.id.home -> finish()
        }
    }
}
