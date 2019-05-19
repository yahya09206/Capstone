package com.yahya.thehorn.ui

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
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

        fetchFoods()
    }

    private fun fetchFoods(){
        foodsCollectionRef.get().addOnCompleteListener {
            it.result?.documents?.forEach { snapshot ->
                foods.add(snapshot.toObject(FoodData::class.java)!!)
                return@forEach
            }
            if(foods.isNotEmpty()){
                progressBar.visibility = View.GONE
                contentList.layoutManager = LinearLayoutManager(this)
                contentList.adapter = FoodsAdapter(this, foods)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?) = super.onOptionsItemSelected(item).also {
        when (item?.itemId){
            android.R.id.home -> finish()
        }
    }

    fun playSound(soundURL: String, btn: View){
        mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(soundURL)
        mediaPlayer.prepare()
        mediaPlayer.setOnErrorListener { mp, _, _ ->
            mp.release()
            Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show()
            return@setonErrorListener true
        }
        mediaPlayer.setOnPreparedListener {it.start()}
        mediaPlayer.setOnCompletionListener{
            it.release()
            (btn as Button).text = "Listen"
            btn.isEnabled = true
        }

    }
}
