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
import com.yahya.thehorn.models.WordData
import kotlinx.android.synthetic.main.activity_nva.*
import kotlinx.android.synthetic.main.layout_phrase_item.view.*

class NVAActivity : AppCompatActivity() {

    private val wordsCollectionRef = FirebaseFirestore.getInstance().collection("words")
    private var words =  mutableListOf<WordData>()
    private lateinit var mediaPlayer : MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nva)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Nouns, verbs, adjectives, ..."

        fetchWords()
    }

    private fun fetchWords() {
        wordsCollectionRef.get().addOnCompleteListener {
            it.result?.documents?.forEach { snapShot ->
                words.add(snapShot.toObject(WordData::class.java)!!)
                return@forEach
            }
            if (words.isNotEmpty()){
                progressBar.visibility = View.GONE
                contentList.layoutManager = LinearLayoutManager(this)
                contentList.adapter = WordsAdapter(this, words)
            }
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

    override fun onOptionsItemSelected(item: MenuItem?) = super.onOptionsItemSelected(item).also {
        when (item?.itemId){
            android.R.id.home -> finish()
        }
    }
}

class WordsAdapter(private val activity: NVAActivity, private val words: List<WordData>): RecyclerView.Adapter<WordVH>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = WordVH(LayoutInflater.from(parent.context).inflate(R.layout.layout_phrase_item, parent, false))

    override fun getItemCount() = words.size

    override fun onBindViewHolder(holder: WordVH, position: Int) {
        holder.bind(words[position])
        holder.itemView.listenBtn.setOnClickListener {
            (it as Button).text = "Playing..."
            it.isEnabled = false
            activity.playSound(words[position].sound, it)
        }
    }

}

class WordVH(itemView: View): RecyclerView.ViewHolder(itemView){
    fun bind(word: WordData) {
        itemView.original.text = "${word.word}"
        itemView.translated.text ="${word.translation}"
    }

}