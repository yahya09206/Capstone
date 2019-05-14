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
import com.yahya.thehorn.models.PhraseData
import kotlinx.android.synthetic.main.activity_phrases.*
import kotlinx.android.synthetic.main.layout_phrase_item.view.*

class PhrasesActivity : AppCompatActivity() {

    private val phrasesCollectionRef = FirebaseFirestore.getInstance().collection("phrases")
    private var phrases =  mutableListOf<PhraseData>()
    private lateinit var mediaPlayer : MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phrases)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Phrases"

        fetchPhrases()
    }

    private fun fetchPhrases() {
        phrasesCollectionRef.get().addOnCompleteListener {
            it.result?.documents?.forEach { snapShot ->
                phrases.add(snapShot.toObject(PhraseData::class.java)!!)
                return@forEach
            }
            if (phrases.isNotEmpty()){
                progressBar.visibility = View.GONE
                contentList.layoutManager = LinearLayoutManager(this)
                contentList.adapter = PhrasesAdapter(this, phrases)
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

class PhrasesAdapter(private val activity: PhrasesActivity, private val phrases: List<PhraseData>): RecyclerView.Adapter<PhraseVH>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PhraseVH(LayoutInflater.from(parent.context).inflate(R.layout.layout_phrase_item, parent, false))

    override fun getItemCount() = phrases.size

    override fun onBindViewHolder(holder: PhraseVH, position: Int) {
        holder.bind(phrases[position])
        holder.itemView.listenBtn.setOnClickListener {
            (it as Button).text = "Playing..."
            it.isEnabled = false
            activity.playSound(phrases[position].sound, it)
        }
    }

}

class PhraseVH(itemView: View): RecyclerView.ViewHolder(itemView){
    fun bind(phrase: PhraseData) {
        itemView.original.text = "${phrase.original}"
        itemView.translated.text ="${phrase.translation}"
    }

}