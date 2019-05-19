package com.yahya.thehorn.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.yahya.thehorn.R
import com.yahya.thehorn.models.FoodData
import kotlinx.android.synthetic.main.activity_numbers.*
import kotlinx.android.synthetic.main.layout_food_item.view.*

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
            return@setOnErrorListener true
        }
        mediaPlayer.setOnPreparedListener {it.start()}
        mediaPlayer.setOnCompletionListener{
            it.release()
            (btn as Button).text = "Listen"
            btn.isEnabled = true
        }

    }

    class FoodsAdapter(private val activity: FoodActivity, private val foods: MutableList<FoodData>) : RecyclerView.Adapter<FoodVH>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FoodVH(LayoutInflater.from(parent.context).inflate(R.layout.layout_food_item, parent, false))

        override fun getItemCount() = foods.size

        override fun onBindViewHolder(holder: FoodVH, position: Int) {
            holder.bind(foods[position])
            holder.itemView.listenBtn.setOnClickListener {
                (it as Button).text = "Playing..."
                it.isEnabled = false
                activity.playSound(foods[position].sound, it)
            }
        }

    }

    class FoodVH(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(foodData: FoodData) {
            Glide.with(itemView.context)
                    .load(foodData.image)
                    .placeholder(R.drawable.ic_loading)
                    .into(itemView.foodImg)
            itemView.foodName.text = foodData.name
            itemView.foodTranslation.text = foodData.translation
        }
    }

}
