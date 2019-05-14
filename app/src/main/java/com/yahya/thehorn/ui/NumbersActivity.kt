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
import com.google.firebase.firestore.FirebaseFirestore
import com.yahya.thehorn.R
import com.yahya.thehorn.models.NumberData
import kotlinx.android.synthetic.main.activity_numbers.*
import kotlinx.android.synthetic.main.layout_number_item.view.*

class NumbersActivity : AppCompatActivity() {

    private val numbersCollectionRef = FirebaseFirestore.getInstance().collection("numbers")
    private var numbers =  mutableListOf<NumberData>()
    private lateinit var mediaPlayer : MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_numbers)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Numbers"

        fetchNumbers()
    }

    private fun fetchNumbers() {
        numbersCollectionRef.get().addOnCompleteListener {
            it.result?.documents?.forEach { snapshot ->
                numbers.add(snapshot.toObject(NumberData::class.java)!!)
                return@forEach
            }
            if (numbers.isNotEmpty()){
                progressBar.visibility = View.GONE
                contentList.layoutManager = LinearLayoutManager(this)
                contentList.adapter = NumbersAdapter(this, numbers.reversed())
            }

        }
    }


    override fun onOptionsItemSelected(item: MenuItem?) = super.onOptionsItemSelected(item).also {
        when (item?.itemId){
            android.R.id.home -> finish()
        }
    }

    fun playSound(soundURL: String, btn: View) {
        mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(soundURL)
        mediaPlayer.prepare()
        mediaPlayer.setOnErrorListener { mp, _, _ ->
            mp.release()
            Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show()
            return@setOnErrorListener true
        }
        mediaPlayer.setOnPreparedListener { it.start() }
        mediaPlayer.setOnCompletionListener {
            it.release()
            (btn as Button).text = "Listen"
            btn.isEnabled = true
        }
    }

}

class NumbersAdapter(private val activity: NumbersActivity, private val numbers: List<NumberData>): RecyclerView.Adapter<NumbersVH>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NumbersVH(LayoutInflater.from(parent.context).inflate(R.layout.layout_number_item, parent, false))

    override fun getItemCount() = numbers.size

    override fun onBindViewHolder(holder: NumbersVH, position: Int) {
        holder.bind(numbers[position])
        holder.itemView.listenBtn.setOnClickListener {
            (it as Button).text = "Playing..."
            it.isEnabled = false
            activity.playSound(numbers[position].sound, it)
        }
    }

}

class NumbersVH(itemView: View): RecyclerView.ViewHolder(itemView){
    fun bind(number: NumberData) {
        itemView.numberDisplay.text = "${number.number}"
        itemView.numberTranslation.text = number.translation
    }

}
